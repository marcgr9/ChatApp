package ro.marc.chatapp.utils

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import ro.marc.chatapp.model.db.AuthModel
import ro.marc.chatapp.model.db.BlockData
import ro.marc.chatapp.model.db.FirestoreUser

class FirestoreRepository {
    private val TAG = "ChatApp FirestoreRepository"

    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val usersRef: CollectionReference = rootRef.collection("users")
    private val idsRef: CollectionReference = rootRef.collection("ids")
    private val blocksRef: CollectionReference = rootRef.collection("blocks")
    private val friendsRef: CollectionReference = rootRef.collection("friends")
    private val unusedFilesRef: CollectionReference = rootRef.collection("unusedFiles")

    private val friendsWithC = "friendsWith"
    private val pendingSentRequestsC = "pendingSentRequests"
    private val pendingReceivedRequestsC = "pendingReceivedRequests"

    private val blockedBy = "blockedBy"
    private val blocking = "blocking"


    fun checkFriendshipStatus(uidUser: String, uidFriend: String): MutableLiveData<Int> {
        // 1 - prieteni
        // 0 - user-ul i-a trimis cerere
        //-1 - user-ul a primit cerere
        //-2 - nimic
        val response = MutableLiveData<Int>()

        friendsRef.document(uidUser).collection(friendsWithC).document(uidFriend).get()
            .addOnSuccessListener { it1 ->
                friendsRef.document(uidUser).collection(pendingSentRequestsC).document(uidFriend).get()
                    .addOnSuccessListener { it2 ->
                        friendsRef.document(uidUser).collection(pendingReceivedRequestsC).document(uidFriend).get()
                            .addOnSuccessListener { it3 ->
                                if (!(it1.exists() || it2.exists() || it3.exists())) response.value = -2
                                else if (it1.exists()) response.value = 1
                                else if (it2.exists()) response.value = 0
                                else if (it3.exists()) response.value = -1
                            }.addOnFailureListener {
                                Log.d(TAG, "eroare la verificare prietenie (-1): ${it.message}")
                            }
                    }.addOnFailureListener {
                        Log.d(TAG, "eroare la verificare prietenie (0): ${it.message}")
                    }
            }.addOnFailureListener {
                Log.d(TAG, "eroare la verificare prietenie (1): ${it.message}")
            }

        return response
    }

    fun editFriendship(dataUser: BlockData, dataFriend: BlockData, mode: Int, action: Int): MutableLiveData<String> {
        // nu testam daca sunt blocati pentru ca asta se va face in search

        // mode:
        // 1 - prieteni -> se sterge prietenia
        // 0 - user-ul i-a trimis cerere -> se sterge cererea
        //-1 - user-ul are cerere de la friend ->
        //       action: 1 -> accepta cererea
        //       action: 0 -> respinge cererea
        //-2 - nimic -> se trimite cerere de la user la friemd
        val response = MutableLiveData<String>()

        val userRef: DocumentReference = friendsRef.document(dataUser.uid)
        val friendRef: DocumentReference = friendsRef.document(dataFriend.uid)

        rootRef.runBatch {
            if (mode == 1) { // prieteni -> sterge prietenia
                it.delete(userRef.collection(friendsWithC).document(dataFriend.uid))
                it.delete(friendRef.collection(friendsWithC).document(dataUser.uid))
            } else if (mode == 0) { // user-ul i-a trimis cererea -> stergem cererea
                it.delete(userRef.collection(pendingSentRequestsC).document(dataFriend.uid))
                it.delete(friendRef.collection(pendingReceivedRequestsC).document(dataUser.uid))
            } else if (mode == -1) { // user-ul a primit cerere
                if (action == 1) { // cerere acceptata
                    it.delete(userRef.collection(pendingReceivedRequestsC).document(dataFriend.uid))
                    it.delete(friendRef.collection(pendingSentRequestsC).document(dataUser.uid))
                    it.set(
                        userRef.collection(friendsWithC).document(dataFriend.uid),
                        dataFriend
                    )
                    it.set(friendRef.collection(friendsWithC).document(dataUser.uid), dataUser)
                } else if (action == 0) { // cerere respinsa
                    it.delete(userRef.collection(pendingReceivedRequestsC).document(dataFriend.uid))
                    it.delete(friendRef.collection(pendingSentRequestsC).document(dataUser.uid))
                }
            } else { // trimitem cerere de la user la friend
                it.set(userRef.collection(pendingSentRequestsC).document(dataFriend.uid), dataFriend)
                it.set(friendRef.collection(pendingReceivedRequestsC).document(dataUser.uid), dataUser)
            }
        }.addOnSuccessListener {
            response.value = "succes"
        }.addOnFailureListener {
            response.value = it.message
            Log.d(TAG, "eroare la schimbare status prietenie ($mode $action): $it")
        }

        return response
    }


    fun changeBlockedStatus(dataUser: BlockData, dataBlockedUser: BlockData, mode: Int = 0): MutableLiveData<String> {
        // mode: 0 -> block
        // mode: 1 -> unblock

        val response = MutableLiveData<String>()

        val uidBlockedByRef: DocumentReference = blocksRef.document(dataBlockedUser.uid).collection(blockedBy).document(dataUser.uid)
        val uidBlockingRef: DocumentReference = blocksRef.document(dataUser.uid).collection(blocking).document(dataBlockedUser.uid)

        val userFriendsRef: DocumentReference = friendsRef.document(dataUser.uid)
        val blockedUserFriendsRef: DocumentReference = friendsRef.document(dataBlockedUser.uid)

        rootRef.runBatch { batch ->// trebuie sa se efectueze ambele operatii ori niciuna
            if (mode == 0) { // block
                batch.set(uidBlockedByRef, dataUser)
                batch.set(uidBlockingRef, dataBlockedUser)

                batch.delete(userFriendsRef.collection(friendsWithC).document(dataBlockedUser.uid))
                batch.delete(blockedUserFriendsRef.collection(friendsWithC).document(dataUser.uid))

                batch.delete(userFriendsRef.collection(pendingSentRequestsC).document(dataBlockedUser.uid))
                batch.delete(blockedUserFriendsRef.collection(pendingSentRequestsC).document(dataUser.uid))

                batch.delete(userFriendsRef.collection(pendingReceivedRequestsC).document(dataBlockedUser.uid))
                batch.delete(blockedUserFriendsRef.collection(pendingReceivedRequestsC).document(dataUser.uid))

            } else { // unblock
                batch.delete(uidBlockedByRef)
                batch.delete(uidBlockingRef)
            }
        }.addOnCompleteListener {
            response.value = "succes"
        }.addOnFailureListener {
            response.value = it.message

            val cuvant = if (mode == 0) "blocare" else "deblocare"
            Log.d(TAG, "Eroare la $cuvant: ${it.message}")
        }

        return response
    }

    fun checkIfBlocked(uidUser: String, uidBlockedUser: String): MutableLiveData<Boolean> {
        val response = MutableLiveData<Boolean>()

        val ref: DocumentReference = blocksRef.document(uidUser).collection(blocking).document(uidBlockedUser)
        ref.get().addOnCompleteListener {
            val document = it.result
            response.value = document!!.exists()
        }.addOnFailureListener {
            Log.d(TAG, "Eroare la verificarea daca user-ul e (de)blocat")
        }
        return response
    }

    fun getUser(uid: String): MutableLiveData<FirestoreUser> {
        val user = MutableLiveData<FirestoreUser>()

        val userRef: DocumentReference = usersRef.document(uid)
        userRef.get().addOnCompleteListener {
            user.value = it.result!!.toObject(FirestoreUser::class.java)
        }.addOnFailureListener {
            Log.d(TAG, "Eroare la getUser cu uid: ${it.message}")
            val data = FirestoreUser()
            data.uid = ""
            user.value = data
        }
        return user
    }

    fun getUser(): MutableLiveData<FirestoreUser> {
        val user = MutableLiveData<FirestoreUser>()

        /// TODO get uid of user in another way

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val userRef: DocumentReference = usersRef.document(uid)
        userRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                user.value = it.result!!.toObject(FirestoreUser::class.java)
            } else {
                println(it.exception!!.message)
                user.value = FirestoreUser()
            }
        }
        return user
    }

    fun createUserInFirestoreIfNotExists(authenticatedUser: FirestoreUser): MutableLiveData<AuthModel> {
        val uidRef: DocumentReference = usersRef.document(authenticatedUser.uid)

        val newUserMutableLiveData = MutableLiveData<AuthModel>()
        lateinit var data: AuthModel

        uidRef.get().addOnCompleteListener { uidTask ->
            if (uidTask.isSuccessful) {
                val document: DocumentSnapshot? = uidTask.result
                if (!document!!.exists()) {
                    rootRef.runBatch {
                        uidRef.set(authenticatedUser)
                        idsRef.document(authenticatedUser.id!!).set(hashMapOf("uid" to authenticatedUser.uid))

                    }.addOnCompleteListener {
                        data = AuthModel(
                            authenticatedUser.uid,
                            authenticatedUser.email,
                            authenticatedUser.name,
                            null, true
                        )
                        newUserMutableLiveData.value = data
                    }.addOnFailureListener {
                        data = AuthModel()
                        data.error = it.message
                        Log.d(TAG, "eroare la setare date in firestore: ${it.message}")
                        newUserMutableLiveData.value = data
                    }
                } else {
                    data = AuthModel(
                        authenticatedUser.uid,
                        authenticatedUser.email,
                        authenticatedUser.name,
                        null,
                        false
                    )
                    newUserMutableLiveData.value = data
                }
            } else {
                data = AuthModel()
                data.error = uidTask.exception!!.message
                Log.d(TAG, "eroare la creare in firestore ${uidTask.exception!!.message!!}")
                newUserMutableLiveData.value = data
            }

        }
        return newUserMutableLiveData
    }

    fun isIdAvailable(id: String): MutableLiveData<Boolean> {
        val response = MutableLiveData<Boolean>()

        idsRef.document(id).get()
            .addOnCompleteListener {
                response.value = it.result!!.exists()
            }.addOnFailureListener {
                response.value = true
                Log.d(TAG, "eroare la veriricare id unic: ${it.message}")
            }

        return response
    }

    fun userHasProfileCompleted(uid: String): MutableLiveData<Boolean> {
        val response = MutableLiveData<Boolean>()
        //response.value = true

        usersRef.document(uid).get()
            .addOnCompleteListener {
                response.value = it.result!!.exists()
            }.addOnFailureListener {
                Log.d(TAG, "eroare la verificare profil complet: ${it.message}")
            }

        return response
    }

    fun setImage(uid: String, uri: String): MutableLiveData<String> {
        val response = MutableLiveData<String>()

        usersRef.document(uid).update("profileUri", uri)
            .addOnSuccessListener {
                response.value = ""
            }.addOnFailureListener {
                response.value = it.message
            }

        return response
    }

    fun addUnusedImage(uid: String): MutableLiveData<String> {
        val response = MutableLiveData<String>()

        unusedFilesRef.document(uid).set(hashMapOf("uid" to uid))
            .addOnCompleteListener {
                response.value = ""
            }.addOnFailureListener {
                response.value = it.message
                Log.d(TAG, "eroare la adaugare poza nefolosita: ${it.message}")
            }

        return response
    }

}