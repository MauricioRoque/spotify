package com.example.spotifyporject.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class Artist(
    val name: String?,
    val genre: String?,
    val songRefs: ArrayList<String>?,
    val artistDocId: String?,
    val songs: ArrayList<Song>? = arrayListOf()
) {
    constructor() : this("", "", arrayListOf(), "", arrayListOf())
}