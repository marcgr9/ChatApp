package ro.marc.chatapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
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

    private val TAG = "ChatApp Register"

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var authViewModel: AuthViewModel

    private var emailFromLogin: String? = null
    private var nameFromLogin: String? = null
    private var uidFromLogin: String? = null

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
            if (it.error == null) {
                Log.d(TAG, "user inregistrat fara firestore: ${it.uid}")
                data.uid = it.uid!!
                createFirestoreUser(data)
            } else errField.text = it.error
        })
    }

    private fun createFirestoreUser(user: RegisterModel) {
        // convert din model cu toate datele in doar cele necesare
        val fsUser = FirestoreUser(user.uid, user.email, user.name, user.id, user.birthday)
        authViewModel.createUserInFirestore(fsUser)
        authViewModel.createdFirestoreUser?.observe(viewLifecycleOwner, Observer {
            if (it.error == null) {
                Log.d(TAG, "creat user in firestore: ${it.uid}")
                findNavController().navigate(R.id.register_to_profile)
            } else errField.text = it.error
        })
    }


}
