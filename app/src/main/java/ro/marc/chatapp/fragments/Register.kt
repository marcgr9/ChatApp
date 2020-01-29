package ro.marc.chatapp.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_register.*
import ro.marc.chatapp.viewmodel.RegisterViewModel
import ro.marc.chatapp.databinding.FragmentRegisterBinding

class Register : Fragment() {
    private var listener: OnFragmentInteractionListener? = null


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
                errors += getString(resources.getIdentifier(err.toString() , "string", "ro.marc.chatapp"))
                errors += "\n"
            }

            errField.text = errors
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.registerViewModel = viewModel

    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}
