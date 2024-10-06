package com.example.mobileclub

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Carnet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carnet)

        val btnVolverCarnet = findViewById<Button>(R.id.btnVolverCarnet)
        btnVolverCarnet.setOnClickListener{
            NavigationUtils.navigateToActivity(this, Menu::class.java)
        }
    }
}