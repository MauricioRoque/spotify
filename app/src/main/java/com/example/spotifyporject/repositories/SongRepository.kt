package com.example.spotifyporject.repositories

import com.example.spotifyporject.models.Artist
import com.example.spotifyporject.models.Song
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object SongRepository {
    fun getSongsFromRefList(songsRefList: ArrayList<String>, success: (ArrayList<Song>) -> Unit, error: () -> Unit) {
        val db = Firebase.firestore
        val songs = arrayListOf<Song>()

        if (songsRefList.size == 0) {
            success(songs)
            return
        }

        db.collection("songs").whereIn(FieldPath.documentId(), songsRefList)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val song = Song(
                        document.data["name"].toString(),
                        document.data["album"].toString(),
                        document.data["filePath"].toString(),
                        document.id,
                    )
                    songs.add(song)
                }
                success(songs)
            }
            .addOnFailureListener {
                error()
            }
    }
}