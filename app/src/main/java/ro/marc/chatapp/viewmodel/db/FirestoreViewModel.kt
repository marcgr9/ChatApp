package ro.marc.chatapp.viewmodel.db

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ro.marc.chatapp.model.db.AuthModel
import ro.marc.chatapp.model.db.BlockData
import ro.marc.chatapp.model.db.FirestoreUser
import ro.marc.chatapp.utils.FirestoreRepository

class FirestoreViewModel: ViewModel() {
    private val firestoreRepository = FirestoreRepository()

    var fetchedUser: LiveData<FirestoreUser?>? = null
    fun getUser() {
        fetchedUser = firestoreRepository.getUser()
    }

    var fetchedOtherUser: LiveData<FirestoreUser>? = null
    fun getUser(uid: String) {
        fetchedOtherUser = firestoreRepository.getUser(uid)
    }

    var blocked: LiveData<Boolean>? = null
    fun checkIfBlocked(uidUser: String, uidBlockedUser: String) {
        blocked = firestoreRepository.checkIfBlocked(uidUser, uidBlockedUser)
    }

    var blockedStatus: LiveData<String?>? = null
    fun changeBlockedStatus(data: BlockData, data2: BlockData, mode: Int) {
        blockedStatus = firestoreRepository.changeBlockedStatus(data, data2, mode)
    }

    var createdFirestoreUser: LiveData<AuthModel>? = null
    fun createUserInFirestore(authenticatedUser: FirestoreUser) {
        createdFirestoreUser = firestoreRepository.createUserInFirestoreIfNotExists(authenticatedUser)
    }
}