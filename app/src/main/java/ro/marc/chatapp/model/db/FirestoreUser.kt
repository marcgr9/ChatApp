package ro.marc.chatapp.model.db

data class FirestoreUser(
    var uid: String = "",
    var email: String? = "",
    var name: String? = "",
    var id: String? = "",
    var birthday: String? = "",
    var profileUri: String = "https://firebasestorage.googleapis.com/v0/b/chatapp-ccaad.appspot.com/o/pics%2Fdefault.png?alt=media&token=3f7550a6-d8f4-41f1-bc0b-9d0f8f5e539e"
)