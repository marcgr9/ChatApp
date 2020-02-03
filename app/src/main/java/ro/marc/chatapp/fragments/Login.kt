package ro.marc.chatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ro.marc.chatapp.databinding.FragmentLoginBinding
import ro.marc.chatapp.viewmodel.LoginViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.fragment_login.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import ro.marc.chatapp.R


class Login : Fragment() {

    val RC_SIGN_IN = 123

    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.executePendingBindings()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel: LoginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        viewModel.error.observe(viewLifecycleOwner, Observer {
            if (it == true) errField.text = getString(R.string.ERRLogin)
            else errField.text = ""

        })

        viewModel.clicked.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                goToRegister()
                viewModel.onNoAccountClicked(false)
            }
        })


        loginBtn.setOnClickListener {  signIn() }

        initClient()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.loginViewModel = viewModel

    }

    fun goToRegister() {
        findNavController().navigate(R.id.action_login_to_register)
    }

    private fun initClient() {
        val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity!!, googleSignInOptions)
    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                if (account != null) {
                    println("succes")
                }
            } catch (e: ApiException) {
                println("login esuat: $e")
            }
        }
    }


}
