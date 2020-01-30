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

        val viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

        viewModel.getErrors().observe(this, Observer {
            Log.d("test", "onViewCreated $it")

            var errors: String = ""

            it.forEach { err ->

                val stringId = when (err) {
                    RegisterViewModel.Errors.ERREmail -> R.string.ERREmail
                    RegisterViewModel.Errors.ERRID -> R.string.ERRID
                    RegisterViewModel.Errors.ERRPassword -> R.string.ERRPassword
                    RegisterViewModel.Errors.ERRDate -> R.string.ERRDate
                    RegisterViewModel.Errors.ERRName -> R.string.ERRName


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
