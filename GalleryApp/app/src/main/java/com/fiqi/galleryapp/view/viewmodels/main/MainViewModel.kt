package com.fiqi.galleryapp.view.viewmodels.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fiqi.galleryapp.data.db.firebase.firestore.GalleryFirestore
import com.fiqi.galleryapp.data.db.firebase.storage.GalleryStorage
import com.fiqi.galleryapp.data.model.FileModel
import com.fiqi.galleryapp.data.model.ImageModel
import com.fiqi.galleryapp.data.params.SuperParams
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {
  private val galleryFirestore = GalleryFirestore()
  private val galleryStorage = GalleryStorage()

  private val _images = MutableLiveData<ArrayList<ImageModel>>()

  fun getImages(): LiveData<ArrayList<ImageModel>> = _images

  fun getImagesData() {
    CoroutineScope(Dispatchers.Main + Job()).launch {
      galleryFirestore.read(
        SuperParams(
          onSucceeded = { data ->
            _images.value = data
          },
          onFailed = ::setFailureMessage
        )
      )
    }
  }

  fun insertImagesData(title: String, imageUri: Uri, imageFormat: String, desc: String) {
    CoroutineScope(Dispatchers.Main + Job()).launch {
      galleryStorage.uploadFile(
        SuperParams(
          data = FileModel(
            file = imageUri,
            name = title,
            format = imageFormat
          ),
          onSucceeded = { data ->
            val file = data!![0]
            galleryFirestore.insert(
              SuperParams(
                data = ImageModel(
                  title = title,
                  link = file.link,
                  desc = desc
                ),
                onSucceeded = { setSucceededMessage("Gambar berhasil ditambahkan") },
                onFailed = ::setFailureMessage
              )
            )
          },
          onFailed = ::setFailureMessage
        )
      )
    }
  }

  fun deleteImagesData(id: String) {
    CoroutineScope(Dispatchers.Main + Job()).launch {
      galleryFirestore.delete(
        SuperParams(
          data = id,
          onSucceeded = { setSucceededMessage("Gambar berhasil dihapus") },
          onFailed = ::setFailureMessage
        )
      )
    }
  }

  private val _image = MutableLiveData<ImageModel>()

  fun getImage(): LiveData<ImageModel> = _image

  fun setImageData(data: ImageModel) = _image.postValue(data)

  private val _datestamp = MutableLiveData<String>()

  fun setDatestampData(datestamp: String) = _datestamp.postValue(datestamp)

  fun getDatestamp(): LiveData<String> = _datestamp

  private val _imageUri = MutableLiveData<Uri>()

  fun setImageUriData(imageUri: Uri) = _imageUri.postValue(imageUri)

  fun getImageUri(): LiveData<Uri> = _imageUri

  private val _imageMimeType = MutableLiveData<String>()

  fun setImageMimeTypeData(imageUri: String) = _imageMimeType.postValue(imageUri)

  fun getImageMimeType(): LiveData<String> = _imageMimeType

  private val _succeededMessage = MutableLiveData<String>()

  fun setSucceededMessage(message: String?) = _succeededMessage.postValue(message)

  fun getSucceededMessage(): LiveData<String> = _succeededMessage

  private val _failureMessage = MutableLiveData<String>()

  fun setFailureMessage(message: String?) = _failureMessage.postValue(message)

  fun getFailureMessage(): LiveData<String> = _failureMessage
}
