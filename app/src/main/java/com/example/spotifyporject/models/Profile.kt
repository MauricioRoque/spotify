package com.example.spotifyporject.models

class Profile (
    val email: String,
    val fullname: String,
    val profileId: String,
    val favoriteSongs : ArrayList<String> = arrayListOf()
) {
    constructor() : this("", "", "", arrayListOf())
}