package com.example.mobileclub

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Pago : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago)

        val btnVolverPago = findViewById<Button>(R.id.btnVolverPago)
        btnVolverPago.setOnClickListener{
            NavigationUtils.navigateToActivity(this, Menu::class.java)
        }
    }
}