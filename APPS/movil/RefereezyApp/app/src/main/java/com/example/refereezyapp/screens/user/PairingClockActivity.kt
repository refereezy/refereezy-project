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
    private lateinit var previewView: PreviewView
    private lateinit var cameraProvider: ProcessCameraProvider

    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    private val refereeViewModel: RefereeViewModel by viewModels()

    private var isCheckingCode = false // ✅ Para evitar spam de validaciones
    private var currentDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pairing_clock)

        previewView = findViewById(R.id.cameraPreview)
        cameraExecutor = Executors.newSingleThreadExecutor()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            initializeCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE &&
            grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        ) {
            initializeCamera()
        } else {
            PopUp.show(this, "Camera permission is required to scan QR codes", PopUp.Type.ERROR)
            finish()
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun initializeCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(cameraExecutor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        val rotation = imageProxy.imageInfo.rotationDegrees

                        if (mediaImage != null && !isCheckingCode) {
                            val image = InputImage.fromMediaImage(mediaImage, rotation)
                            processBarcode(image, imageProxy)
                        } else {
                            imageProxy.close()
                        }
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer)
            } catch (e: Exception) {
                Log.e("Camera", "Failed to bind camera use cases", e)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun processBarcode(image: InputImage, imageProxy: ImageProxy) {
        if (isCheckingCode) {
            imageProxy.close()
            return
        }

        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull { it.rawValue != null && isValidClockCode(it.rawValue!!) }?.rawValue?.let { qrValue ->
                    checkClockCode(qrValue)
                }
            }
            .addOnFailureListener { e ->
                Log.e("QRScanner", "Error scanning barcode", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun isValidClockCode(code: String): Boolean {
        return Regex("\\w{50}").matches(code)
    }

    private fun checkClockCode(code: String) {
        isCheckingCode = true

        val referee = RefereeManager.getCurrentReferee() ?: run {
            PopUp.show(this, "No referee session found", PopUp.Type.ERROR)
            isCheckingCode = false
            return
        }

        refereeViewModel.pairClock(referee, code)

        refereeViewModel.referee.observe(this) { referee ->
            isCheckingCode = false

            if (referee.clock_code != null) {
                PopUp.show(this, "Clock paired correctly", PopUp.Type.OK)
                finish()
            } else {
                PopUp.show(this, "Invalid clock QR, not registered", PopUp.Type.ERROR)
                // La cámara sigue activa, no hacemos `finish()`
            }
        }
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
