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
import ro.marc.chatapp.viewmodel.RegisterViewModel
import ro.marc.chatapp.databinding.FragmentRegisterBinding

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
class Register : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    //problema de aici este ca ai folosit 2 obiecte de tip RegisterViewModel
    //in onCreateView initializezi prin constructor, iar in onViewCreated utilizezi ViewModelProviders
    //ideea e ca la binding tu ai dat primul VM, si il observai pe al doilea, iar modificarile apar numai in primul
    //Rezolvare: 1. binding variabila globala care se initializeaza in onCreateView
    //           2. Codul din onViewCreated se muta in onActivityCreaded (metoda asta asgura faptul ca in activitatea din care face partea fragmentul a fost aplecat on create)
    //           3. viewModel creat in onActivityCreaded este utilizat de binding, (1) si (2) se muta in onActivityCreaded si la (2) se utilizeaza modelul creat de ViewModelProviders

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentRegisterBinding =
            FragmentRegisterBinding.inflate(inflater, container, false)

        val registerViewModel = RegisterViewModel()

        binding.lifecycleOwner = viewLifecycleOwner  // (1)
        binding.registerViewModel = registerViewModel // (2)

        binding.executePendingBindings()

        registerViewModel.getErrors()
            .observe(viewLifecycleOwner, Observer { Log.d("test", "onCreateView $it") })

        println("onCreate")


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("onViewCreated")
        val viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

        viewModel.getErrors().observe(this, Observer {
            Log.d("test", "onViewCreated $it")
        })
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Register.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Register().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}