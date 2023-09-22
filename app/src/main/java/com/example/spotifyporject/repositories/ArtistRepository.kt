package com.example.spotifyporject.repositories

import android.util.Log
import com.example.spotifyporject.models.Artist
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ArtistRepository {
    fun getAllArtists(success: (ArrayList<Artist>) -> Unit, error: () -> Unit) {
        val db = Firebase.firestore
        val artists = arrayListOf<Artist>()

        db.collection("artists")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val artist = Artist(
                        document.data["name"].toString(),
                        document.data["genre"].toString(),
                        document.data["songs"] as ArrayList<String>,
                        document.id,
                        arrayListOf()
                    )
                    artists.add(artist)
                }
                success(artists)
            }
            .addOnFailureListener {
                Log.e("ARTIST", it.message.toString())
                error()
            }
    }

    fun getArtist(artistDocID: String, success: (Artist) -> Unit, error: () -> Unit) {
        val db = Firebase.firestore
        db.collection("artists").document(artistDocID)
            .get().addOnSuccessListener {
                val artist = Artist(
                    it.data?.get("name").toString(),
                    it.data?.get("genre").toString(),
                    it.data?.get("songs") as ArrayList<String>,
                    it.id,
                    arrayListOf()
                )
                success(artist)
            }.addOnFailureListener {
                Log.e("ARTIST", it.message.toString())
                error()
            }
    }
}