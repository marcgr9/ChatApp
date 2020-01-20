package ro.marc.chatapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), Register.OnFragmentInteractionListener, Splash.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        println("interact")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        var binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//
//        binding.setRegisterViewModel(RegisterViewModel())
//        binding.executePendingBindings()
    }

}
