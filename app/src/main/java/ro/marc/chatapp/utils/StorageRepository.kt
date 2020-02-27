package ro.marc.chatapp.utils

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.*
import ro.marc.chatapp.model.db.ImageData
import java.io.ByteArrayOutputStream

class StorageRepository {
    private val TAG = "ChatApp StorageRepository"

    private val rootRef = FirebaseStorage.getInstance().reference

    fun uploadImage(img: Bitmap, uid: String): MutableLiveData<ImageData> {
        val response = MutableLiveData<ImageData>()

        val baos = ByteArrayOutputStream()
        val storageRef = rootRef
            .child("pics/$uid")
        img.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        storageRef.putBytes(image)
            .addOnSuccessListener {
                storageRef.downloadUrl
                    .addOnSuccessListener { urlTask ->
                        urlTask.let {
                            response.value = ImageData(img, "")
                        }
                    }.addOnFailureListener { ex ->
                        response.value = ImageData(null, ex.message!!)
                    }
            }.addOnFailureListener {
                response.value = ImageData(null, it.message!!)
            }

        return response
    }
}