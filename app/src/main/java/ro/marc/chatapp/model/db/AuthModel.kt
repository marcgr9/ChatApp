package ro.marc.chatapp.model.db

data class AuthModel(
    var uid: String? = null,
    var email: String? = null,
    var name: String? = null,
    var error: String? = null,
    var isNew: Boolean = false
)