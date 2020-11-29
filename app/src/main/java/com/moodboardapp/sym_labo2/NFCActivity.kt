package com.moodboardapp.sym_labo2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class NFCActivity : AppCompatActivity() {
    /* NFC */

    private lateinit var highSecurityButton: Button
    private lateinit var mediumSecurityButton: Button
    private lateinit var lowSecurityButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)

        highSecurityButton = findViewById(R.id.high_security)
        mediumSecurityButton = findViewById(R.id.medium_security)
        lowSecurityButton = findViewById(R.id.low_security)

        highSecurityButton.setOnClickListener {
            // todo
        }
        mediumSecurityButton.setOnClickListener {
            // todo
        }
        lowSecurityButton.setOnClickListener {
            // todo
        }
    }
}