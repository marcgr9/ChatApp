package ro.marc.chatapp.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.*
import ro.marc.chatapp.R
import ro.marc.chatapp.databinding.FragmentProfileBinding
import ro.marc.chatapp.databinding.FragmentRegisterBinding
import ro.marc.chatapp.model.db.BlockData
import ro.marc.chatapp.utils.BindingAdapters
import ro.marc.chatapp.viewmodel.db.AuthViewModel
import ro.marc.chatapp.viewmodel.db.FirestoreViewModel
import ro.marc.chatapp.viewmodel.db.StorageViewModel
import java.io.ByteArrayOutputStream



class Profile : Fragment() {
    private val TAG = "ChatApp Profile"
    private val PICK_IMAGE_REQUEST = 43
    private lateinit var authViewModel: AuthViewModel
    private lateinit var firestoreViewModel: FirestoreViewModel
    private lateinit var storageViewModel: StorageViewModel

    private lateinit var binding: FragmentProfileBinding

    lateinit var uid: String
    lateinit var id: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.executePendingBindings()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel::class.java)
        storageViewModel = ViewModelProviders.of(this).get(StorageViewModel::class.java)

        firestoreViewModel.getUser()
        firestoreViewModel.fetchedUser!!.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                profile.text = it.uid
                uid = it.uid
                id = it.id!!
                Log.d(TAG, "$uid si $id")

                binding.pictureUri = Uri.parse(it.profileUri)
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

        image_view.setOnClickListener {
            takePictureIntent()
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

    private fun takePictureIntent() {
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        ).also {
            startActivityForResult(it, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST) {
            val imgUri = data!!.data

            val source = ImageDecoder.createSource(activity!!.contentResolver, imgUri!!)
            val bitmap = ImageDecoder.decodeBitmap(source)

            storageViewModel.uploadImage(bitmap, uid)
            storageViewModel.imageUploaded!!.observe(viewLifecycleOwner, Observer {
                if (it.response == "") {
                    Glide.with(this).load(it.img).into(image_view)
                } else {
                    Log.d(TAG, it.response)
                }
            })
        }
    }
}
