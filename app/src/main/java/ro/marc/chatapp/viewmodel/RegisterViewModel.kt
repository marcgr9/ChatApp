package ro.marc.chatapp.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.marc.chatapp.BR
import ro.marc.chatapp.model.RegisterModel
import java.text.ParseException
import java.text.SimpleDateFormat

class RegisterViewModel : ViewModel() {
    enum class Errors {
        ERREmail, ERRID, ERRName, ERRDate
    }

    var email: MutableLiveData<String> = MutableLiveData()
    var id: MutableLiveData<String> = MutableLiveData()
    var name: MutableLiveData<String> = MutableLiveData()
    var date: MutableLiveData<String> = MutableLiveData()

    var errs: MutableLiveData<ArrayList<Errors>> = MutableLiveData()

    fun getErrors(): LiveData<ArrayList<Errors>> {
        return errs
    }

    fun onRegisterClicked() {
        Log.d("test", "onregoster")
        var data: RegisterModel = RegisterModel(id.value!!, email.value!!, name.value!!, date.value!!)
        var err: ArrayList<Errors> = ArrayList()

        if (data.getEmail().length < 3) {
            println("asf")
            err.add(Errors.ERREmail)
        }

        errs.value = err


    }


//    fun getErrors(): MutableLiveData<ArrayList<Errors>> {
//        var errors: MutableLiveData<ArrayList<Errors>> = MutableLiveData()
//        if (getEmail().isEmpty() || Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches()) errors.value?.add(Errors.ERREmail)
//        if (getId().length < 4 || getId().length > 16 || !getId().matches(Regex("[A-Za-z0-9._]+"))) errors.value?.add(Errors.ERRID)
//        if (getName().length < 3) errors.value?.add(Errors.ERRName)
//        if (!isValidDate(getDate())) errors.value?.add(Errors.ERRDate)
//
//        return errors
//
//    }

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