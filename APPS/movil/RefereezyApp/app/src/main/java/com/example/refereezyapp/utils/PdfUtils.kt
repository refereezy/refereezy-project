package com.example.refereezyapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.refereezyapp.data.models.PopulatedReport
import java.io.File
import java.io.FileOutputStream

object PDFUtils { // Usamos un objeto singleton en lugar de una clase
    fun generateActaPdf(context: Context, report: PopulatedReport): File {
        val pdf = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(843, 595, 1).create()
        val page = pdf.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
        }

        var y = 40f
        canvas.drawText("Acta de Partido", 230f, y, paint)
        y += 30f
        var date = report.match.raw.date
        canvas.drawText("Fecha del Partido: ${date.dayOfMonth}/${date.monthValue}/${date.year}  ${date.hour}:${date.minute}",  40f, y, paint)
        y += 25f
        var nameRefere = report.match.referee.name
        canvas.drawText("Árbitro: ${nameRefere}", 40f, y, paint)
        y += 25f

        /*var estado = if (report.raw.timer[0]>90) "Finalizado" else "Suspendido( ${report.raw.timer[0]}:${report.raw.timer[1]})"
        canvas.drawText("Duración: ${report.raw.timer[0]}:${report.raw.timer[1]}", 40f, y, paint)
        y += 30f*/

        val minutes = report.raw.timer[0].toInt()
        val seconds = report.raw.timer[1].toInt()

        val estado = if (minutes > 90) "Finalizado" else "Suspendido ($minutes:$seconds'')"

        canvas.drawText("Estado: ${estado} ", 40f, y, paint)
        y += 25f

        canvas.drawText("Incidencias:", 40f, y, paint)
        y += 25f

        // Define un Paint para el borde del recuadro
        val borderPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE  // Borde, no relleno
            strokeWidth = 2f
        }

        // Dibuja un recuadro alrededor del área de incidencias
        val left = 35f
        val top = y - 20f // un poco antes del título "Incidencias"
        val right = 800f
        val initialY = y

        // Aquí accedemos a report.raw.incidents directamente
        report.raw.incidents.forEach { incident ->
            // Accedemos a los campos de Incident (que está dentro de raw)
            canvas.drawText("- ${incident.minute}': ${incident.description}", 50f, y, paint)
            y += 20f
            if (y > 800) return@forEach
        }
        // Una vez añadidas, dibuja el borde alrededor
        canvas.drawRect(left, top, right, y + 10f, borderPaint)

        pdf.finishPage(page)

        val file = File(context.getExternalFilesDir(null), "acta_${report.raw.id}.pdf")
        pdf.writeTo(FileOutputStream(file))
        pdf.close()

        return file
    }

    fun openPdfFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
        }

        context.startActivity(Intent.createChooser(intent, "Abrir acta PDF con..."))
    }
}
