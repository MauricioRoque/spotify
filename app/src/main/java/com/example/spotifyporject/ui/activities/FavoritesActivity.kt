package com.example.spotifyporject.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spotifyporject.R
import com.example.spotifyporject.databinding.ActivityFavoritesBinding
import com.example.spotifyporject.models.Profile
import com.example.spotifyporject.models.Song
import com.example.spotifyporject.repositories.ProfileRepository
import com.example.spotifyporject.repositories.SongRepository
import com.example.spotifyporject.services.MediaPlayerService
import com.example.spotifyporject.ui.adapters.SongsAdapter
import com.example.spotifyporject.ui.viewmodels.SongListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FavoritesActivity : AppCompatActivity() {
    private lateinit var theAdapter: SongsAdapter
    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewmodel: SongListViewModel
    private lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewmodel = ViewModelProvider(this).get(SongListViewModel::class.java)
        auth = Firebase.auth
        profile = Profile()

        getFirestoreUserInfo()
        setUpEventListeners()
        setupRecyclerView()
        setupViewModelObservers()
    }

    private fun setupViewModelObservers() {
        viewmodel.songList.observe(this) {
            theAdapter.updateData(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewmodel.refreshSongList(profile.favoriteSongs)
    }

    private fun setupRecyclerView() {
        theAdapter = SongsAdapter(arrayListOf())
        theAdapter.setOnClickListener(object : SongsAdapter.OnClickListener {
            override fun onClick(position: Int, song: Song) {
                clickSong(song)
            }
        })

        theAdapter.setOnFavoriteClickListener(object : SongsAdapter.OnFavoriteClickListener {
            override fun onFavoriteClick(position: Int, song: Song) {
                clickFavorite(song, position)
            }
        })

        binding.rvFavoriteSongs.apply {
            adapter = theAdapter
            layoutManager = LinearLayoutManager(this@FavoritesActivity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
    }

    private fun setUpEventListeners() {
        binding.btnLogOutFavorites.setOnClickListener {
            showMenu(it)
        }
    }

    private fun clickSong(song: Song) {
        MediaPlayerService.clickedSong(song, onPrepared = {
            theAdapter.notifyDataSetChanged()
        })
    }

    private fun clickFavorite(song: Song, position: Int) {
        if (profile.favoriteSongs.contains(song.songDocId)) {
            ProfileRepository.removeFavoriteSong(profile.profileId, song.songDocId, success = {
                theAdapter.removeSong(position)
                Toast.makeText(this, "Canción removida de favoritos", Toast.LENGTH_SHORT).show()
            }, error = {
                Toast.makeText(this, "Error al remover canción de favoritos", Toast.LENGTH_SHORT).show()
            })
        } else {
            ProfileRepository.addFavoriteSong(profile.profileId, song.songDocId, success = {
                getFirestoreUserInfo()
                Toast.makeText(this, "Canción agregada a favoritos", Toast.LENGTH_SHORT).show()
            }, error = {
                Toast.makeText(this, "Error al agregar canción a favoritos", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun showMenu(v: android.view.View) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(R.menu.user_options, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.optnLogOut -> {
                    logOut()
                    true
                }
                else -> false
            }
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }

    private fun logOut() {
        auth.signOut()
        /*googleSignInClient.signOut()*/
        goToMainActivity()
    }

    private fun goToMainActivity() {
        val intent = MainActivity.intent(this)
        startActivity(intent)
        finish()
    }

    private fun getFirestoreUserInfo() {
        if (auth.currentUser == null) return
        val email = auth.currentUser?.email
        ProfileRepository.getProfileInfo(email!!, success = { profile ->
            binding.lblUserNameFavorites.text = profile.fullname
            this.profile = profile
            viewmodel.refreshSongList(profile.favoriteSongs)
        }, error = {
            Toast.makeText(this, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show()
        })
    }

    companion object {
        const val TAG = "FavoritesActivity"
        fun intent(context: Context): Intent {
            return Intent(context, FavoritesActivity::class.java)
        }
    }
}