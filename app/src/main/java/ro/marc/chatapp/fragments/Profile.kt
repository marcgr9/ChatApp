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
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*
import ro.marc.chatapp.R
import ro.marc.chatapp.viewmodel.AuthViewModel

class Profile : Fragment() {

    val TAG = "ChatApp Profile"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val loginManager: LoginManager = LoginManager.getInstance()

        profile.text = firebaseAuth.currentUser!!.uid

        profile.setOnClickListener {
            logOut()
        }
    }

    fun logOut() {
        val authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        authViewModel.logOut(activity!!)
        authViewModel.loggedOut!!.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Log.d(TAG, "logout esuat: $it")
            } else {
                findNavController().navigate(R.id.profile_to_login)
            }
        })
    }
}
