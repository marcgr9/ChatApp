package ro.marc.chatapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_register.*
import ro.marc.chatapp.R
import ro.marc.chatapp.viewmodel.RegisterViewModel
import ro.marc.chatapp.databinding.FragmentRegisterBinding
import ro.marc.chatapp.utils.Utils

class Register : Fragment() {

    lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.executePendingBindings()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel: RegisterViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

        viewModel.errors.observe(this, Observer {
            var errors = ""

            it.forEach { err ->
                val stringId: Int = when (err) {
                    Utils.CredentialErrors.ERREmail -> R.string.ERREmail
                    Utils.CredentialErrors.ERRId -> R.string.ERRId
                    Utils.CredentialErrors.ERRPassword -> R.string.ERRPassword
                    Utils.CredentialErrors.ERRDateFormat -> R.string.ERRDateFormat
                    Utils.CredentialErrors.ERRDateAge -> R.string.ERRDateAge
                    Utils.CredentialErrors.ERRName -> R.string.ERRName
                    else -> 0
                }

                errors += getString(stringId)
                errors += "\n"
            }

            errField.text = errors
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.registerViewModel = viewModel
    }


}
