package com.moodboardapp.sym_labo2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator


class Activity1 : AppCompatActivity() {

    private lateinit var scanButton: Button
    private lateinit var scanResultTitle: TextView
    private lateinit var scanResultImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_1)

        scanButton = findViewById(R.id.button_BarCode)
        scanResultTitle = findViewById(R.id.res_title_BarCode)
        scanResultImage = findViewById(R.id.res_image_barCode)

        scanButton.setOnClickListener{
            IntentIntegrator(this).initiateScan()
        }
    }

    // Get the results:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {

                //set text
                scanResultTitle.text = result.contents

                //set image

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}