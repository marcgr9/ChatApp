package ro.marc.chatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import ro.marc.chatapp.databinding.FragmentLoginBinding
import ro.marc.chatapp.viewmodel.LoginViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_login.errField


class Login : Fragment() {

    val errorId = "ERRLogin"

    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.executePendingBindings()

        return binding.getRoot()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        viewModel.hasError().observe(this, Observer {
            if (it == true) errField.text = getString(resources.getIdentifier(errorId , "string", "ro.marc.chatapp"))
            else errField.text = ""

        })

        viewModel.clicked.observe(this, Observer {
            goToRegister()
            viewModel.resetState()
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.loginViewModel = viewModel

    }

    fun goToRegister() {
        val navController = Navigation.findNavController(activity!!,
            ro.marc.chatapp.R.id.navHostFragment
        )
        navController.navigate(ro.marc.chatapp.R.id.action_login_to_register)
    }
}
