package ro.marc.chatapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_main.*
import ro.marc.chatapp.R
import ro.marc.chatapp.viewmodel.db.FirestoreViewModel

class Main : Fragment() {

    private val TAG = "ChatApp Main"
    private lateinit var firestoreViewModel: FirestoreViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel::class.java)

        search.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                firestoreViewModel.getByName(s.toString())
                subscribe()

            }
        })
    }

    fun subscribe() {
        if (!firestoreViewModel.usersByName!!.hasActiveObservers()) {
            println("ed")
            firestoreViewModel.usersByName!!.observe(viewLifecycleOwner, Observer {
                Log.d(TAG, "it-ul e $it")

            })
        }
    }
}
