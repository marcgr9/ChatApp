package ro.marc.chatapp.model

data class FirestoreUser(
    var uid: String = "",
    var email: String? = "",
    var name: String? = "",
    var id: String? = "",
    var birthday: String? = ""
)