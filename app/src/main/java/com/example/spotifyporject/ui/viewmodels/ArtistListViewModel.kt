package com.example.spotifyporject.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spotifyporject.models.Artist
import com.example.spotifyporject.repositories.ArtistRepository

class ArtistListViewModel: ViewModel() {
    private var _artistList: MutableLiveData<ArrayList<Artist>> = MutableLiveData(arrayListOf())
    val artistList: LiveData<ArrayList<Artist>> get() = _artistList

    fun refreshArtistList() {
        ArtistRepository.getAllArtists(
            success = {
                _artistList.value = it
            },
            error = {
                _artistList.value = arrayListOf()
            }
        )
    }
}