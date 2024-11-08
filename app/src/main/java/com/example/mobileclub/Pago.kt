package com.example.mobileclub

import android.content.ContentValues
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Pago : AppCompatActivity() {
    private lateinit var sociosHelper: SociosHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago)

        sociosHelper = SociosHelper(this)
        val edtDni = findViewById<EditText>(R.id.editTextText9)
        val btnBuscar = findViewById<Button>(R.id.button11)
        val txtNombreSocio = findViewById<TextView>(R.id.txtNombreSocio)
        val btnPagar = findViewById<Button>(R.id.btnPagar)

        btnBuscar.setOnClickListener {
            val dni = edtDni.text.toString().trim()
            val socio = sociosHelper.obtenerSocioPorDNI(dni)
            if (socio != null) {
                txtNombreSocio.text = "${socio["nombre"]} ${socio["apellido"]}"
                btnPagar.isEnabled = true // Habilitar botón de pago
            } else {
                Toast.makeText(this, "Socio no encontrado", Toast.LENGTH_SHORT).show()
            }
        }

        btnPagar.setOnClickListener {
            val dni = edtDni.text.toString().trim()
            val socio = sociosHelper.obtenerSocioPorDNI(dni)
            if (socio != null) {
                if (socio["tipoSocio"] == "ASOCIADO") {
                    actualizarFechaVencimiento(socio["id"]!!.toLong())
                    Toast.makeText(this, "Cuota mensual pagada", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Cuota diaria pagada", Toast.LENGTH_SHORT).show()
                }
                txtNombreSocio.text = ""
                edtDni.text.clear()
                btnPagar.isEnabled = false
            }
        }

        val btnVolverPago = findViewById<Button>(R.id.btnVolverPago)
        btnVolverPago.setOnClickListener{
            NavigationUtils.navigateToActivity(this, Menu::class.java)
        }
    }

    private fun actualizarFechaVencimiento(socioId: Long) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        val nuevaFechaVencimiento = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        val values = ContentValues().apply {
            put("fecha_vencimiento", nuevaFechaVencimiento)
        }
        sociosHelper.writableDatabase.update("socios", values, "id = ?", arrayOf(socioId.toString()))
    }
}