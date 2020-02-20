package ro.marc.chatapp.viewmodel.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ro.marc.chatapp.model.AuthModel
import ro.marc.chatapp.model.BlockModel
import ro.marc.chatapp.model.FirestoreUser
import ro.marc.chatapp.utils.FirestoreRepository

class FirestoreViewModel: ViewModel() {
    private val firestoreRepository = FirestoreRepository()

    var fetchedUser: LiveData<FirestoreUser?>? = null
    fun getUser() {
        fetchedUser = firestoreRepository.getUser()
    }

    var fetchedOtherUser: LiveData<FirestoreUser?>? = null
    fun getUser(uid: String) {
        fetchedOtherUser = firestoreRepository.getUser(uid)
    }

    fun block(data: BlockModel, data2: BlockModel) {
        firestoreRepository.blockUser(data, data2)
    }

    var createdFirestoreUser: LiveData<AuthModel>? = null
    fun createUserInFirestore(authenticatedUser: FirestoreUser) {
        createdFirestoreUser = firestoreRepository.createUserInFirestoreIfNotExists(authenticatedUser)
    }
}