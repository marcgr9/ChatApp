package ro.marc.chatapp.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_register.*
import ro.marc.chatapp.R
import ro.marc.chatapp.viewmodel.fragments.RegisterViewModel
import ro.marc.chatapp.databinding.FragmentRegisterBinding
import ro.marc.chatapp.model.db.FirestoreUser
import ro.marc.chatapp.model.fragments.RegisterModel
import ro.marc.chatapp.utils.Utils
import ro.marc.chatapp.viewmodel.db.AuthViewModel
import ro.marc.chatapp.viewmodel.db.FirestoreViewModel
import ro.marc.chatapp.viewmodel.db.StorageViewModel
import ro.marc.chatapp.viewmodel.factory.RegisterViewModelFactory

class Register : Fragment() {
    private val TAG = "ChatApp Register"

    private val PICK_IMAGE_REQUEST = 43

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var firestoreViewModel: FirestoreViewModel

    private var emailFromLogin: String? = null
    private var nameFromLogin: String? = null
    private var uidFromLogin: String? = null
    private var profileImage: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.executePendingBindings()
        binding.profileImageUri = null

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        emailFromLogin = arguments?.getString("email")
        nameFromLogin = arguments?.getString("name")
        uidFromLogin = arguments?.getString("uid")

        val factory = RegisterViewModelFactory(if (userAuthIsCreated()) 1 else 0)

        val viewModel: RegisterViewModel = ViewModelProviders.of(this, factory).get(
            RegisterViewModel::class.java)

        if (userAuthIsCreated()) {
            viewModel.setData(
                RegisterModel(
                    uidFromLogin!!,
                    "",
                    emailFromLogin,
                    "",
                    nameFromLogin,
                    ""
                )
            )
        }

        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel::class.java)


        viewModel.clicked.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                selectPictureIntent()
                viewModel.onProfileImageClicked(false)
            }
        })

        viewModel.errors.observe(viewLifecycleOwner, Observer {
            // ca sa nu intre cand e initializat livedata-ul in vm
            // initializat -> null , la verificare de erori ArrayList gol / cu erori
            if (it != null) {
                var errors = ""

                it.forEach { err ->
                    val stringId: Int = when (err) {
                        Utils.CredentialErrors.ERREmail -> R.string.ERREmail
                        Utils.CredentialErrors.ERRId -> R.string.ERRId
                        Utils.CredentialErrors.ERRPassword -> R.string.ERRPassword
                        Utils.CredentialErrors.ERRDateFormat -> R.string.ERRDateFormat
                        Utils.CredentialErrors.ERRDateAge -> R.string.ERRDateAge
                        Utils.CredentialErrors.ERRName -> R.string.ERRName
                        else -> R.string.NULL
                    }

                    errors += getString(stringId)
                    errors += "\n"
                }

                errField.text = errors
            }
        })

        viewModel.isSuccessful.observe(viewLifecycleOwner, Observer {
            errField.text = ""
            register(it)
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.registerViewModel = viewModel
    }

    fun register(data: RegisterModel) {
        firestoreViewModel.checkIfIdAvailable(data.id!!)
        firestoreViewModel.idAvailable!!.observe(viewLifecycleOwner, Observer {
            if (it == false) {
                if (userAuthIsCreated()) createFirestoreUser(data)
                else {
                    createAuthUser(data)
                }
            } else errField.text = getString(R.string.id_not_unique)
        })
    }

    private fun userAuthIsCreated(): Boolean {
        /// e diferit de null doar cand e deschisa pagina ca urmare la google sign up
        return emailFromLogin != null
    }

    private fun createAuthUser(data: RegisterModel) {
        authViewModel.signUpUser(data.email!!, data.password!!)
        authViewModel.signedUpUser?.observe(viewLifecycleOwner, Observer {
            if (it.error == null) {
                Log.d(TAG, "user inregistrat fara firestore: ${it.uid}")
                data.uid = it.uid!!
                createFirestoreUser(data)
            } else errField.text = it.error
        })
    }

    private fun createFirestoreUser(user: RegisterModel) {
        // convert din model cu toate datele in doar cele necesare
        val fsUser = FirestoreUser(
            user.uid,
            user.email,
            user.name,
            user.id,
            user.birthday
        )
        firestoreViewModel.createUserInFirestore(fsUser)
        firestoreViewModel.createdFirestoreUser!!.observe(viewLifecycleOwner, Observer {
            if (it.error == null) {
                Log.d(TAG, "creat user in firestore: ${it.uid}")
                
                val source = ImageDecoder.createSource(activity!!.contentResolver, profileImage!!)
                val bitmap = ImageDecoder.decodeBitmap(source)

                uploadImage(it.uid!!, bitmap)
            } else errField.text = it.error
        })
    }

    private fun selectPictureIntent() {
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        ).also {
            startActivityForResult(it, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "maaarc $resultCode $requestCode ${data != null}")
        if (requestCode == PICK_IMAGE_REQUEST) {
            println("wsf" + data!!.data.toString())
            profileImage = data!!.data

            binding.profileImageUri = profileImage
            Log.d(TAG, binding.profileImageUri.toString())
            binding.executePendingBindings()
        }
    }

    fun uploadImage(uid: String, bitmap: Bitmap) {
        val storageViewModel: StorageViewModel = ViewModelProviders.of(this).get(StorageViewModel::class.java)

        storageViewModel.uploadImage(bitmap, uid)
        storageViewModel.imageUploaded!!.observe(viewLifecycleOwner, Observer {
            if (it.response == "") {
                findNavController().navigate(R.id.register_to_profile)
            }
        })
    }


}
