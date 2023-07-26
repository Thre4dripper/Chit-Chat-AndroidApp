package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MarkFavourite {
    companion object {
        fun markAsFavourite(
            firestore: FirebaseFirestore,
            userModel: UserModel,
            favourite: String,
            onSuccess: (Boolean?) -> Unit
        ) {

            val favouriteList = userModel.favourites.toMutableList()
            if (favouriteList.contains(favourite)) {
                favouriteList.remove(favourite)
            } else {
                favouriteList.add(favourite)
            }
            val newUserModel = userModel.copy(favourites = favouriteList)
            firestore.collection(FirestoreCollections.USERS_COLLECTION)
                .document(userModel.username)
                .set(newUserModel, SetOptions.merge())
                .addOnSuccessListener {
                    onSuccess(true)
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }
    }
}