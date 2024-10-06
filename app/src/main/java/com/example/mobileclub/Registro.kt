package com.example.mobileclub

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

    val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener{
            NavigationUtils.navigateToActivity(this, Menu::class.java)
        }
    }
}