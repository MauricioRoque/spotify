package com.example.spotifyporject.repositories

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object FirebaseAuthRepository {
    public fun validateIfUserNotExists(
        activity: Activity,
        email: String,
        userDontExistCallback: () -> Unit,
        userExistCallback: () -> Unit,
        error: (e: java.lang.Exception?) -> Unit
    ) {
        val auth = Firebase.auth

        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList<String>()
                if (signInMethods.isNotEmpty()) {
                    userExistCallback()
                } else {
                    userDontExistCallback()
                }
            } else {
                Log.e(
                    "validateIfUserNotExists",
                    "Error getting sign in methods for user",
                    task.exception
                )
                error(task.exception)
            }
        }
    }
}