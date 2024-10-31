package com.example.mobileclub

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Carnet : AppCompatActivity() {
    private lateinit var sociosHelper: SociosHelper
    private lateinit var cardViewCarnet: CardView
    private lateinit var pdfService: PdfService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)

        sociosHelper = SociosHelper(this)
        pdfService = PdfService(this)

        // Inicializar vistas
        val edtDNI = findViewById<EditText>(R.id.editTextText9)
        val btnBuscar = findViewById<Button>(R.id.button11)
        val btnVolverCarnet = findViewById<Button>(R.id.btnVolverCarnet)
        cardViewCarnet = findViewById(R.id.cardViewCarnet)

        btnBuscar.setOnClickListener {
            val dni = edtDNI.text.toString().trim()
            if (dni.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese un DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            buscarSocio(dni)
        }

        btnVolverCarnet.setOnClickListener {
            NavigationUtils.navigateToActivity(this, Menu::class.java)
        }

        // Configurar botón de generar PDF del carnet
        findViewById<Button>(R.id.btnGenerarPDFCarnet).setOnClickListener {
            generarPDFCarnet(edtDNI.text.toString().trim())
        }
    }

    // Carnet.kt
    private fun buscarSocio(dni: String) {
        val socio = sociosHelper.obtenerSocioPorDNI(dni)
        if (socio != null && socio["tipoSocio"] == "ASOCIADO") {
            findViewById<CardView>(R.id.cardViewCarnet).visibility = View.VISIBLE
            mostrarCarnet(socio)
        } else {
            findViewById<CardView>(R.id.cardViewCarnet).visibility = View.GONE
            Toast.makeText(this,
                "No se encontró un socio asociado con ese DNI",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarCarnet(socio: Map<String, String>) {
        // Actualizar datos del carnet
        findViewById<TextView>(R.id.tvNombreCarnet).text = "${socio["nombre"]} ${socio["apellido"]}"
        findViewById<TextView>(R.id.tvDNICarnet).text = "DNI: ${socio["dni"]}"

        // Fecha actual formateada
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.tvFechaCarnet).text = "Fecha: ${sdf.format(Date())}"
    }

    private fun generarPDFCarnet(dni: String) {
        try {
            val socio = sociosHelper.obtenerSocioPorDNI(dni)
            if (socio != null && socio["tipoSocio"] == "ASOCIADO") {
                val pdfPath = pdfService.generarCarnetPDF(
                    nombre = "${socio["nombre"]} ${socio["apellido"]}",
                    dni = socio["dni"] ?: "",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                )

                // Mostrar diálogo de éxito
                android.app.AlertDialog.Builder(this)
                    .setTitle("Carnet Generado")
                    .setMessage("El carnet se guardó en:\n$pdfPath")
                    .setPositiveButton("Ver PDF") { _, _ ->
                        abrirPDF(pdfPath)
                    }
                    .setNegativeButton("Cerrar", null)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al generar el carnet: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun abrirPDF(pdfPath: String) {
        try {
            val pdfFile = File(pdfPath)
            val pdfUri = androidx.core.content.FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                pdfFile
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(pdfUri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error al abrir el PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


}