package com.example.spotifyporject.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.spotifyporject.R
import com.example.spotifyporject.databinding.ActivityLogInBinding
import com.example.spotifyporject.repositories.FirebaseAuthRepository
import com.example.spotifyporject.repositories.GoogleRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var googleResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == GoogleRepository.RC_SIGN_IN || result.resultCode == GoogleRepository.RC_SIGN_IN_2) {
                val data: Intent? = result.data
                receivedGoogleSignInResult(data)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        googleSignInClient =
            GoogleRepository.getGoogleSignInClient(getString(R.string.default_web_client_id), this)

        setUpEventListeners()
    }

    private fun setUpEventListeners() {
        binding.btnLogIn.setOnClickListener {
            logIn()
        }

        binding.btnLogInGoogle.setOnClickListener {
            logInWithGoogle()
        }
    }

    private fun logInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleResultLauncher.launch(signInIntent)
    }

    private fun logIn() {
        val email = binding.txtLogInEmail.editText?.text.toString()
        val password = binding.txtLogInPassword.editText?.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Log.d(TAG, "User: ${user?.email}")
                    Toast.makeText(
                        this, "Authentication successful.",
                        Toast.LENGTH_SHORT
                    ).show()
                    goToHomePage()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun goToHomePage() {
        val intent = HomeActivity.intent(this)
        startActivity(intent)
    }

    private fun receivedGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val account = GoogleSignIn.getLastSignedInAccount(this) ?: return
        val email = account.email ?: return

        FirebaseAuthRepository.validateIfUserNotExists(this, email, userDontExistCallback = {
            googleSignInClient.signOut()
            Toast.makeText(this, "Error, user does not exist", Toast.LENGTH_SHORT).show()
        }, userExistCallback = {
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        goToHomePage()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this, "Error signing in", Toast.LENGTH_SHORT).show()
                        googleSignInClient.signOut()
                    }
                }
        }, error = {
            Toast.makeText(this, "Error validating user", Toast.LENGTH_SHORT).show()
        })
    }

    companion object {
        const val TAG = "LogInActivity"
        fun intent(context: Context): Intent {
            return Intent(context, LogInActivity::class.java)
        }
    }
}