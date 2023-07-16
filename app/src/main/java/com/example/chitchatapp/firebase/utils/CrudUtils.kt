package com.example.chitchatapp.firebase.utils

import com.google.firebase.firestore.FirebaseFirestore

class CrudUtils {
    companion object {
        fun getFirestoreDocument(
            firestore: FirebaseFirestore,
            collection: String,
            document: String,
            onSuccess: (HashMap<String, Any>?) -> Unit
        ) {
            firestore.collection(collection).document(document).get()
                .addOnSuccessListener {
                    onSuccess(it.data as HashMap<String, Any>?)
                }.addOnFailureListener {
                    onSuccess(null)
                }
        }

        fun createFirestoreDocument(
            firestore: FirebaseFirestore,
            collection: String,
            document: String,
            data: HashMap<String, Any>,
            success: (Boolean) -> Unit
        ) {
            firestore.collection(collection).document(document).set(data)
                .addOnSuccessListener {
                    success(true)
                }.addOnFailureListener {
                    success(false)
                }
        }

        fun updateFirestoreDocument(
            firestore: FirebaseFirestore,
            collection: String,
            document: String,
            data: HashMap<String, Any>,
            success: (Boolean) -> Unit
        ) {
            firestore.collection(collection).document(document).set(data)
                .addOnSuccessListener {
                    success(true)
                }.addOnFailureListener {
                    success(false)
                }
        }

        fun deleteFirestoreDocument(
            firestore: FirebaseFirestore,
            collection: String,
            document: String,
            success: (Boolean) -> Unit
        ) {
            firestore.collection(collection).document(document).delete()
                .addOnSuccessListener {
                    success(true)
                }.addOnFailureListener {
                    success(false)
                }
        }
    }
}