package ro.marc.chatapp.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.marc.chatapp.model.RegisterModel
import ro.marc.chatapp.utils.Utils
import java.text.ParseException
import java.text.SimpleDateFormat

class RegisterViewModel : ViewModel() {

    enum class Errors {
        ERREmail, ERRID, ERRName, ERRDate, ERRPassword
    }

    val registerModelLiveData: LiveData<RegisterModel>
        get() = _registerModelLiveData
    private val _registerModelLiveData = MutableLiveData<RegisterModel>()

    init {
        _registerModelLiveData.value = RegisterModel()
    }

    var errs: MutableLiveData<ArrayList<Errors>> = MutableLiveData()

    fun getErrors(): MutableLiveData<ArrayList<Errors>> = errs

    fun onRegisterClicked() {
        val registerModel = _registerModelLiveData.value!!

        var err: ArrayList<Errors> = ArrayList()

        val email: String? = registerModel.email
        val id: String? = registerModel.id
        val name: String? = registerModel.name
        val birthday: String? = registerModel.date
        val passwd: String? = registerModel.password

        if (!Patterns.EMAIL_ADDRESS.matcher(email!!).matches()) err.add(Errors.ERREmail)
        if (id!!.length < 4 || id!!.length > 16 || !id!!.matches(Regex("[A-Za-z0-9._]+"))) err.add(Errors.ERRID)
        if (name!!.length < 3) err.add(Errors.ERRName)
        if (!Utils.isValidDate(birthday!!)) err.add(Errors.ERRDate)
        if (passwd!!.length < 4 || !passwd!!.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$"))) err.add(Errors.ERRPassword)

        errs.value = err
    }

}