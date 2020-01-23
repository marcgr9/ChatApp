package ro.marc.chatapp.model

data class LoginModel(var mEmail: String, var mPass: String) {
    fun getEmail(): String = mEmail
    fun getPass(): String = mPass

    fun setEmail(sEmail: String) {mEmail = sEmail}
    fun setPass(sPass: String) {mPass = sPass}
}
