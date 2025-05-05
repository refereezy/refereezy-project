package com.example.rellotgejais.screens.user

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rellotgejais.MyApp
import com.example.rellotgejais.R
import com.example.rellotgejais.data.handlers.ClockHandler
import com.example.rellotgejais.data.handlers.RefereeViewModel
import com.example.rellotgejais.data.handlers.ReportHandler
import com.example.rellotgejais.data.managers.RefereeManager
import com.example.rellotgejais.data.managers.ReportManager
import com.example.rellotgejais.data.services.LocalStorageService
import com.example.rellotgejais.screens.report.ActionsActivity
import com.example.rellotgejais.utils.ConfirmationDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.runBlocking

class QrActivity : AppCompatActivity() {
    private val clockViewModel: ClockHandler by viewModels()
    private val refereeViewModel: RefereeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        val codeclock = LocalStorageService.getClockQrCode()!!
        val qrBitmap = generateQRCode(codeclock, 1000)
        findViewById<ImageView>(R.id.qrImageView).setImageBitmap(qrBitmap)
        val qrImageView = findViewById<ImageView>(R.id.qrImageView)

        qrImageView.setOnLongClickListener { _ ->
            ConfirmationDialog.showReportDialog(
                this, "Are you sure you want to regenerate the QR code?",
                onConfirm = { newCodeToQr() },
                onCancel = {}
            )
            true
        }
        val tempToken = "$2b$12$/88liRIt9od1TfvQDqIYj.3B3KdN0ZAv/gfNrfjXN/8aB1TPejxa."
        val tempId = 1
        refereeViewModel.getReferee(tempId, tempToken)

        refereeViewModel.referee.observe(this) { referee ->
            if (referee != null) {
                RefereeManager.setCurrentReferee(referee)
                loadReport()
            }
        }


    }

    /**
     * TODO: Requerimientos de emparejamiento con QR
     * - Pedir QR a la api
     * - Almacenar QR en el reloj
     * - Mostrar QR en la api.
     **/


    fun generateQRCode(text: String, size: Int): Bitmap? {
        val writer = QRCodeWriter()
        return try {
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }

            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    fun newCodeToQr() {
        clockViewModel.generateCode();
        clockViewModel.clock.observe(this) { code ->
            if (code != null) {
                val qrBitmap = generateQRCode(code.code, 1000)
                findViewById<ImageView>(R.id.qrImageView).setImageBitmap(qrBitmap)
            }
        }
    }
    fun loadReport() {
        val referee = RefereeManager.getCurrentReferee()!!
        runBlocking {
            val report = ReportHandler.getReport(referee.id)
            if (report == null) {
                println("No report found")
                return@runBlocking
            }
            println("Report found: ${report.raw.id}")
            ReportManager.setCurrentReport(report)

        }

        val report = ReportManager.getCurrentReport()!!
        val timerViewModel = (application as MyApp).timerViewModel
        timerViewModel.initTimer(report.raw.timer[0], report.raw.timer[1])
        val intent = Intent(this@QrActivity, ActionsActivity::class.java)
        startActivity(intent)

    }


}