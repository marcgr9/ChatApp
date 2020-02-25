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
import ro.marc.chatapp.model.db.BlockData
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
        firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel::class.java)
        lateinit var uid: String
        lateinit var id: String

        firestoreViewModel.getUser()
        firestoreViewModel.fetchedUser!!.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                profile.text = it.uid
                uid = it.uid
                id = it.id!!
                Log.d(TAG, "$uid si $id")
            } else profile.text = getString(R.string.general_error)
        })

        profile.setOnClickListener {
            logOut()
        }

        editfriendship.setOnClickListener {
            val text = uidEditText.text.toString()
            val modee: String = mode.text.toString()
            val actionn = action.text.toString()
            firestoreViewModel.getUser(text)
            firestoreViewModel.fetchedOtherUser!!.observe(viewLifecycleOwner, Observer { user ->
                if (user.uid != "") {
                    firestoreViewModel.changeFriendship(
                        BlockData(uid, id),
                        BlockData(user.uid, user.id!!),
                        modee.toInt(),
                        actionn.toInt()
                    )
                    firestoreViewModel.changedFriendship!!.observe(viewLifecycleOwner, Observer { resp ->
                            Log.d(TAG, "changedfriendship: $resp")
                    })
                }
            })
        }

        checkfriendship.setOnClickListener {
            val text = uidEditText.text.toString()
            firestoreViewModel.getUser(text)
            firestoreViewModel.fetchedOtherUser!!.observe(viewLifecycleOwner, Observer { user ->
                if (user.uid != "") {
                    firestoreViewModel.checkFriendship(uid, user.uid)
                    firestoreViewModel.friendshipStatus!!.observe(
                        viewLifecycleOwner,
                        Observer { mode ->
                            Log.d(TAG, "mode = $mode")
                        })
                }
            })
        }

        block.setOnClickListener {
            val text = uidEditText.text.toString()
            firestoreViewModel.getUser(text)
            firestoreViewModel.fetchedOtherUser!!.observe(viewLifecycleOwner, Observer { user ->
                if (user.uid != "") {
                    firestoreViewModel.checkIfBlocked(uid, user.uid)
                    firestoreViewModel.blocked?.observe(viewLifecycleOwner, Observer { it2 ->
                        val mode = if (it2 == true) 1 else 0
                        firestoreViewModel.changeBlockedStatus(
                            BlockData(uid, id),
                            BlockData(text, user.id!!),
                            mode
                        )

                        firestoreViewModel.blockedStatus!!.observe(viewLifecycleOwner, Observer { it3 ->
                            Log.d(TAG, it3!!)
                        })

                    })
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
