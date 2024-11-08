package com.example.mobileclub

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Pago : AppCompatActivity() {
    private lateinit var sociosHelper: SociosHelper
    private lateinit var pdfService: PdfService
    private lateinit var permissionHandler: PermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago)

        sociosHelper = SociosHelper(this)
        pdfService = PdfService(this)
        permissionHandler = PermissionHandler(this)

        val edtDni = findViewById<EditText>(R.id.editTextText9)
        val btnBuscar = findViewById<Button>(R.id.button11)
        val txtNombreSocio = findViewById<TextView>(R.id.txtNombreSocio)
        val btnPagar = findViewById<Button>(R.id.btnPagar)
        val btnVolverPago = findViewById<Button>(R.id.btnVolverPago)

        btnBuscar.setOnClickListener {
            val dni = edtDni.text.toString().trim()
            if (dni.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese un DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener}

            val socio = sociosHelper.obtenerSocioPorDNI(dni)
            if (socio != null) {
                txtNombreSocio.text = "${socio["nombre"]} ${socio["apellido"]}"
                btnPagar.isEnabled = true // Habilitar botón de pago

                AlertDialog.Builder(this)
                    .setTitle("Socio encontrado")
                    .setMessage("Socio encontrado: ${socio["nombre"]} ${socio["apellido"]}. Presione en pagar.")
                    .setPositiveButton("Aceptar") { dialog, _ ->
                        dialog.dismiss() // Cerrar el diálogo al presionar "Aceptar"
                    }
                    .show()


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
                    mostrarOpcionesPago(20000.0,socio)
                } else {
                    mostrarOpcionesPagoDia(socio)
                }
                txtNombreSocio.text = ""
                edtDni.text.clear()
                btnPagar.isEnabled = false
            }
        }


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

    private fun mostrarOpcionesPago(importe: Double, socio: Map<String, String>) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Opciones de pago")
        dialogBuilder.setMessage("El importe mensual es: $importe. ¿Cómo deseas pagar?")
        dialogBuilder.setPositiveButton("Tarjeta (3 cuotas)") { _, _ ->
            val cuota = importe / 3

            val actividades = socio["actividades"] ?: "Ninguna"
            val intent = Intent(this, Factura::class.java)
            intent.putExtra("actividades", actividades)
            intent.putExtra("dni", socio["dni"])
            intent.putExtra("nombre", socio["nombre"])
            intent.putExtra("apellido", socio["apellido"])
            intent.putExtra("metodoPago", "Tarjeta en 3 cuotas")
            intent.putExtra("importe", importe)
            intent.putExtra("cuota", cuota)
            startActivity(intent)
        }
        dialogBuilder.setNegativeButton("Efectivo") { _, _ ->

            val intent = Intent(this, Factura::class.java)
            val actividades = socio["actividades"] ?: "Ninguna"
            intent.putExtra("actividades", actividades)
            intent.putExtra("dni", socio["dni"])
            intent.putExtra("nombre", socio["nombre"])
            intent.putExtra("apellido", socio["apellido"])
            intent.putExtra("metodoPago", "Efectivo")
            intent.putExtra("importe", importe)
            startActivity(intent)
        }
        dialogBuilder.create().show()
    }

    private fun mostrarOpcionesPagoDia( socio: Map<String, String>) {
        val importeDiario = 6000.0
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Pago por día")
        dialogBuilder.setMessage("El importe diario es: $$importeDiario. Pago solo en efectivo")
        dialogBuilder.setPositiveButton("Pagar") { _, _ ->


            val intent = Intent(this, Factura::class.java)
            val actividades = socio["actividades"] ?: "Ninguna"
            intent.putExtra("actividades", actividades)
            intent.putExtra("dni", socio["dni"])
            intent.putExtra("nombre", socio["nombre"])
            intent.putExtra("apellido", socio["apellido"])
            intent.putExtra("metodoPago", "Efectivo (Pago diario)")
            intent.putExtra("importe", importeDiario)
            startActivity(intent)
        }
        dialogBuilder.create().show()
    }



}