package com.moodboardapp.sym_labo2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class Activity3 : AppCompatActivity() {
    /* NFC */
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var connectButton: Button

    // Account Data
    private var myUsername = "User"
    private var verySecretPassword = "s3cret"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        connectButton = findViewById(R.id.connect_button)

        connectButton.setOnClickListener {
            // Credentials test (ugly but not the focus for this lab)
            if(usernameInput.text.isEmpty() || usernameInput.text.trim().toString() != myUsername) {
                usernameInput.error = "Invalid username"
            } else if(passwordInput.text.isEmpty() || passwordInput.text.trim().toString() != verySecretPassword) {
                passwordInput.error = "Invalid password"
            } else {
                // User connected : launch NFC activity
                val intent = Intent(this, NFCActivity::class.java)
                startActivity(intent)
            }
        }

    }

    companion object {
        private const val TAG: String = "Activity3"
    }
}