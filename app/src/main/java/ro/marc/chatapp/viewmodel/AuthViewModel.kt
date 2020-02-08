package ro.marc.chatapp.viewmodel

import com.google.firebase.auth.AuthCredential
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ro.marc.chatapp.model.FirestoreUser
import ro.marc.chatapp.model.RegisterModel
import ro.marc.chatapp.utils.AuthRepository

class AuthViewModel: ViewModel() {
    private val authRepository: AuthRepository = AuthRepository()
    var authenticatedUserLiveData: LiveData<RegisterModel>? = null

    var createdUserLiveData: LiveData<FirestoreUser>? = null

    var signedUpUser: LiveData<RegisterModel>? = null

    var loggedUser: LiveData<RegisterModel>? = null

    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        authenticatedUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential)
    }

    fun createUser(authenticatedUser: FirestoreUser) {
        println("create user")
        createdUserLiveData = authRepository.createUserInFirestoreIfNotExists(authenticatedUser)
    }

    fun signUpUser(email: String, password: String) {
        println("sign uo cu $email si $password")
        signedUpUser = authRepository.signUpUser(email, password);
    }

    fun loginUser(email: String, password: String) {
        println("login cu $email")
        loggedUser = authRepository.login(email, password)
    }
}