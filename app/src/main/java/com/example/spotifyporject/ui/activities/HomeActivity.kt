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
import com.example.spotifyporject.databinding.ActivityHomeBinding
import com.example.spotifyporject.models.Artist
import com.example.spotifyporject.repositories.ProfileRepository
import com.example.spotifyporject.ui.adapters.ArtistsAdapter
import com.example.spotifyporject.ui.viewmodels.ArtistListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    private lateinit var theAdapter: ArtistsAdapter
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewmodel: ArtistListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewmodel = ViewModelProvider(this).get(ArtistListViewModel::class.java)
        auth = Firebase.auth

        getFirestoreUserInfo()
        setUpEventListeners()
        setupRecyclerView()
        setupViewModelObservers()
    }

    private fun setupViewModelObservers() {
        viewmodel.artistList.observe(this) {
            theAdapter.updateData(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewmodel.refreshArtistList()
    }

    private fun setupRecyclerView() {
        theAdapter = ArtistsAdapter(arrayListOf())
        theAdapter.setOnClickListener(object : ArtistsAdapter.OnClickListener {
            override fun onClick(position: Int, artist: Artist) {
                clickArtist(artist)
            }
        })

        binding.rvArtists.apply {
            adapter = theAdapter
            layoutManager = LinearLayoutManager(this@HomeActivity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
    }

    private fun setUpEventListeners() {
        binding.btnLogOut.setOnClickListener {
            showMenu(it)
        }

        binding.btnFavorites.setOnClickListener {
            goToFavoritesActivity()
        }
    }

    private fun clickArtist(artist: Artist) {
        val intent = ArtistDetailActivity.intent(this)
        intent.putExtra("artistDocId", artist.artistDocId)
        startActivity(intent)
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

    private fun goToFavoritesActivity() {
        val intent = FavoritesActivity.intent(this)
        startActivity(intent)
    }

    private fun getFirestoreUserInfo() {
        if (auth.currentUser?.email == null) {
            goToMainActivity()
            return
        }
        val email = auth.currentUser?.email
        ProfileRepository.getProfileInfo(email!!, success = { profile ->
            binding.lblUserName.text = profile.fullname
        }, error = {
            Toast.makeText(this, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show()
        })
    }

    companion object {
        const val TAG = "HomeActivity"
        fun intent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }
}