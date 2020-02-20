package ro.marc.chatapp.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import ro.marc.chatapp.model.db.AuthModel
import ro.marc.chatapp.model.db.BlockData
import ro.marc.chatapp.model.db.FirestoreUser

class FirestoreRepository {
    private val TAG = "ChatApp FirestoreRepository"

    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val usersRef: CollectionReference = rootRef.collection("users")
    private val idsRef: CollectionReference = rootRef.collection("ids")
    private val blocksRef: CollectionReference = rootRef.collection("blocks")

    fun changeBlockedStatus(dataUser: BlockData, dataBlockedUser: BlockData, mode: Int = 0): MutableLiveData<String> {
        // mode: 0 -> block
        // mode: 1 -> unblock

        val response = MutableLiveData<String>()

        val uidBlockedByRef: DocumentReference = blocksRef.document(dataBlockedUser.uid).collection("blockedBy").document(dataUser.uid)
        val uidBlockingRef: DocumentReference = blocksRef.document(dataUser.uid).collection("blocking").document(dataBlockedUser.uid)

        rootRef.runBatch { // trebuie sa se efectueze ambele operatii ori niciuna
            if (mode == 0) {
                it.set(uidBlockedByRef, dataUser)
                it.set(uidBlockingRef, dataBlockedUser)
            } else {
                it.delete(uidBlockedByRef)
                it.delete(uidBlockingRef)
            }
        }.addOnCompleteListener {
            response.value = ""
        }.addOnFailureListener {
            response.value = it.message

            val cuvant = if (mode == 0) "blocarea" else "deblocarea"
            Log.d(TAG, "Eroare la $cuvant lui ${dataBlockedUser.uid} fata de ${dataUser.uid}: ${it.message}")
        }

        return response
    }

    fun checkIfBlocked(uidUser: String, uidBlockedUser: String): MutableLiveData<Boolean> {
        val response = MutableLiveData<Boolean>()

        val ref: DocumentReference = blocksRef.document(uidUser).collection("blocking").document(uidBlockedUser)
        ref.get().addOnCompleteListener {
            val document = it.result
            response.value = document!!.exists()
        }.addOnFailureListener {
            Log.d(TAG, "Eroare la verificarea daca $uidBlockedUser e (de)blocat de $uidUser")
        }
        return response
    }

    fun getUser(uid: String): MutableLiveData<FirestoreUser> {
        val user = MutableLiveData<FirestoreUser>()

        val userRef: DocumentReference = usersRef.document(uid)
        userRef.get().addOnCompleteListener {
            user.value = it.result!!.toObject(FirestoreUser::class.java)
            Log.d(TAG, "marc si ${it.result!!.toObject(FirestoreUser::class.java)!!.uid}")
        }.addOnFailureListener {
            Log.d(TAG, "Eroare la getUser cu uid $uid: ${it.message}")
            val data = FirestoreUser()
            data.uid = ""
            user.value = data
        }
        return user
    }

    fun getUser(): MutableLiveData<FirestoreUser> {
        val user = MutableLiveData<FirestoreUser>()

        /// TODO get uid of user in other way

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

                    rootRef.runBatch { batch ->
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

                    uidRef.set(authenticatedUser).addOnCompleteListener { userCreationTask ->
                        if (userCreationTask.isSuccessful) {
                            data = AuthModel(
                                authenticatedUser.uid,
                                authenticatedUser.email,
                                authenticatedUser.name,
                                null,
                                true
                            )
                            newUserMutableLiveData.value = data
                        } else {
                            data = AuthModel()
                            data.error = userCreationTask.exception!!.message
                            Log.d(TAG, "eroare la setare date in firestore: ${userCreationTask.exception!!.message}")
                            newUserMutableLiveData.value = data
                        }
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
        response.value = true

        idsRef.document(id).get()
            .addOnCompleteListener {
                response.value = it.result!!.exists()
            }.addOnFailureListener {
                //TODO failure event
                Log.d(TAG, "eroare la veriricare id unic ($id): ${it.message}")
            }

        return response
    }

}