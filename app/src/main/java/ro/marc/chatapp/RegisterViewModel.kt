package ro.marc.chatapp

import android.util.Patterns
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import java.text.ParseException
import java.text.SimpleDateFormat

class RegisterViewModel() : BaseObservable() {


    var dataVar: Data = Data("", "", "", "");

    @Bindable
    var message: String = ""

    init {
        dataVar = Data("", "", "", "")
    }

    fun setMsg(msg: String) {
        this.message = msg;
        notifyPropertyChanged(BR.message)
    }

    fun getMsg(): String {
        return this.message
    }

    fun onRegisterClicked() {
        setMsg(inputValid())
    }

    private fun inputValid(): String {
        var msg: String = "";
        if (getEmail().length < 1 || Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches()) msg += "Email-ul e invalid" + "\n"
        if (getId().length < 4 || getId().length > 16 || !getId().matches(Regex("[A-Za-z0-9._]+"))) msg += "ID-ul e format doar din litere, cifre, ., _ si are 4-16 caractere" + "\n"
        if (getName().length < 3) msg += "Numele trebuie sa aiba minimn 3 caractere" + "\n"
        if (!isValidDate(getDate())) msg += "Data trebuie sa aiba formatul dd/mm/yyyy" + "\n"

        return msg

    }

    fun setEmail(email: String) {
        this.dataVar.setEmail(email)
    }

    @Bindable
    fun getEmail(): String {
        return dataVar.getEmail()
    }

    fun setId(id: String) {
        this.dataVar.setId(id)
    }

    @Bindable
    fun getId(): String {
        return dataVar.getId()
    }

    fun setName(name: String) {
        this.dataVar.setName(name)
    }

    @Bindable
    fun getName(): String {
        return dataVar.getName()
    }

    fun setDate(date: String) {
        this.dataVar.setDate(date)
    }

    @Bindable
    fun getDate(): String {
        return dataVar.getDate()
    }

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