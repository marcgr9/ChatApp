package ro.marc.chatapp.viewmodel.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.marc.chatapp.model.fragments.LoginModel

class LoginViewModel : ViewModel() {
    var loginModel = LoginModel()

    val error: LiveData<Boolean>
        get() = _error

    val clicked: LiveData<Boolean>
        get() = _clicked

    val isSuccessful: LiveData<LoginModel>
        get() = _isSuccessful

    private val _error = MutableLiveData<Boolean>()
    private val _isSuccessful = MutableLiveData<LoginModel>()
    private val _clicked = MutableLiveData<Boolean>()

    init {
        _error.value = false
    }

    fun onLoginClicked() {
        val registerModel: LoginModel = loginModel

        val email: String? = registerModel.email
        val password: String? = registerModel.password

        _error.value = email.isNullOrBlank() || password.isNullOrBlank()

        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            _error.value = true
        } else _isSuccessful.value = loginModel
    }

    fun onNoAccountClicked(clicked: Boolean) {
        _clicked.value = clicked
    }

}