package ro.marc.chatapp.viewmodel

import com.google.firebase.auth.AuthCredential
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import ro.marc.chatapp.model.FirestoreUser
import ro.marc.chatapp.model.RegisterModel
import ro.marc.chatapp.utils.AuthRepository

class AuthViewModel: ViewModel() {
    private val authRepository: AuthRepository = AuthRepository()

    var signedInWithGoogleUser: LiveData<RegisterModel>? = null
    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        signedInWithGoogleUser = authRepository.firebaseSignInWithGoogle(googleAuthCredential)
    }

    var createdFirestoreUser: LiveData<FirestoreUser>? = null
    fun createUserInFirestore(authenticatedUser: FirestoreUser) {
        createdFirestoreUser = authRepository.createUserInFirestoreIfNotExists(authenticatedUser)
    }

    var signedUpUser: LiveData<RegisterModel>? = null
    fun signUpUser(email: String, password: String) {
        signedUpUser = authRepository.signUpUser(email, password);
    }

    var loggedUser: LiveData<RegisterModel>? = null
    fun loginUser(email: String, password: String) {
        loggedUser = authRepository.login(email, password)
    }

    var loggedUserUid: LiveData<String?>? = null
    fun checkIfUserIsLoggedIn() {
        loggedUserUid = authRepository.getLoggedUserUid()
    }

    var signedInWithFacebookUser: LiveData<RegisterModel>? = null
    fun handleFacebook(token: AccessToken) {
        signedInWithFacebookUser = authRepository.handleFacebookAccessToken(token)
    }
}