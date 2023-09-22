package com.example.spotifyporject.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class Song(
    val name: String?,
    val album: String?,
    val filePath: String?,
    val songDocId : String
)