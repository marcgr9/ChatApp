package ro.marc.chatapp.viewmodel.db

import android.app.Activity
import com.google.firebase.auth.AuthCredential
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import ro.marc.chatapp.model.AuthModel
import ro.marc.chatapp.model.BlockModel
import ro.marc.chatapp.model.FirestoreUser
import ro.marc.chatapp.model.RegisterModel
import ro.marc.chatapp.utils.AuthRepository

class AuthViewModel: ViewModel() {
    private val authRepository: AuthRepository = AuthRepository()

    var signedInWithGoogleUser: LiveData<AuthModel>? = null
    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        signedInWithGoogleUser = authRepository.firebaseSignInWithGoogle(googleAuthCredential)
    }

    var signedUpUser: LiveData<AuthModel>? = null
    fun signUpUser(email: String, password: String) {
        signedUpUser = authRepository.signUpUser(email, password);
    }

    var loggedUser: LiveData<AuthModel>? = null
    fun loginUser(email: String, password: String) {
        loggedUser = authRepository.login(email, password)
    }

    var loggedUserUid: LiveData<String?>? = null
    fun checkIfUserIsLoggedIn() {
        loggedUserUid = authRepository.getLoggedUserUid()
    }

    var signedInWithFacebookUser: LiveData<AuthModel>? = null
    fun handleFacebook(token: AccessToken) {
        signedInWithFacebookUser = authRepository.handleFacebookAccessToken(token)
    }

    var loggedOut: LiveData<String>? = null
    fun logOut(activity: Activity) {
        loggedOut = authRepository.logOut(activity)
    }


}