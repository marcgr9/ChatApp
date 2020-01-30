package ro.marc.chatapp.model

data class RegisterModel(
    var id: String? = null,
    var email: String? = null,
    var password: String? = null,
    var name: String? = null,
    var birthday: String? = null
)
