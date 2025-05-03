package com.example.rellotgejais.screens.user

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rellotgejais.R
import com.example.rellotgejais.data.services.LocalStorageService
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class QrActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)

        val qrBitmap = LocalStorageService.getClockQrCode()?.let { generateQRCode(it, 500) }
        findViewById<ImageView>(R.id.qrImageView).setImageBitmap(qrBitmap)

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
}