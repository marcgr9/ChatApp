package ro.marc.chatapp.model

//data class au fost construite pentru a mapa un model, iar una dintre caracteristicile lor este ca
// simplifica codul deoarece au deja unele metode implementate (get/set pt parametri, toString, equals etc)
data class RegisterModel(
    var id: String? = null,
    var email: String? = null,
    var name: String? = null,
    var date: String? = null
)
