package com.example.mobileclub

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class ListarVenc : AppCompatActivity() {
    private lateinit var sociosHelper: SociosHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_venc)


        sociosHelper = SociosHelper(this)
        val edtFecha = findViewById<EditText>(R.id.editTextText9)
        val btnBuscar = findViewById<Button>(R.id.button11)
        val listViewVenc = findViewById<ListView>(R.id.listViewVenc)


        edtFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                        //"$selectedDay/${selectedMonth + 1}/$selectedYear"
                    edtFecha.setText(selectedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }



        // Configuración del botón de búsqueda por fecha
        btnBuscar.setOnClickListener {
            val fecha = edtFecha.text.toString().trim()
            if (fecha.isNotEmpty()) {
                val sociosVencen = sociosHelper.obtenerVencimientosPorFecha(fecha)
                if (sociosVencen.isNotEmpty()) {
                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        sociosVencen.map { "${it["nombre"]} ${it["apellido"]} - DNI: ${it["dni"]}" }
                    )
                    listViewVenc.adapter = adapter
                } else {
                    Toast.makeText(this, "No hay socios con cuota vencida en esa fecha", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Ingrese una fecha válida", Toast.LENGTH_SHORT).show()
            }
        }

        val btnVolverListar = findViewById<Button>(R.id.btnVolverListar)
        btnVolverListar.setOnClickListener{
            NavigationUtils.navigateToActivity(this, Menu::class.java)
        }
    }
}