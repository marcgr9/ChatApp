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

    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        authenticatedUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential)
    }

    fun createUser(authenticatedUser: FirestoreUser) {
        println("test in createUser ${authenticatedUser.id}")
        createdUserLiveData = authRepository.createUserInFirestoreIfNotExists(authenticatedUser)
    }
}