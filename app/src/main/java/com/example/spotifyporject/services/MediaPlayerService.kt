package com.example.spotifyporject.services

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.spotifyporject.models.Song

object MediaPlayerService {
    var isPlaying = false
    var playingSongFilePath = ""
    var mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
    }

    private fun formatSongPath(song: Song): String {
        val formattedFilePath = song.filePath?.replace("/", "%2F")

        return "https://firebasestorage.googleapis.com/v0/b/spotifymau-affcb.appspot.com/o/${formattedFilePath}?alt=media"
    }

    fun clickedSong(song: Song, onPrepared : () -> Unit) {
        if (isPlaying && playingSongFilePath == song.filePath) {
            mediaPlayer.pause()
            isPlaying = false
            onPrepared()
        } else if (!isPlaying && playingSongFilePath == song.filePath) {
            mediaPlayer.start()
            isPlaying = true
            onPrepared()
        } else {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(formatSongPath(song))
            mediaPlayer.setOnPreparedListener { mp ->
                mp?.start()
                isPlaying = true
                playingSongFilePath = song.filePath ?: ""
                onPrepared()
            }
            mediaPlayer.prepareAsync()
        }
    }
}