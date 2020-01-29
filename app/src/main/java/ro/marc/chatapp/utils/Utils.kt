package ro.marc.chatapp.utils

import java.text.ParseException
import java.text.SimpleDateFormat

object Utils {
    fun isValidDate(inDate: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        dateFormat.setLenient(false)
        try {
            dateFormat.parse(inDate.trim { it <= ' ' })
        } catch (pe: ParseException) {
            return false
        }

        return true
    }
}