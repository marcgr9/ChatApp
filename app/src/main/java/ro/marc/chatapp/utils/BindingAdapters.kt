package ro.marc.chatapp.utils

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

class BindingAdapters {

    companion object {
        @JvmStatic
        @BindingAdapter("profileImage")
        fun loadImage(view: ImageView, uri: Uri?) {
            //println("marc loadImage")
            if (uri != null) {
                Glide.with(view.context)
                    .load(uri)
                    .into(view)
            }
        }
    }

}