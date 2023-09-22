package com.example.spotifyporject.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.spotifyporject.R
import com.example.spotifyporject.databinding.ActivitySignUpBinding
import com.example.spotifyporject.repositories.FirebaseAuthRepository
import com.example.spotifyporject.repositories.GoogleRepository
import com.example.spotifyporject.repositories.ProfileRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
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
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        setUpEventListeners()
        googleSignInClient =GoogleRepository.getGoogleSignInClient(getString(R.string.default_web_client_id) , this)
    }

    private fun setUpEventListeners() {
        binding.btnSignIn.setOnClickListener {
            signIn()
        }

        binding.btnSignUpGoogle.setOnClickListener {
            signUpWithGoogle()
        }
    }

    private fun signUpWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleResultLauncher.launch(signInIntent)
    }

    private fun validateForm(): Boolean {
        return binding.txtSignInEmail.editText?.text.toString().isEmpty() || binding.txtSignInPassword.editText?.text.toString()
            .isEmpty() || binding.txtSignInFullName.editText?.text.toString().isEmpty()
    }

    private fun signIn() {
        if (validateForm()) {
            Toast.makeText(this, "Email, password and fullName can't be empty", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val email = binding.txtSignInEmail.editText?.text.toString()
        val password = binding.txtSignInPassword.editText?.text.toString()
        val fullName = binding.txtSignInFullName.editText?.text.toString()

        FirebaseAuthRepository.validateIfUserNotExists(
            this,
            email,
            userDontExistCallback = {
                createUser(email, password, fullName)
            },
            userExistCallback = {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
            },
            error = {
                Toast.makeText(this, "Error: ${it?.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun createUser(email: String, password: String, fullName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Created and logged in", Toast.LENGTH_SHORT)
                        .show()
                    ProfileRepository.registerProfile(
                        fullName,
                        email,
                        success = {
                            goToHomePage()
                        },
                        error = {
                            Toast.makeText(
                                this,
                                "Error registering profile",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        })
                    goToHomePage()
                } else {
                    Log.e("RegisterActivity", "Error registering user", task.exception)
                    Toast.makeText(
                        this,
                        "Error registering user",
                        Toast.LENGTH_SHORT
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
        val fullName = account.displayName ?: "No name"

        FirebaseAuthRepository.validateIfUserNotExists(this, email, userDontExistCallback = {
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        ProfileRepository.registerProfile(
                            fullName,
                            email,
                            success = {
                                goToHomePage()
                            },
                            error = {
                                Toast.makeText(
                                    this,
                                    "Error registering profile",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            })
                        goToHomePage()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                    }
                }
        }, userExistCallback = {
            Toast.makeText(this, "This user is already registered", Toast.LENGTH_SHORT)
                .show()
        }, error = {
            Toast.makeText(this, "Error validating user", Toast.LENGTH_SHORT).show()
        })
    }

    companion object {
        const val TAG = "SignUpActivity"
        fun intent(context: Context): Intent {
            return Intent(context, SignUpActivity::class.java)
        }
    }
}