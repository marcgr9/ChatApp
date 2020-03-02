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

    var idAvailable: LiveData<Boolean>? = null
    fun checkIfIdAvailable(id: String) {
        idAvailable = firestoreRepository.isIdAvailable(id)
    }

    var profileCompleted: LiveData<Boolean>? = null
    fun checkIfProfileCompleted(uid: String) {
        profileCompleted = firestoreRepository.userHasProfileCompleted(uid)
    }

    var friendshipStatus: LiveData<Int>? = null
    fun checkFriendship(uidUser: String, uidFriend: String) {
        friendshipStatus = firestoreRepository.checkFriendshipStatus(uidUser, uidFriend)
    }

    var changedFriendship: LiveData<String>? = null
    fun changeFriendship(user: BlockData, friend: BlockData, mode: Int, action: Int) {
        changedFriendship = firestoreRepository.editFriendship(user, friend, mode, action)
    }

    var imageUpdated: LiveData<String>? = null
    fun setImage(uid: String, uri: String) {
        imageUpdated = firestoreRepository.setImage(uid, uri)
    }

    var fileAdded: LiveData<String>? = null
    fun addUnusedFile(uid: String) {
        fileAdded = firestoreRepository.addUnusedImage(uid)
    }
}