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
import ro.marc.chatapp.model.LoginModel
import ro.marc.chatapp.model.RegisterModel
import ro.marc.chatapp.viewmodel.AuthViewModel


class Login : Fragment() {

    private val RC_SIGN_IN = 123
    private val TAG = "ChatApp Login"

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

        viewModel.isSuccessful.observe(viewLifecycleOwner, Observer {
            loginUser(it)
        })

        google_login_button.setOnClickListener { signInWithGoogle() }
        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        initClient()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.loginViewModel = viewModel
    }

    private fun loginUser(data: LoginModel) {
        authViewModel.loginUser(data.email!!, data.password!!)
        authViewModel.loggedUser?.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "user logat cu email & parola: ${it.uid}")
            findNavController().navigate(R.id.login_to_profile)
        })
    }

    fun goToRegister() {
        findNavController().navigate(R.id.login_to_register)
    }

    private fun initClient() {
        val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity!!, googleSignInOptions)
    }

    private fun signInWithGoogle() {
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
                Log.d(TAG, "login cu google esuat: $e")
            }
        }
    }

    private fun getGoogleAuthCredential(googleSignInAccount: GoogleSignInAccount) {
        val googleTokenId = googleSignInAccount.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
        signInWithGoogleAuthCredential(googleAuthCredential)
    }

    private fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential) {
        authViewModel.signInWithGoogle(googleAuthCredential)
        authViewModel.signedInWithGoogleUser?.observe(this, Observer {
            if (it.isNew) {
                Log.d(TAG, "user inregistrat cu google: ${it.uid}")
                val bundle = bundleOf("email" to it.email, "name" to it.name, "uid" to it.uid)
                findNavController().navigate(R.id.login_to_register, bundle)
            } else {
                Log.d(TAG, "logat cu google: ${it.uid}")
                findNavController().navigate(R.id.login_to_profile)
            }
        })
    }
}
