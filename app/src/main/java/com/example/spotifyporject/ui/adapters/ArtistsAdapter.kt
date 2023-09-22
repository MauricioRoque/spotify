package com.example.spotifyporject.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifyporject.R
import com.example.spotifyporject.databinding.ArtistItemBinding
import com.example.spotifyporject.models.Artist

class ArtistsAdapter(
    val artists: MutableList<Artist>,
) : RecyclerView.Adapter<ArtistsAdapter.ArtistItemViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowView = inflater.inflate(R.layout.artist_item, parent, false)

        return ArtistItemViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return artists.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: ArtistItemViewHolder, position: Int) {
        val artist = artists[position]

        holder.bind(artist)
        holder.itemView.setOnClickListener{
            onClickListener?.onClick(position, artist)
        }
    }

    fun updateData(artistList: ArrayList<Artist>) {
        artists.clear()
        artists.addAll(artistList)
        notifyItemRangeChanged(0, artistList.size)
    }

    interface OnClickListener {
        fun onClick(position: Int,artist: Artist)
    }

    class ArtistItemViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding = ArtistItemBinding.bind(itemView)

        fun bind(artist: Artist) {
            binding.lblArtistName.text = artist.name
            binding.lblArtistGenre.text = artist.genre
            binding.imgArtist.setImageResource(R.drawable.artist_default_image)
        }

    }
}