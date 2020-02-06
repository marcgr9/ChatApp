package ro.marc.chatapp.fragments

import android.os.Bundle
import android.util.Log
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
import ro.marc.chatapp.model.RegisterModel
import ro.marc.chatapp.model.User
import ro.marc.chatapp.utils.Utils
import ro.marc.chatapp.viewmodel.AuthViewModel

class Register : Fragment() {

    lateinit var binding: FragmentRegisterBinding
    lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        println("start of oncreate view")

        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.executePendingBindings()

        println("end of oncreate view")

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        println("start of onact created")

        val emailFromLogin = arguments?.getString("email")
        val nameFromLogin = arguments?.getString("name")
        val uidFromLogin = arguments?.getString("uid")

        val viewModel: RegisterViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

        viewModel.setData(RegisterModel(uidFromLogin!!, "", emailFromLogin, "", nameFromLogin, ""))

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

                if (errors == "") {
                    println("no errors so we call createuser from register.kt")
                    createUser(viewModel.getData()!!)
                }
            }
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.registerViewModel = viewModel

        println("end of inact created")
    }

    private fun createUser(authenticatedUser: RegisterModel) {
        println("called createuser inside register.kt")
        authViewModel.createUser(authenticatedUser)
        println("called createuser inside authviewmodel")
        authViewModel.createdUserLiveData?.observe(viewLifecycleOwner, Observer { user ->
            println("inregistrat ca si ${user.name}")
        })
    }


}
