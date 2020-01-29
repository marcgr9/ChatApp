package ro.marc.chatapp.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_login.*
import ro.marc.chatapp.databinding.FragmentLoginBinding
import ro.marc.chatapp.viewmodel.RegisterViewModel
import ro.marc.chatapp.databinding.FragmentRegisterBinding
import ro.marc.chatapp.viewmodel.LoginViewModel
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_login.errField
import kotlinx.android.synthetic.main.fragment_register.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Register.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Register.newInstance] factory method to
 * create an instance of this fragment.
 */
class Login : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    val errorId = "ERRLogin"

    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.executePendingBindings()

        return binding.getRoot()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        viewModel.hasError().observe(this, Observer {
            if (it == true) errField.text = getString(resources.getIdentifier(errorId , "string", "ro.marc.chatapp"))
            else errField.text = ""

        })

        viewModel.clicked.observe(this, Observer {
            goToRegister()
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.loginViewModel = viewModel

    }

    fun goToRegister() {
        val navController = Navigation.findNavController(activity!!,
            ro.marc.chatapp.R.id.navHostFragment
        )
        navController.navigate(ro.marc.chatapp.R.id.action_login_to_register)
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
