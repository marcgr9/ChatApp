package ro.marc.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.marc.chatapp.model.RegisterModel
import ro.marc.chatapp.utils.Utils
import ro.marc.chatapp.utils.Utils.CredentialErrors

class RegisterViewModel : ViewModel() {
    var registerModel =  RegisterModel()

    val errors: LiveData<ArrayList<CredentialErrors?>?>
        get() = _errors

    val isSuccessful: LiveData<RegisterModel>
        get() = _isSuccessful

    private val _errors = MutableLiveData<ArrayList<CredentialErrors?>>()
    private val _isSuccessful = MutableLiveData<RegisterModel>()

    init {
        _errors.value = null
    }

    fun onRegisterClicked() {
        var err: ArrayList<CredentialErrors?> = ArrayList()

        var validations = ArrayList<CredentialErrors?>()

        validations.add(Utils.checkId(registerModel.id))
        validations.add(Utils.checkEmail(registerModel.email))
        validations.add(Utils.checkName(registerModel.name))
        validations.add(Utils.checkPassword(registerModel.password))
        validations.add(Utils.checkDate(registerModel.birthday))

        validations.forEach {
            if (it != null) {
                err.add(it)
            }
        }

        if (err.isEmpty()) {
            _isSuccessful.value = registerModel
        } else {
            _errors.value = err
        }
    }

    fun setData(data: RegisterModel) {
        registerModel = data
    }
}