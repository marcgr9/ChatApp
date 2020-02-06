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
import androidx.core.os.bundleOf
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.fragment_login.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import ro.marc.chatapp.R
import ro.marc.chatapp.model.RegisterModel
import ro.marc.chatapp.viewmodel.AuthViewModel


class Login : Fragment() {

    private val RC_SIGN_IN = 123

    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authViewModel: AuthViewModel

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

        loginBtn.setOnClickListener { signIn() }
        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
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
                    getGoogleAuthCredential(account)
                }
            } catch (e: ApiException) {
                println("esuat: $e")
            }
        }
    }

    private fun getGoogleAuthCredential(googleSignInAccount: GoogleSignInAccount) {
        val googleTokenId = googleSignInAccount.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
        signInWithGoogleAuthCredential(googleAuthCredential)
    }

    private fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential) {
        println("before signinw google")
        authViewModel.signInWithGoogle(googleAuthCredential)
        println("after signinw google")
        authViewModel.authenticatedUserLiveData?.observe(this, Observer { authenticatedUser ->
            println("start of authedUser observer")
            if (authenticatedUser.isNew) {
                // redirect la un fragment nou pt datele suplimentare

                val bundle = bundleOf("email" to authenticatedUser.email, "name" to authenticatedUser.name, "uid" to authenticatedUser.uid)
                findNavController().navigate(R.id.action_login_to_register, bundle)

                //createNewUser(authenticatedUser)
            } else {
                println("logat ca si ${authenticatedUser.name}")
            }
        println("end of authed user observer")
        })
    }


}
