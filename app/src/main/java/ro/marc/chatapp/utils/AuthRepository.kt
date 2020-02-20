package ro.marc.chatapp.utils

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import ro.marc.chatapp.model.db.AuthModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class AuthRepository {
    private val TAG = "ChatApp AuthRepository"

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val facebookLoginManager: LoginManager = LoginManager.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient

    fun logOut(context: Activity): MutableLiveData<String> {
        val loggedOut = MutableLiveData<String>()

        val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

        try {
            firebaseAuth.signOut()
            facebookLoginManager.logOut()
            googleSignInClient.signOut()
            loggedOut.value = ""
        } catch (e: Exception) {
            loggedOut.value = e.message
        }
        return loggedOut
    }

    fun getLoggedUserUid(): MutableLiveData<String?> {
        val isUserLoggedIn = MutableLiveData<String?>()
        isUserLoggedIn.value = firebaseAuth.currentUser?.uid
        return isUserLoggedIn
    }

    fun getUidOfCurrentUser(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun signUpUser(email: String, password: String): MutableLiveData<AuthModel> {
        val signedUpUser = MutableLiveData<AuthModel>()
        lateinit var data: AuthModel

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                println("intrat pe createuser with email onsucces")
                val firebaseUser = firebaseAuth.currentUser
                data = AuthModel(
                    firebaseUser!!.uid,
                    firebaseUser.email,
                    firebaseUser.displayName,
                    null,
                    true
                )
                signedUpUser.value = data
            } else {
                Log.d(TAG, "sign up cu email & parola esuat: ${it.exception!!.message}")
                data = AuthModel()
                data.error = it.exception!!.message
                signedUpUser.value = data
            }
        }
        return signedUpUser
    }

    fun login(email: String, password: String): MutableLiveData<AuthModel> {
        val loggedUser = MutableLiveData<AuthModel>()
        lateinit var data: AuthModel

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                println("user logat cu uid ${firebaseAuth.currentUser!!.uid}")
                val firebaseUser = firebaseAuth.currentUser
                data = AuthModel(
                    firebaseUser!!.uid,
                    firebaseUser.email,
                    firebaseUser.displayName,
                    null,
                    false
                )
                loggedUser.value = data
            } else {
                println("logare esuata cu ${it.exception!!}")
                data = AuthModel()
                data.error = it.exception!!.message
                loggedUser.value = data
            }
        }
        return loggedUser
    }

    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<AuthModel> {
        val authenticatedUserMutableLiveData = MutableLiveData<AuthModel>()
        lateinit var data: AuthModel

        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                val firebaseUser = firebaseAuth.currentUser
                data = AuthModel(
                    firebaseUser!!.uid,
                    firebaseUser.email,
                    firebaseUser.displayName,
                    null,
                    authTask.result!!.additionalUserInfo!!.isNewUser
                )
                authenticatedUserMutableLiveData.value = data
            } else {
                data = AuthModel()
                data.error = authTask.exception!!.message!!
                Log.d(TAG, "eroare la signin cu google: ${authTask.exception!!.message!!}")
                authenticatedUserMutableLiveData.value = data
            }
        }
        return authenticatedUserMutableLiveData
    }

    fun handleFacebookAccessToken(token: AccessToken): MutableLiveData<AuthModel> {
        val user = MutableLiveData<AuthModel>()
        lateinit var data: AuthModel

        val credential: AuthCredential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                data = AuthModel(
                    firebaseAuth.currentUser!!.uid,
                    firebaseAuth.currentUser!!.email,
                    firebaseAuth.currentUser!!.displayName,
                    null,
                    task.result!!.additionalUserInfo!!.isNewUser
                )
                user.value = data
            } else {
                Log.w(TAG, "signin cu facebook esuat: ${task.exception}")
                data = AuthModel()
                data.error = task.exception.toString()
                user.value = data
            }

        }
        return user
    }
}