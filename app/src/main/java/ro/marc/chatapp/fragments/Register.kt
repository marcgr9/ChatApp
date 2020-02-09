package ro.marc.chatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_register.*
import ro.marc.chatapp.R
import ro.marc.chatapp.viewmodel.RegisterViewModel
import ro.marc.chatapp.databinding.FragmentRegisterBinding
import ro.marc.chatapp.model.FirestoreUser
import ro.marc.chatapp.model.RegisterModel
import ro.marc.chatapp.utils.Utils
import ro.marc.chatapp.viewmodel.AuthViewModel
import ro.marc.chatapp.viewmodel.factory.RegisterViewModelFactory

class Register : Fragment() {

    lateinit var binding: FragmentRegisterBinding
    lateinit var authViewModel: AuthViewModel

    var emailFromLogin: String? = null
    var nameFromLogin: String? = null
    var uidFromLogin: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.executePendingBindings()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        emailFromLogin = arguments?.getString("email")
        nameFromLogin = arguments?.getString("name")
        uidFromLogin = arguments?.getString("uid")

        val factory = RegisterViewModelFactory(if (userAuthIsCreated()) 1 else 0)

        val viewModel: RegisterViewModel = ViewModelProviders.of(this, factory).get(RegisterViewModel::class.java)

        if (userAuthIsCreated()) {
            viewModel.setData(RegisterModel(uidFromLogin!!, "", emailFromLogin, "", nameFromLogin, ""))
        }

        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)

        viewModel.errors.observe(viewLifecycleOwner, Observer {
            // ca sa nu intre cand e initializat livedata-ul in vm
            // initializat -> null , la verificare de erori ArrayList gol / cu erori
            if (it != null) {
                var errors = ""

                it.forEach { err ->
                    val stringId: Int = when (err) {
                        Utils.CredentialErrors.ERREmail -> R.string.ERREmail
                        Utils.CredentialErrors.ERRId -> R.string.ERRId
                        Utils.CredentialErrors.ERRPassword -> R.string.ERRPassword
                        Utils.CredentialErrors.ERRDateFormat -> R.string.ERRDateFormat
                        Utils.CredentialErrors.ERRDateAge -> R.string.ERRDateAge
                        Utils.CredentialErrors.ERRName -> R.string.ERRName
                        else -> R.string.NULL
                    }

                    errors += getString(stringId)
                    errors += "\n"
                }

                errField.text = errors
            }
        })

        viewModel.isSuccessful.observe(viewLifecycleOwner, Observer {
            errField.text = ""
            register(it)
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.registerViewModel = viewModel
    }

    fun register(data: RegisterModel) {
        if (userAuthIsCreated()) createFirestoreUser(data)
        else {
            println("start creare auth")
            createAuthUser(data)
        }
    }

    private fun userAuthIsCreated(): Boolean {
        /// e diferit de null doar cand e deschisa pagina ca urmare la google sign up
        return emailFromLogin != null
    }

    private fun createAuthUser(data: RegisterModel) {
        authViewModel.signUpUser(data.email!!, data.password!!)
        authViewModel.signedUpUser?.observe(viewLifecycleOwner, Observer {
            println("user inregistrat")
            data.uid = it.uid
            createFirestoreUser(data)
        })
    }

    private fun createFirestoreUser(authenticatedUser: RegisterModel) {
        // convert din model cu toate datele in doar cele necesare
        val fsUser = FirestoreUser(authenticatedUser.uid, authenticatedUser.email, authenticatedUser.name, authenticatedUser.id, authenticatedUser.birthday)
        authViewModel.createUser(fsUser)
        authViewModel.createdUserLiveData?.observe(viewLifecycleOwner, Observer { user ->
            println("creat user-ul ${user.uid} in firestore (${user.name})")
        })
    }


}
