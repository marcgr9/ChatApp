package ro.marc.databinding

import java.util.*

data class Data(var mId: String, var mEmail: String, var mName: String, var mDate: String) {
    fun getId(): String = mId
    fun getEmail(): String = mEmail
    fun getName(): String = mName
    fun getDate(): String = mDate

    fun setId(sId: String) {mId = sId}
    fun setEmail(sEmail: String) {mEmail = sEmail}
    fun setName(sName: String) {mName = sName}
    fun setDate(sDate: String) {mDate = sDate}
}
