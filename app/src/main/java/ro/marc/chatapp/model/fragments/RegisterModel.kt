package ro.marc.chatapp.model.fragments

data class RegisterModel(
    var uid: String = "",
    var id: String? = null,
    var email: String? = null,
    var password: String? = null,
    var name: String? = null,
    var birthday: String? = null
)