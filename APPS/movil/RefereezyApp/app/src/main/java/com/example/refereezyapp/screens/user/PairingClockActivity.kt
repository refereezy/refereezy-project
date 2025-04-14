package com.example.refereezyapp.screens.user

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.refereezyapp.R
import com.example.refereezyapp.data.handlers.RefereeViewModel
import com.example.refereezyapp.data.managers.RefereeManager
import com.example.refereezyapp.utils.PopUp
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PairingClockActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private var isScanning = true
    private lateinit var previewView: PreviewView
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private lateinit var cameraProvider: ProcessCameraProvider
    private var currentDialog: AlertDialog? = null

    private val refereeViewModel: RefereeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pairing_clock)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        previewView = findViewById(R.id.cameraPreview)
        cameraExecutor = Executors.newSingleThreadExecutor()
        val returnBtn = findViewById<Button>(R.id.cancelBtn)

        returnBtn.setOnClickListener {
            onBackPressed()
            finish()
        }

        checkCameraPermission()
    }


    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
        } else {
            startCamera()
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    PopUp.show(this, "Camera permission is required to scan QR codes", PopUp.Type.ERROR)
                    finish()
                }
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        val rotation = imageProxy.imageInfo.rotationDegrees
                        try {
                            if (mediaImage != null && isScanning) {
                                val image = InputImage.fromMediaImage(mediaImage, rotation)
                                processImage(image, imageProxy)
                            }
                        } catch (e: Exception) {
                            Log.e("Camera", "Error processing image", e)
                        } finally {
                            imageProxy.close()
                        }
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analyzer
                )
            } catch (e: Exception) {
                Log.e("Camera", "Error setting camera", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImage(image: InputImage, imageProxy: ImageProxy) {
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { qrValue ->
                        if (isValidClockCode(qrValue) && isScanning) {
                            isScanning = false
                            runOnUiThread {
                                pairClock(qrValue)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("QRScanner", "Error en escaneo", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun isValidClockCode(qrValue: String): Boolean {
        val pattern = Regex("\\w{50}")
        return pattern.matches(qrValue)
    }

    private fun pairClock(code: String) {

        PopUp.show(this, "Code: ${code.substring(5)}...", PopUp.Type.OK)
        // todo: comprobar que el codigo exista en la db
        refereeViewModel.pairClock(referee = RefereeManager.getCurrentReferee()!!, code)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        currentDialog?.dismiss()
        if (::cameraProvider.isInitialized) {
            cameraProvider.unbindAll()
        }
    }
}