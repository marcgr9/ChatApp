package ro.marc.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.marc.chatapp.model.LoginModel

class LoginViewModel : ViewModel() {

    val loginModelLiveData: LiveData<LoginModel>
        get() = _loginModelLiveData

    private val _loginModelLiveData = MutableLiveData<LoginModel>()

    init {
        _loginModelLiveData.value = LoginModel()
    }

    var error: MutableLiveData<Boolean> = MutableLiveData()
    var clicked: MutableLiveData<Boolean> = MutableLiveData()

    fun hasError(): MutableLiveData<Boolean> = error

    fun onLoginClicked() {
        val registerModel = _loginModelLiveData.value!!

        val email: String? = registerModel.email
        val passwd: String? = registerModel.password

        error.value = email!!.isBlank() || passwd!!.isBlank()
    }

    fun onNoAccountClicked() {
        clicked.value = true
    }

    fun resetState() {
        clicked = MutableLiveData()
    }

}