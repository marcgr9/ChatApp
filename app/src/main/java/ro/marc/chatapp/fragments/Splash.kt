package ro.marc.chatapp.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import ro.marc.chatapp.R
import ro.marc.chatapp.viewmodel.db.AuthViewModel
import ro.marc.chatapp.viewmodel.db.FirestoreViewModel

class Splash : Fragment() {
    private val TAG = "ChatApp Splash"

    private lateinit var authViewModel: AuthViewModel
    private lateinit var firestoreViewModel: FirestoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val handler = Handler()
        handler.postDelayed({
            authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
            firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel::class.java)
            authViewModel.checkIfUserIsLoggedIn()
            authViewModel.loggedUserUid!!.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Log.d(TAG, "exista un user autentificat: ${it.uid}")

                    firestoreViewModel.checkIfProfileCompleted(it.uid!!)
                    firestoreViewModel.profileCompleted!!.observe(viewLifecycleOwner, Observer { profileTask ->
                        if (profileTask == true) findNavController().navigate(R.id.splash_to_profile)
                        else {
                            val bundle =
                                bundleOf("email" to it.email, "name" to it.name, "uid" to it.uid)
                            findNavController().navigate(R.id.splash_to_register, bundle)
                        }
                    })
                } else {
                    findNavController().navigate(R.id.splash_to_login)
                    Log.d(TAG, "nu exista user autentificat")
                }
            })

        }, 3000)

    }
}
