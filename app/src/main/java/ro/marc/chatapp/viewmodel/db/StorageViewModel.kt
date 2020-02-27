package ro.marc.chatapp.viewmodel.db

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.marc.chatapp.model.db.ImageData
import ro.marc.chatapp.utils.StorageRepository

class StorageViewModel: ViewModel() {
    private val storageRepository = StorageRepository()

    var imageUploaded: MutableLiveData<ImageData>? = null
    fun uploadImage(img: Bitmap, uid: String) {
        imageUploaded = storageRepository.uploadImage(img, uid)
    }
}