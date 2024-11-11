// Menu.kt
package com.example.mobileclub

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.text.SimpleDateFormat
import java.util.*

class Menu : AppCompatActivity() {
    private lateinit var sociosHelper: SociosHelper
    private lateinit var cardInfoSocio: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        sociosHelper = SociosHelper(this)
        cardInfoSocio = findViewById(R.id.cardInfoSocio)

        val btnIrRegistro = findViewById<Button>(R.id.btnIrRegistro)
        val btnIrGenerarCarnet = findViewById<Button>(R.id.btnIrGenerarCarnet)
        val btnIrPago = findViewById<Button>(R.id.btnIrPago)
        val btnIrListar = findViewById<Button>(R.id.btnIrListar)
        val editTextDNI = findViewById<EditText>(R.id.editTextText)
        val btnBuscar = findViewById<Button>(R.id.button2)

        // Configurar botones de navegación
        btnIrRegistro.setOnClickListener {
            NavigationUtils.navigateToActivity(this, Registro::class.java)
        }
        btnIrGenerarCarnet.setOnClickListener {
            NavigationUtils.navigateToActivity(this, Carnet::class.java)
        }
        btnIrPago.setOnClickListener {
            NavigationUtils.navigateToActivity(this, Pago::class.java)
        }
        btnIrListar.setOnClickListener {
            NavigationUtils.navigateToActivity(this, ListarVenc::class.java)
        }

        // Configurar búsqueda
        btnBuscar.setOnClickListener {
            val dni = editTextDNI.text.toString().trim()
            if (dni.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese un DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            buscarYMostrarSocio(dni)
        }
    }

    private fun buscarYMostrarSocio(dni: String) {
        val socio = sociosHelper.obtenerSocioPorDNI(dni)
        if (socio == null) {
            Toast.makeText(this, "No se encontró ningún socio con ese DNI", Toast.LENGTH_SHORT).show()
            cardInfoSocio.visibility = View.GONE
            return
        }

        // Mostrar y actualizar CardView con la información
        findViewById<TextView>(R.id.tvNombreCompleto).text = "${socio["nombre"]} ${socio["apellido"]}"
        findViewById<TextView>(R.id.tvDNI).text = "DNI: ${socio["dni"]}"
        findViewById<TextView>(R.id.tvTipoSocio).text =
            "Tipo: ${if (socio["tipoSocio"] == "ASOCIADO") "Socio" else "Particular"}"

        val fechaVencimiento = socio["fechaVencimiento"] ?: calcularProximoVencimiento()
        findViewById<TextView>(R.id.tvFechaVencimiento).text = "Vencimiento: $fechaVencimiento"

        // Configurar botones
        findViewById<Button>(R.id.btnIrAPagar).setOnClickListener {
            val intent = Intent(this, Pago::class.java).apply {
                putExtra("SOCIO_ID", socio["id"]!!.toLong())
                putExtra("TIPO_SOCIO", socio["tipoSocio"])
            }
            startActivity(intent)
        }


        findViewById<Button>(R.id.btnGenerarCarnet).setOnClickListener {
            val intent = Intent(this, Carnet::class.java).apply {
                putExtra("SOCIO_ID", socio["id"]!!.toLong())
                putExtra("DNI", socio["dni"])
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnClose).setOnClickListener {
            cardInfoSocio.visibility = View.GONE
        }

        // Mostrar el CardView
        cardInfoSocio.visibility = View.VISIBLE

    }


    private fun calcularProximoVencimiento(): String {
        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.DAY_OF_MONTH) > 10) {
            calendar.add(Calendar.MONTH, 1)
        }
        calendar.set(Calendar.DAY_OF_MONTH, 10)
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
    }
}