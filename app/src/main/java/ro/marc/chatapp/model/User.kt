package ro.marc.chatapp.model

import com.google.firebase.firestore.Exclude

data class User(var uid: String = "",
                var name: String? = "",
                var email: String? = ""
) {
    @get:Exclude
    var isAuthenticated: Boolean = false
    @get:Exclude
    var isNew: Boolean = false
    @get:Exclude
    var isCreated: Boolean = false
}