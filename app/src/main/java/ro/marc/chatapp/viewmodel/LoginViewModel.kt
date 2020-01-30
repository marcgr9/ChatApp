package ro.marc.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.marc.chatapp.model.LoginModel

class LoginViewModel : ViewModel() {

    val loginModelLiveData: LiveData<LoginModel>
        get() = _loginModelLiveData

    val error: LiveData<Boolean>
        get() = _error

    private val _loginModelLiveData = MutableLiveData<LoginModel>()
    private val _error = MutableLiveData<Boolean>()

    init {
        _loginModelLiveData.value = LoginModel()
        _error.value = false
    }


    var clicked: MutableLiveData<Boolean> = MutableLiveData()

    fun onLoginClicked() {
        val registerModel = _loginModelLiveData.value!!

        val email: String? = registerModel.email
        val passwd: String? = registerModel.password

        _error.value = email.isNullOrBlank() || passwd.isNullOrBlank()
    }

    fun onNoAccountClicked(clicked: Boolean) {
        this.clicked.value = clicked
    }

}