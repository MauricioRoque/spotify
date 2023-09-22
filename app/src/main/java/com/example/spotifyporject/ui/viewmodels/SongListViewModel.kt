package com.example.spotifyporject.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spotifyporject.models.Profile
import com.example.spotifyporject.models.Song
import com.example.spotifyporject.repositories.SongRepository

class SongListViewModel: ViewModel() {
    private var _songList: MutableLiveData<ArrayList<Song>> = MutableLiveData(arrayListOf())
    val songList: LiveData<ArrayList<Song>> get() = _songList

    fun refreshSongList(songRefs: ArrayList<String>) {
        SongRepository.getSongsFromRefList(
            songRefs,
            success = {
                _songList.value = it
            },
            error = {
                _songList.value = arrayListOf()
            }
        )
    }
}