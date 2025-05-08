package com.example.rellotgejais.screens.user

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.rellotgejais.R
import com.example.rellotgejais.data.handlers.ClockHandler
import com.example.rellotgejais.data.handlers.RefereeViewModel
import com.example.rellotgejais.data.managers.RefereeManager
import com.example.rellotgejais.data.services.LocalStorageService
import com.example.rellotgejais.data.services.SocketService
import com.example.rellotgejais.utils.ConfirmationDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.example.rellotgejais.data.handlers.SocketHandler

class QrActivity : AppCompatActivity() {
    private val clockViewModel: ClockHandler by viewModels()
    private val refereeViewModel: RefereeViewModel by viewModels()
    private val socketHandler: SocketHandler by viewModels()

    private lateinit var code: String
    private lateinit var qrImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qr)

        qrImageView = findViewById(R.id.qrImageView)
        code = LocalStorageService.getClockQrCode()!!

        val qrBitmap = generateQRCode(code, 1000)
        qrImageView.setImageBitmap(qrBitmap)

        // Espera a que se invoque el código y se envien los datos del referee

        socketHandler.refereeLoad.observe(this) {
            if (it == null) return@observe
            if (RefereeManager.getCurrentReferee() != null) return@observe
            // Carga los datos del arbitro usando las credenciales obtenidas por el emparejamiento
            val tempToken = it.token
            val tempId = it.id
            refereeViewModel.getReferee(tempId, tempToken)
        }

        // Cuando obtiene los datos completos del arbitro, lo guarda en el manager y carga el reporte
        refereeViewModel.referee.observe(this) { referee ->
            if (referee != null) {
                LocalStorageService.saveRefereeReference(referee.id.toString(), referee.token)
                RefereeManager.setCurrentReferee(referee)
                socketHandler.stopPairing() // Deja de escuchar emparejamientos
                // Redirige a la WaitActivity para esperar la acción de usar reloj
                val intent = Intent(this, WaitActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Si se genera un nuevo código, actualiza la imagen del QR
        clockViewModel.clock.observe(this) { newCode ->
            if (newCode != null) {
                code = newCode.code

                socketHandler.registerCode(code)
                socketHandler.awaitPairing()
                val qrBitmap = generateQRCode(code, 1000)
                qrImageView.setImageBitmap(qrBitmap)
            }
        }

        qrImageView.setOnLongClickListener {
            ConfirmationDialog.showReportDialog(
                this, "Are you sure you want to regenerate the QR code?",
                onConfirm = { newCodeToQr() },
                onCancel = {}
            )
            true
        }

    }

    override fun onResume() {
        super.onResume()
        // Registra en el servidor este QR para escuchar el emparejamiento
        socketHandler.registerCode(code)
        socketHandler.awaitPairing()
    }

    fun generateQRCode(text: String, size: Int): Bitmap? {
        val writer = QRCodeWriter()
        return try {
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap[x, y] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }

            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    fun newCodeToQr() {
        socketHandler.unregisterCode(code)
        clockViewModel.generateCode();
    }


}