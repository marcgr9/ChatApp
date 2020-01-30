package ro.marc.chatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import ro.marc.chatapp.databinding.FragmentLoginBinding
import ro.marc.chatapp.viewmodel.LoginViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
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

        val viewModel: LoginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        viewModel.error.observe(this, Observer {
            if (it == true) errField.text = getString(resources.getIdentifier(errorId , "string", "ro.marc.chatapp"))
            else errField.text = ""

        })

        viewModel.clicked.observe(this, Observer {
            if (it == true) {
                goToRegister()
                viewModel.onNoAccountClicked(false)
            }
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.loginViewModel = viewModel

    }

    fun goToRegister() {
        findNavController().navigate(ro.marc.chatapp.R.id.action_login_to_register)
        //Toast.makeText(requireActivity(), "toast", Toast.LENGTH_LONG).show()
    }
}
