package com.example.spotifyporject.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.spotifyporject.R
import com.example.spotifyporject.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getFirebaseToken()
        auth = Firebase.auth
        if (auth.currentUser != null) {

            goToHomePage()
        }

        setUpEventListeners()
    }

    private fun goToHomePage() {
        val intent = HomeActivity.intent(this)
        startActivity(intent)
    }

    private fun setUpEventListeners() {
        binding.btnToLogIn.setOnClickListener {
            goToLogIn()
        }

        binding.btnToSignIn.setOnClickListener {
            goToSignIn()
        }

        binding.btnError.setOnClickListener {
            throwError()
        }
    }

    private fun throwError() {
        Toast.makeText(this, "Error thrown", Toast.LENGTH_SHORT).show()
        throw RuntimeException("Test Crash")
    }

    private fun goToSignIn() {
        val intent = SignUpActivity.intent(this)
        startActivity(intent)
    }

    private fun goToLogIn() {
        val intent = LogInActivity.intent(this)
        startActivity(intent)
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg)
            /*Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()*/
        })
    }

    companion object {
        const val TAG = "MainActivity"
        fun intent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}