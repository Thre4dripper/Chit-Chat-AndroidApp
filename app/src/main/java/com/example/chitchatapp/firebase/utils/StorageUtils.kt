package com.example.chitchatapp.firebase.utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class StorageUtils {
    companion object {
        fun getUrlFromStorage(
            storage: FirebaseStorage,
            path: String,
            imageUri: Uri,
            callback: (String?) -> Unit
        ) {
            val ref = storage.reference.child(path)
            ref.putFile(imageUri).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }.addOnFailureListener {
                callback(null)
            }
        }
    }
}