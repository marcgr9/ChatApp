package ro.marc.chatapp.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import ro.marc.chatapp.R
import ro.marc.chatapp.viewmodel.AuthViewModel

class Splash : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_splash, container, false)

    val TAG = "ChatApp Splash"

    lateinit var authViewModel: AuthViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val handler = Handler()
        handler.postDelayed({
            var action: Int = R.id.splash_to_login

            authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
            authViewModel.checkIfUserIsLoggedIn()
            authViewModel.loggedUserUid?.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Log.d(TAG, "exista un user autentificat: $it")
                    action = R.id.splash_to_profile
                } else Log.d(TAG, "nu exista user autentificat")
            })
            findNavController().navigate(action)
        }, 3000)

    }
}
