package ro.marc.chatapp.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import ro.marc.chatapp.model.FirestoreUser
import com.google.firebase.firestore.FirebaseFirestore
import ro.marc.chatapp.model.RegisterModel

class AuthRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection("users")

    val TAG = "ChatApp AuthRepository"

    fun getLoggedUserUid(): MutableLiveData<String?> {
        val isUserLoggedIn = MutableLiveData<String?>()
        isUserLoggedIn.value = firebaseAuth.currentUser?.uid
        return isUserLoggedIn
    }

    fun signUpUser(email: String, password: String): MutableLiveData<RegisterModel> {
        val signedUpUser = MutableLiveData<RegisterModel>()
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val firebaseUser = firebaseAuth.currentUser
                signedUpUser.value = RegisterModel(firebaseUser!!.uid, "", email, password, "", "")
            } else {
                Log.d(TAG, "sign up cu email & parola esuat: ${it.exception!!.message}")
            }
        }
        return signedUpUser
    }

    fun login(email: String, password: String): MutableLiveData<RegisterModel> {
        val loggedUser = MutableLiveData<RegisterModel>()
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                println("user logat cu uid ${firebaseAuth.currentUser!!.uid}")
                loggedUser.value = RegisterModel(firebaseAuth.currentUser!!.uid, "", email, password, "", "")
            } else {
                println("logare esuata cu ${it.exception!!}")
            }
        }
        return loggedUser
    }

    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<RegisterModel> {
        val authenticatedUserMutableLiveData = MutableLiveData<RegisterModel>()
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                val isNewUser = authTask.result!!.additionalUserInfo!!.isNewUser
                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val name = firebaseUser.displayName
                    val email = firebaseUser.email
                    val user = RegisterModel(uid, "", email, "", name, "")
                    user.isNew = isNewUser
                    authenticatedUserMutableLiveData.value = user
                }
            } else {
                println(authTask.exception!!.message)
            }
        }
        return authenticatedUserMutableLiveData
    }

    fun createUserInFirestoreIfNotExists(authenticatedUser: FirestoreUser): MutableLiveData<FirestoreUser> {
        val newUserMutableLiveData = MutableLiveData<FirestoreUser>()
        val uidRef: DocumentReference = usersRef.document(authenticatedUser.uid)
        uidRef.get().addOnCompleteListener { uidTask ->
            if (uidTask.isSuccessful) {
                val document: DocumentSnapshot? = uidTask.result
                if (!document!!.exists()) {
                    println("document nu exista")
                    uidRef.set(authenticatedUser).addOnCompleteListener { userCreationTask ->
                        if (userCreationTask.isSuccessful) {
                            authenticatedUser.isCreated = true
                            newUserMutableLiveData.value = authenticatedUser
                        } else {
                            println(userCreationTask.exception!!.message)
                        }
                    }
                } else {
                    newUserMutableLiveData.value = authenticatedUser
                }
            } else {
                println(uidTask.exception!!.message)
            }
        }
        return newUserMutableLiveData
    }
}