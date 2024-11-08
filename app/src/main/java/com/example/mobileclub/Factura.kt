package com.example.mobileclub

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class Factura : AppCompatActivity() {

    private lateinit var descripcionFactura: TextView
    private lateinit var totalField: TextView
    private lateinit var dniField: TextView
    private lateinit var nombreField: TextView
    private lateinit var apellidoField: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_factura)

        descripcionFactura = findViewById(R.id.descripcionFactura)
        totalField = findViewById(R.id.total_field)
        dniField = findViewById(R.id.dni_field)
        nombreField = findViewById(R.id.nombre_field)
        apellidoField = findViewById(R.id.apellido_field)

        val btnVolver = findViewById<Button>(R.id.button2)

        // Recibir los datos del Intent
        val metodoPago = intent.getStringExtra("metodoPago") ?: "Método desconocido"
        val importe = intent.getDoubleExtra("importe", 0.0)
        val cuota = intent.getDoubleExtra("cuota", 0.0)
        val dni = intent.getStringExtra("dni") ?: "N/A"
        val nombre = intent.getStringExtra("nombre") ?: "N/A"
        val apellido = intent.getStringExtra("apellido") ?: "N/A"



        // Mostrar la información en la pantalla de Factura
       // val descripcionFactura = findViewById<TextView>(R.id.descripcionFactura)
        //descripcionFactura.text = "Método de Pago: $metodoPago\n" +
          //      "Importe Total: $$importe\n" +
            //    (if (metodoPago.contains("Tarjeta")) "Cuota Mensual: $$cuota" else "")

        // Mostrar los datos en la interfaz
        dniField.text = dni
        nombreField.text = nombre
        apellidoField.text = apellido
        totalField.text = "$$importe"
        descripcionFactura.text = "Método de Pago: $metodoPago\nImporte Total: $$importe\n" + (if (metodoPago.contains("Tarjeta")) "Cuota Mensual: $$cuota" else "")


        btnVolver.setOnClickListener {
            NavigationUtils.navigateToActivity(this, Menu::class.java)
        }
        // Botón para generar el PDF
        val btnGenerarPdf = findViewById<Button>(R.id.button1)
        btnGenerarPdf.setOnClickListener { generarPDF(dni, nombre, apellido, metodoPago, importe) }

    }

    private fun generarPDF(dni: String, nombre: String, apellido: String, metodoPago: String, importe: Double) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Información en el PDF
        val paint = Paint()
        paint.textSize = 16f
        paint.color = Color.BLACK

        // Agregar el contenido
        canvas.drawText("Factura de Pago", 220f, 50f, paint)
        canvas.drawText("DNI: $dni", 50f, 100f, paint)
        canvas.drawText("Nombre: $nombre $apellido", 50f, 130f, paint)
        canvas.drawText("Método de Pago: $metodoPago", 50f, 160f, paint)
        canvas.drawText("Importe Total: $$importe", 50f, 190f, paint)

        // Finalizar la página
        pdfDocument.finishPage(page)

        // Guardar el archivo
        val directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(directory, "Factura_$dni.pdf")

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF guardado en: ${file.absolutePath}. Toca para abrir.", Toast.LENGTH_LONG).show()

            // Intent para abrir el PDF
            val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            // Mostrar el Intent para abrir el PDF
            startActivity(intent)

        } catch (e: IOException) {
            Toast.makeText(this, "Error al guardar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }finally {
            // Cerrar el documento
            pdfDocument.close()
        }


    }
}
