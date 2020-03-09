package ro.marc.chatapp.model.db

import ro.marc.chatapp.utils.Utils

data class FirestoreUser(
    var uid: String = "",
    var email: String? = "",
    var name: String? = "",
    var id: String? = "",
    var birthday: String? = "",
    var profileUri: String = Utils.DEFAULT_PROFILE_PICTURE
)