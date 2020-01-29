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

    //Nu este necesar sa ai pentru fiecare field. Poti sa faci un singur obiect LiveData cu RegisterModel.
    // Acest livedata il utilizezi atat in view cat si la validari
    val registerModelLiveData: LiveData<RegisterModel>
        get() = _registerModelLiveData
    private val _registerModelLiveData = MutableLiveData<RegisterModel>()

    init {
        _registerModelLiveData.value = RegisterModel()
    }

    var email: MutableLiveData<String> = MutableLiveData()
    var id: MutableLiveData<String> = MutableLiveData()
    var name: MutableLiveData<String> = MutableLiveData()
    var date: MutableLiveData<String> = MutableLiveData()

    var errs: MutableLiveData<ArrayList<Errors>> = MutableLiveData()

    fun getErrors(): MutableLiveData<ArrayList<Errors>> {
        println("getErrors, livedata size: " + errs.value?.size)
        return errs
    }

    fun onRegisterClicked() {
        println("onButtonnClicked")

        val registerModel = _registerModelLiveData.value!!

        //var data = RegisterModel(id.value!!, email.value!!, name.value!!, date.value!!)
        var err: ArrayList<Errors> = ArrayList()

        if (registerModel.email?.length!! < 3) {
            //println("asf")
            err.add(Errors.ERREmail)
            println("errors size: " + err.size)
        }

        errs.value = err
        //println(errs.value!!.equals(err))
    }

}