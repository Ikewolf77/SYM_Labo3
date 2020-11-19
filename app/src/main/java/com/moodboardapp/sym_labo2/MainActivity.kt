package com.moodboardapp.sym_labo2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var activity1Launcher: Button
    private lateinit var activity2Launcher: Button
    private lateinit var activity3Launcher: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity1Launcher = findViewById(R.id.activity1)
        activity2Launcher = findViewById(R.id.activity2)
        activity3Launcher = findViewById(R.id.activity3)

        activity1Launcher.setOnClickListener {
            val intent = Intent(this, Activity1::class.java)
            startActivity(intent)
        }
        activity2Launcher.setOnClickListener {
            val intent = Intent(this, Activity2::class.java)
            startActivity(intent)
        }
        activity3Launcher.setOnClickListener {
            val intent = Intent(this, Activity3::class.java)
            startActivity(intent)
        }
    }
}