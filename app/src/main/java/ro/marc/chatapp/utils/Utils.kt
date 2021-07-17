package ro.marc.chatapp.utils

import android.util.Patterns
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate

object Utils {
    enum class CredentialErrors {
        ERRId, ERREmail, ERRName, ERRPassword, ERRDateFormat, ERRDateAge
    }

    fun checkDate(_date: String?): CredentialErrors? {
        if (_date.isNullOrBlank()) return Utils.CredentialErrors.ERRDateFormat
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        dateFormat.isLenient = false
        try {
            dateFormat.parse(_date.trim { it <= ' ' })
        } catch (pe: ParseException) {
            return Utils.CredentialErrors.ERRDateFormat
        }

        val dateList = _date.split("/")

        val date: LocalDate = LocalDate.of(dateList[2].toInt(), dateList[1].toInt(), dateList[0].toInt())
        val today: LocalDate = LocalDate.now()

        if (date.isBefore(today.minusYears(124)) || date.isAfter(today.minusYears(18)))
            return Utils.CredentialErrors.ERRDateAge

        return null
    }

    fun checkId(id: String?): CredentialErrors? {
        if (id.isNullOrBlank() || id.length < 4 || id.length > 16 || !id.matches(Regex("[A-Za-z0-9._]+")))
            return Utils.CredentialErrors.ERRId
        return null
    }

    fun checkEmail(email: String?): CredentialErrors? {
        if (email.isNullOrBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return Utils.CredentialErrors.ERREmail
        return null
    }

    fun checkPassword(password: String?): CredentialErrors? {
        if (password.isNullOrBlank() || password.length < 6 || !password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")))
            return Utils.CredentialErrors.ERRPassword
        return null
    }

    fun checkName(name: String?): CredentialErrors? {
        if (name.isNullOrBlank() || name.length < 3 || !name.matches(Regex("[a-zA-Z ]+")))
            return Utils.CredentialErrors.ERRName
        return null
    }
}