package ro.marc.chatapp.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import ro.marc.chatapp.model.AuthModel
import ro.marc.chatapp.model.BlockModel
import ro.marc.chatapp.model.FirestoreUser

class FirestoreRepository {
    private val TAG = "ChatApp FirestoreRepository"

    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val usersRef: CollectionReference = rootRef.collection("users")
    private val blocksRef: CollectionReference = rootRef.collection("blocks")

    fun blockUser(dataUser: BlockModel, dataBlockedUser: BlockModel) {

        val uidBlockedByRef: DocumentReference = blocksRef.document(dataBlockedUser.uid).collection("blockedBy").document(dataUser.uid)
        val uidBlockingRef: DocumentReference = blocksRef.document(dataUser.uid).collection("blocking").document(dataBlockedUser.uid)

        rootRef.runBatch {
            it.set(uidBlockedByRef, dataUser)
            it.set(uidBlockingRef, dataBlockedUser)
        }.addOnFailureListener {
            // TODO error handling
            print(it.message)
        }
    }

    fun getUser(uid: String): MutableLiveData<FirestoreUser?> {
        val user = MutableLiveData<FirestoreUser?>()

        val userRef: DocumentReference = usersRef.document(uid)
        userRef.get().addOnCompleteListener {
            println(it.exception)
            if (it.isSuccessful) {
                user.value = it.result!!.toObject(FirestoreUser::class.java)
            } else {
                println(it.exception!!.message)

                user.value = null
            }
        }
        return user
    }

    fun getUser(): MutableLiveData<FirestoreUser?> {
        val user = MutableLiveData<FirestoreUser?>()

        /// TODO get uid of user in other way

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val userRef: DocumentReference = usersRef.document(uid)
        userRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                user.value = it.result!!.toObject(FirestoreUser::class.java)
            } else {
                println(it.exception!!.message)
                user.value = null
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

}