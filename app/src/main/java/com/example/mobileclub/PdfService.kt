package com.example.mobileclub

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import androidx.appcompat.content.res.AppCompatResources
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import java.io.ByteArrayOutputStream
import java.io.File

class PdfService(private val context: Context) {

    fun generarCarnetPDF(nombre: String, dni: String, fecha: String): String {
        try {
            // Crear directorio si no existe
            val pdfDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "carnets")
            if (!pdfDir.exists()) {
                pdfDir.mkdirs()
            }

            // Crear archivo PDF
            val fileName = "carnet_${dni}_$fecha.pdf"
            val filePath = File(pdfDir, fileName)

            // Inicializar PDF
            val writer = PdfWriter(filePath)
            val pdf = PdfDocument(writer)
            val document = Document(pdf, PageSize.A6.rotate())

            // Agregar logo
            val logo = AppCompatResources.getDrawable(context, R.drawable.logo_mi_club_1)
            if (logo != null) {
                val bitmap = (logo as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val logoImage = Image(ImageDataFactory.create(stream.toByteArray()))
                    .setWidth(50f)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                document.add(logoImage)
            }

            // Título
            document.add(
                Paragraph("MI CLUB")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20f)
                    .setBold()
            )

            // Línea separadora
            document.add(
                Paragraph("_".repeat(30))
                    .setTextAlignment(TextAlignment.CENTER)
            )

            // Datos del socio
            val table = Table(2).useAllAvailableWidth()
            table.addCell(Cell().add(Paragraph("Nombre:").setBold()))
            table.addCell(Cell().add(Paragraph(nombre)))
            table.addCell(Cell().add(Paragraph("DNI:").setBold()))
            table.addCell(Cell().add(Paragraph(dni)))
            table.addCell(Cell().add(Paragraph("Fecha:").setBold()))
            table.addCell(Cell().add(Paragraph(fecha)))
            document.add(table)

            document.close()
            return filePath.absolutePath

        } catch (e: Exception) {
            throw Exception("Error al generar PDF del carnet: ${e.message}")
        }
    }
}