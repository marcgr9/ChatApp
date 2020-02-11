package ro.marc.chatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*
import ro.marc.chatapp.R

class Profile : Fragment() {

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
            firebaseAuth.signOut()
            loginManager.logOut()
            findNavController().navigate(R.id.profile_to_login)
        }
    }
}
