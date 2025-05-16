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

object PDFUtils {

    fun generateActaPdf(context: Context, report: PopulatedReport): File {
        val pdf = PdfDocument()

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
        }

        val borderPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }

        val pageWidth = 843
        val pageHeight = 595
        val marginX = 40f
        val totalWidth = pageWidth - marginX * 2
        val goalBoxWidth = totalWidth * 1f / 3f
        val otherBoxWidth = totalWidth * 2f / 3f
        val incidentSpacing = 20f

        fun drawTeamPage(
            teamName: String,
            report: PopulatedReport,
            teamId: Int,
            canvas: android.graphics.Canvas,
            includeHeader: Boolean
        ) {
            val incidents = report.incidents.filter {
                it.player?.team?.id == teamId
            }

            val goles = incidents.filter {
                it.raw.description.equals("GOAL", ignoreCase = true)
            }

            val otras = incidents.filter {
                !it.raw.description.equals("GOAL", ignoreCase = true)
            }

            var y = 40f
            var headerTop = y
            var headerBottom = y

            if (includeHeader) {
                canvas.drawText("ACTA DE PARTIDO", marginX, y+10f, paint)
                y += 30f

                val date = report.match.raw.date
                canvas.drawText(
                    "Fecha del Partido: ${date.dayOfMonth}/${date.monthValue}/${date.year}  ${date.hour}:${date.minute}",
                    marginX, y, paint
                )
                y += 25f

                val refereeName = report.match.referee.name
                canvas.drawText("Árbitro: $refereeName", marginX, y, paint)
                y += 25f

                canvas.drawText("Encunetro: ${report.match.local_team.name} vs ${report.match.visitor_team.name} "  , marginX, y, paint)
                y += 25f

                val minutes = report.raw.timer[0].toInt()
                val seconds = report.raw.timer[1].toInt()
                val estado = if (minutes > 90) "Finalizado" else "Suspendido ($minutes:$seconds'')"


                canvas.drawText("Estado: $estado", marginX, y, paint)
                y += 10f

                headerBottom = y
                // Dibujar rectángulo de cabecera (sin incluir el nombre del equipo)
                canvas.drawRect(
                    marginX - 10f,
                    headerTop - 10f,
                    pageWidth - marginX + 10f,
                    headerBottom + 10f,
                    borderPaint
                )

                y += 25f // Espacio después del recuadro
            }

            // Nombre del equipo (fuera del recuadro de cabecera)
            canvas.drawText("Equipo: $teamName", marginX, y, paint)
            y += 25f

            val boxTop = y
            var yGoles = y - 5f
            var yOtras = y - 5f

            // Título GOLES (ahora dentro del cuadro de goles)
            canvas.drawText("GOLES", marginX , yGoles, paint)
            yGoles += 20f

            goles.forEach {
                val desc = if (it.raw.type.name == it.raw.description) "${it.raw.description}" else "${it.raw.type.name}: ${it.raw.description}"

                canvas.drawText(
                    "- ${it.raw.minute}' ${it.raw.player?.name}[${it.raw.player?.dorsal}]: $desc",
                    marginX + 10f,
                    yGoles+5f,
                    paint
                )
                yGoles += incidentSpacing
            }

            // Título OTRAS INCIDENCIAS (también ajustado)
            canvas.drawText("INCIDENCIAS", marginX + goalBoxWidth + 10f, yOtras, paint)
            yOtras += 20f

            otras.forEach {
                val desc = if (it.raw.type.name == it.raw.description) "${it.raw.description}"
                else "${it.raw.type.name}: ${it.raw.description}"

                canvas.drawText(
                    "- ${it.raw.minute}' ${it.raw.player?.name}[${it.raw.player?.dorsal}]: $desc",
                    marginX + goalBoxWidth + 10f,
                    yOtras,
                    paint
                )
                yOtras += incidentSpacing
            }

            // Dibujar recuadro de goles
            canvas.drawRect(
                marginX,
                boxTop,
                marginX + goalBoxWidth,
                yGoles + 10f,
                borderPaint
            )

            // Dibujar recuadro de otras incidencias
            canvas.drawRect(
                marginX + goalBoxWidth,
                boxTop,
                marginX + goalBoxWidth + otherBoxWidth,
                yOtras + 10f,
                borderPaint
            )
        }

        // Página 1 – Equipo Local
        val pageLocal = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create())
        val canvasLocal = pageLocal.canvas
        drawTeamPage(report.match.local_team.name, report, report.match.local_team.id, canvasLocal, includeHeader = true)
        pdf.finishPage(pageLocal)

        // Página 2 – Equipo Visitante
        val pageVisitor = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create())
        val canvasVisitor = pageVisitor.canvas
        drawTeamPage(report.match.visitor_team.name, report, report.match.visitor_team.id, canvasVisitor, includeHeader = false)
        pdf.finishPage(pageVisitor)

        // Guardar PDF
        val file = File(context.getExternalFilesDir(null), "acta_${report.raw.match_id}.pdf")
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
