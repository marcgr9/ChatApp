package ro.marc.chatapp.model

import com.google.firebase.firestore.Exclude

data class FirestoreUser(var uid: String = "",
                var email: String? = "",
                var name: String? = "",
                var id: String? = "",
                var birthday: String? = ""
) {
    @get:Exclude
    var isAuthenticated: Boolean = false
    @get:Exclude
    var isNew: Boolean = false
    @get:Exclude
    var isCreated: Boolean = false
}