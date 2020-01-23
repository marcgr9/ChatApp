package ro.marc.chatapp.viewmodel

import android.util.Patterns
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.navigation.Navigation
import ro.marc.chatapp.BR
import ro.marc.chatapp.R
import ro.marc.chatapp.model.LoginModel
import ro.marc.chatapp.model.RegisterModel
import java.text.ParseException
import java.text.SimpleDateFormat

class LoginViewModel : BaseObservable() {
    var dataVar: LoginModel = LoginModel("", "")

    @Bindable
    var message: String = ""

    fun setMsg(msg: String) {
        this.message = msg;
        notifyPropertyChanged(BR.message)
    }

    fun getMsg(): String {
        return this.message
    }

    fun onLoginClicked() {
        setMsg(inputValid())
    }

//    fun noAccount() {
//
//    }
//    https://stackoverflow.com/questions/51451819/how-to-get-context-in-android-mvvm-viewmodel

    private fun inputValid(): String {
        var msg: String = "";
        if (getEmail().length < 1 || Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches()) msg += "Email-ul e invalid" + "\n"
        if (getPass().equals("parola")) msg += "Parola invalida"

        return msg

    }

    fun setEmail(email: String) {
        this.dataVar.setEmail(email)
    }

    @Bindable
    fun getEmail(): String {
        return dataVar.getEmail()
    }

    fun setPass(pass: String) {
        this.dataVar.setPass(pass)
    }

    @Bindable
    fun getPass(): String {
        return dataVar.getPass()
    }

}