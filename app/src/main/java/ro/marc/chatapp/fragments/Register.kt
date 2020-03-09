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
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
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

/// mode:
/// register
//           -> create firebase user -> create firestore user doc -> upload & set user profile picture
/// register with service
//                                   -> create firestore user doc -> upload & set user profile picture
/// profile
//                                   -> update firestore user doc -> upload & set user profile

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
    private var initialUser: FirestoreUser = FirestoreUser()

    private var user: FirestoreUser? = null

    private enum class Modes {
        REGISTER, REGISTER_WITH_SERVICE, PROFILE
    }

    private var mode: Modes = Modes.REGISTER

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        profileImage = Uri.parse(Utils.DEFAULT_PROFILE_PICTURE)
        binding.profileImageUri = profileImage.toString()

        Log.d(TAG, binding.profileImageUri.toString())

        binding.executePendingBindings()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        emailFromLogin = arguments?.getString("email")
        nameFromLogin = arguments?.getString("name")
        uidFromLogin = arguments?.getString("uid")

        if (uidFromLogin != null && emailFromLogin != null) {
            mode = Modes.REGISTER_WITH_SERVICE
        } else if (uidFromLogin != null && emailFromLogin == null) {
            mode = Modes.PROFILE
        }

        val factory = RegisterViewModelFactory(if (mode == Modes.REGISTER) 0 else if (mode == Modes.REGISTER_WITH_SERVICE) 1 else 2)

        val viewModel: RegisterViewModel = ViewModelProviders.of(this, factory).get(
            RegisterViewModel::class.java)

        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel::class.java)

        if (mode == Modes.REGISTER_WITH_SERVICE) {
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
        } else if (mode == Modes.PROFILE) {
            registerBtn.text = getString(R.string.update_profile)

            firestoreViewModel.getUser(uidFromLogin!!)
            firestoreViewModel.fetchedUser!!.observe(viewLifecycleOwner, Observer {
                if (it.uid != "") {
                    user = it
                    val data = RegisterModel(
                        it.uid,
                        it.id,
                        it.email,
                        "",
                        it.name,
                        it.birthday
                    )

                    initialUser = it

                    viewModel.setData(data)

                    profileImage = Uri.parse(it.profileUri)
                    binding.profileImageUri = profileImage.toString()
                    binding.registerViewModel = viewModel
                }
            })
        }

        Log.d(TAG, mode.toString())


        button2.setOnClickListener {logOut()}

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
            click(it)
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.registerViewModel = viewModel
    }

    fun click(data: RegisterModel) {
        Log.d(TAG, mode.toString())
        firestoreViewModel.checkIfIdAvailable(data.id!!)
        firestoreViewModel.idAvailable!!.observe(viewLifecycleOwner, Observer {
            if (it == false || ((it == true) && (mode == Modes.PROFILE) && (data.id == initialUser.id!!))) {
                when (mode) {
                    Modes.REGISTER_WITH_SERVICE -> createFirestoreUser(data)
                    Modes.REGISTER -> createAuthUser(data)
                    Modes.PROFILE -> updateUser(data)
                }
            } else errField.text = getString(R.string.id_not_unique)
        })
    }

    private fun updateUser(data: RegisterModel) {
        changePasswordIfDifferent(data.password!!)
        updateUserFirestoreIfDifferent(data)
        updateImageIfDifferent()

    }

    fun changePasswordIfDifferent(password: String) {
        if (!password.isNullOrBlank()) {
            Log.d(TAG, "schimbare parola")
            authViewModel.updatePassword(password.toString())
            authViewModel.updatedPassword!!.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrBlank()) errField.text = it
            })
        }
    }

    fun updateUserFirestoreIfDifferent(data: RegisterModel) {
        val user = FirestoreUser(
            uidFromLogin!!,
            data.email,
            data.name,
            data.id,
            data.birthday,
            profileImage.toString()
        )
        if (user.name != initialUser.name || user.id != initialUser.id || user.birthday != initialUser.birthday) {
            Log.d(TAG, "schimbare doc")
            firestoreViewModel.createOrUpdateFirestoreUser(user)
            firestoreViewModel.firestoreUser!!.observe(viewLifecycleOwner, Observer {
                if (it != "") errField.text = "${errField.text}\n$it"
            })
        }
    }

    fun updateImageIfDifferent() {
        if (profileImage.toString() != initialUser.profileUri) {
            Log.d(TAG, "schimbare imagine")
            val source = ImageDecoder.createSource(
                activity!!.contentResolver,
                profileImage!!
            )
            val bitmap = ImageDecoder.decodeBitmap(source)
            uploadImage(uidFromLogin.toString(), bitmap)
        }
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

        firestoreViewModel.createOrUpdateFirestoreUser(fsUser)
        firestoreViewModel.firestoreUser!!.observe(viewLifecycleOwner, Observer {
            if (it == "") {
                Log.d(TAG, "creat user in firestore: ${user.uid}")
                if (profileImage.toString() != Utils.DEFAULT_PROFILE_PICTURE) {
                    val source = ImageDecoder.createSource(activity!!.contentResolver, profileImage!!)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    uploadImage(user.uid!!, bitmap)
                } else {
                    val bundle = bundleOf("uid" to user.uid)
                    findNavController().navigate(R.id.register_self, bundle)
                }
            } else errField.text = it
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
        if (requestCode == PICK_IMAGE_REQUEST) {
            profileImage = data!!.data
            binding.profileImageUri = profileImage.toString()

            Log.d(TAG, binding.profileImageUri.toString())

            binding.executePendingBindings()
        }
    }

    fun uploadImage(uid: String, bitmap: Bitmap) {
        val storageViewModel: StorageViewModel = ViewModelProviders.of(this).get(StorageViewModel::class.java)

        storageViewModel.uploadImage(bitmap, uid)
        storageViewModel.imageUploaded!!.observe(viewLifecycleOwner, Observer {
            if (it.response == "") {
                Log.d(TAG, it.img.toString())

                firestoreViewModel.setImage(uid, it.img.toString())
                firestoreViewModel.imageUpdated!!.observe(viewLifecycleOwner, Observer {imgUpdated ->
                    if (imgUpdated.isNotBlank()) {
                        Log.d(TAG, imgUpdated)

                        // TODO recheck at some point later in the app flow (profile settings maybe)
                        //  if the file exists, and if so try again to set the uri
                        storageViewModel.deletePicture(uid)
                        storageViewModel.deletedPicture!!.observe(viewLifecycleOwner, Observer {del ->
                            if (del.isNotBlank()) {
                                // for periodic deletion?
                                firestoreViewModel.addUnusedFile(uid)
                                firestoreViewModel.fileAdded!!.observe(viewLifecycleOwner, Observer {
                                    if (it.isNotBlank()) {

                                    }
                                })
                            }
                        })
                    }
                })

                val bundle = bundleOf("uid" to uid)
                findNavController().navigate(R.id.register_self, bundle)
            }
        })
    }

    fun logOut() {
        authViewModel.logOut(activity!!)
        authViewModel.loggedOut!!.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Log.d(TAG, "logout esuat: $it")
            } else {
                findNavController().navigate(R.id.register_to_login)
            }
        })
    }


}
