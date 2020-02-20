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
import kotlinx.android.synthetic.main.fragment_profile.*
import ro.marc.chatapp.R
import ro.marc.chatapp.model.BlockModel
import ro.marc.chatapp.viewmodel.db.AuthViewModel
import ro.marc.chatapp.viewmodel.db.FirestoreViewModel

class Profile : Fragment() {
    private val TAG = "ChatApp Profile"
    private lateinit var authViewModel: AuthViewModel
    private lateinit var firestoreViewModel: FirestoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        lateinit var uid: String
        lateinit var id: String

        firestoreViewModel.getUser()
        firestoreViewModel.fetchedUser!!.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                profile.text = it.uid
                uid = it.uid
                id = it.id!!
            } else profile.text = getString(R.string.general_error)
        })

        profile.setOnClickListener {
            logOut()
        }

        block.setOnClickListener {
            val text = uidEditText.text.toString()
            println(text)
            firestoreViewModel.getUser(text)
            firestoreViewModel.fetchedOtherUser!!.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    firestoreViewModel.block(BlockModel(uid, id), BlockModel(text, it.id!!))
                }
            })
        }
    }

    fun logOut() {
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
