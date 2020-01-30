package ro.marc.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.marc.chatapp.model.RegisterModel
import ro.marc.chatapp.utils.Utils
import ro.marc.chatapp.utils.Utils.CredentialErrors

class RegisterViewModel : ViewModel() {
    val registerModelLiveData: LiveData<RegisterModel>
        get() = _registerModelLiveData
    private val _registerModelLiveData = MutableLiveData<RegisterModel>()

    val errors: LiveData<ArrayList<CredentialErrors?>>
        get() = _errors

    private val _errors = MutableLiveData<ArrayList<CredentialErrors?>>()

    init {
        _registerModelLiveData.value = RegisterModel()
        _errors.value = ArrayList<CredentialErrors?>()
    }

    fun onRegisterClicked() {
        val registerModel = _registerModelLiveData.value!!

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

        _errors.value = err
    }

}