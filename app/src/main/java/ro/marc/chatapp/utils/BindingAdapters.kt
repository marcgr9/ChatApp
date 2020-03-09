package ro.marc.chatapp.utils

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class BindingAdapters {

    companion object {
        @JvmStatic
        @BindingAdapter("profileImage")
        fun loadImage(view: ImageView, uri: String?) {
            if (uri != null) {
                Log.d("ChatApp Bindingadapters", uri)
                Glide.with(view.rootView.context)
                    .load(uri)
                    .circleCrop()
                    .apply(RequestOptions().override(512, 512))
                    .into(view)
            }
        }
    }

}