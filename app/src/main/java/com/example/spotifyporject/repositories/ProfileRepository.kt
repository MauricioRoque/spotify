package com.example.spotifyporject.repositories

import android.util.Log
import com.example.spotifyporject.models.Profile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ProfileRepository {
    fun getProfileInfo(email: String, success: (profile: Profile) -> Unit, error: () -> Unit) {
        val db = Firebase.firestore
        db.collection("profiles").whereEqualTo("email", email)
            .get().addOnSuccessListener {
                val fullName = it.documents[0].get("fullname")
                val profileId = it.documents[0].id
                val profile = Profile(
                    email,
                    fullName.toString(),
                    profileId,
                    it.documents[0].get("favoriteSongs") as ArrayList<String>? ?: arrayListOf()
                )

                success(profile)
            }.addOnFailureListener {
                Log.e("PROFILE", it.message.toString())
                error()
            }
    }

    fun registerProfile(fullName: String, email: String, success: () -> Unit, error: () -> Unit) {
        val profile = hashMapOf(
            "email" to email,
            "fullname" to fullName,
        )
        val db = Firebase.firestore

        try {
            db.collection("profiles")
                .add(profile)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "RegisterProfile",
                        "DocumentSnapshot added with ID: ${documentReference.id}"
                    )
                    success()
                }
                .addOnFailureListener { e ->
                    Log.w("RegisterProfile", "Error adding document", e)
                    error()
                }
        } catch (e: Exception) {
            Log.e("RegisterProfile", "Error adding document", e)
        }
    }

    fun addFavoriteSong(profileDocId: String, songDocId: String, success: () -> Unit, error: () -> Unit) {
        val db = Firebase.firestore
        db.collection("profiles").document(profileDocId)
            .update("favoriteSongs", FieldValue.arrayUnion(songDocId))
            .addOnSuccessListener {
                success()
            }.addOnFailureListener {
                Log.e("PROFILE", it.message.toString())
                error()
            }
    }

    fun removeFavoriteSong(profileDocId: String, songDocId: String, success: () -> Unit, error: () -> Unit) {
        val db = Firebase.firestore
        db.collection("profiles").document(profileDocId)
            .update("favoriteSongs", FieldValue.arrayRemove(songDocId))
            .addOnSuccessListener {
                success()
            }.addOnFailureListener {
                Log.e("PROFILE", it.message.toString())
                error()
            }
    }
}