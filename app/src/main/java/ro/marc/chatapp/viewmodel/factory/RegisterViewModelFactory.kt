package ro.marc.chatapp.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ro.marc.chatapp.viewmodel.fragments.RegisterViewModel

class RegisterViewModelFactory(
    private val mode: Int
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegisterViewModel(mode) as T
    }
}

// mode:
// 0 - register simplu
// 1 - register cu serviciu