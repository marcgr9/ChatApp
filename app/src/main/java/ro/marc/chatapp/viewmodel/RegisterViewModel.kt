package ro.marc.chatapp.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.marc.chatapp.model.RegisterModel
import ro.marc.chatapp.utils.Utils
import ro.marc.chatapp.utils.Utils.CredentialErrors

// mode:
// 0 - register simplu
// 1 - register cu google

class RegisterViewModel(
    var mode: Int
) : ViewModel() {
    var registerModel =  RegisterModel()
    var visibility: Int = if (mode == 1) View.GONE else View.VISIBLE

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

        if (mode == 0) {
            validations.add(Utils.checkEmail(registerModel.email))
            validations.add(Utils.checkPassword(registerModel.password))
        }
        validations.add(Utils.checkId(registerModel.id))
        validations.add(Utils.checkName(registerModel.name))
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