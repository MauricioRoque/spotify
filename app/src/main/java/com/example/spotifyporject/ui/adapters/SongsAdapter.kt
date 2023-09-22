package com.example.spotifyporject.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifyporject.R
import com.example.spotifyporject.databinding.ArtistItemBinding
import com.example.spotifyporject.databinding.SongItemBinding
import com.example.spotifyporject.models.Artist
import com.example.spotifyporject.models.Song
import com.example.spotifyporject.services.MediaPlayerService

class SongsAdapter(
    val songs: MutableList<Song>,
) : RecyclerView.Adapter<SongsAdapter.SongItemViewHolder>() {
    private var onClickListener: OnClickListener? = null
    private var onFavoriteClickListener: OnFavoriteClickListener? = null
    private var userFavoriteSongs = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowView = inflater.inflate(R.layout.song_item, parent, false)

        return SongItemViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    fun removeSong(position: Int) {
        songs.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateFavoriteSongs(userFavoriteSongs: ArrayList<String>) {
        this.userFavoriteSongs = userFavoriteSongs
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun setOnFavoriteClickListener(onFavoriteClickListener: OnFavoriteClickListener) {
        this.onFavoriteClickListener = onFavoriteClickListener
    }

    override fun onBindViewHolder(holder: SongItemViewHolder, position: Int) {
        val song = songs[position]

        holder.bind(song, onFavoriteClickListener?: return, onClickListener?: return,
            userFavoriteSongs.contains(song.songDocId))
        holder.itemView.setOnClickListener{
            onClickListener?.onClick(position, song)
        }
    }

    fun updateData(songList: ArrayList<Song>) {
        songs.clear()
        songs.addAll(songList)
        notifyItemRangeChanged(0, songList.size)
    }

    interface OnClickListener {
        fun onClick(position: Int, song: Song)
    }

    interface OnFavoriteClickListener {
        fun onFavoriteClick(position: Int, song: Song)
    }

    class SongItemViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding = SongItemBinding.bind(itemView)

        fun bind(song: Song, onFavoriteClickListener: OnFavoriteClickListener, onClickListener: OnClickListener, favorite : Boolean) {
            binding.lblSongName.text = song.name
            binding.lblSongAlbum.text = song.album
            binding.imgSong.setImageResource(R.drawable.music_note_icon_song_melody_tune_flat_symbol_free_vector)
            binding.btnFavorite.setOnClickListener {
                onFavoriteClickListener.onFavoriteClick(adapterPosition, song)
            }

            binding.btnPlayPause.setOnClickListener {
                onClickListener.onClick(adapterPosition, song)
            }

            if (MediaPlayerService.isPlaying && MediaPlayerService.playingSongFilePath == song.filePath) {
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            } else {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            }

            if (favorite) {
                binding.btnFavorite.setImageResource(R.drawable.ic_heart)
            } else {
                binding.btnFavorite.setImageResource(R.drawable.ic_unfavorite)
            }
        }

    }
}