package com.fiqi.galleryapp.data.db.firebase.storage

import android.net.Uri
import com.fiqi.galleryapp.data.model.FileModel
import com.fiqi.galleryapp.data.params.SuperParams
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.Continuation
import com.google.firebase.storage.FirebaseStorage

class GalleryStorage {
  private val storage = FirebaseStorage.getInstance().getReference("images_data")

  fun uploadFile(param: SuperParams<FileModel<Uri>>) {
    val filePath = storage.child(param.data?.name + "." + param.data?.format)

    filePath.putFile(param.data?.file!!)
      .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
        if (!task.isSuccessful) {
          task.exception?.let { exc ->
            throw exc
          }
        }
        return@Continuation filePath.downloadUrl
      })
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          val imageUrl = task.result.toString()
          val data = param.data
          data?.link = imageUrl
          param.onSucceeded(arrayListOf(data!!))
        } else {
          param.onFailed("Gagal mendapatkan url gambar")
        }
      }
      .addOnFailureListener { exc ->
        param.onFailed(exc.message!!)
      }
  }
}
