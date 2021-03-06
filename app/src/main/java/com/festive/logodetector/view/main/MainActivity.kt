package com.festive.logodetector.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.festive.logodetector.view.content.ContentActivity
import com.festive.logodetector.R
import com.festive.logodetector.model.StorageFeeder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    val executer: ExecutorService = Executors.newSingleThreadExecutor()

    @Inject
    lateinit var storageFeeder: StorageFeeder

    private var shouldFinish = false
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (allPermissionsGranted()) {
            storageFeeder.copyDirorfileFromAssetManager("قوانین و مقررات", "LogoDetector/قوانین و مقررات")
            startCamera()
            captureButton.setOnClickListener { takePhoto() }
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        progressBar.visibility = View.VISIBLE
        captureButton.visibility = View.INVISIBLE
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val appFolder =
            File(Environment.getExternalStorageDirectory().absolutePath + "/LogoDetector")
        appFolder.mkdir()
        val photoFile = File(appFolder.absolutePath, "temp.jpg")
        photoFile.delete()

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(
                        this@MainActivity,
                        "برنامه با مشکل مواجه شد.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Log.d(TAG, msg)
                    initAutoML(savedUri)
                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                storageFeeder.copyDirorfileFromAssetManager("قوانین و مقررات", "LogoDetector/قوانین و مقررات")
                startCamera()
                captureButton.setOnClickListener { takePhoto() }
            } else {
                Toast.makeText(
                    this,
                    "کاربر دسترسی لازم به برنامه را نداده است.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun initAutoML(uri: Uri) {
        /** Create LocalModel object, specifying the path to the model manifest file */
        val localModel = AutoMLImageLabelerLocalModel.Builder()
            .setAssetFilePath("model/manifest.json")
            // or .setAbsoluteFilePath(absolute file path to manifest file)
            .build()

        /** Create an image labeler from your model */
        val autoMLImageLabelerOptions = AutoMLImageLabelerOptions.Builder(localModel)
            .setConfidenceThreshold(0f)  // Evaluate your model in the Firebase console
            // to determine an appropriate value.
            .build()
        val labeler = ImageLabeling.getClient(autoMLImageLabelerOptions)

        //TODO provide the bitmap to InputImage, then pass that to AutoML
        val bitmap = BitmapFactory.decodeResource(resources,
            R.drawable.niroo_test_image
        )
//        val image = InputImage.fromBitmap(bitmap, 0)
        val image = InputImage.fromFilePath(this, uri)


        /** run model on the provided image */
        labeler.process(image)
            .addOnSuccessListener { labels ->
                /** Task completed successfully so navigate to corresponding list of folders of PDF */
                labels.iterator().forEachRemaining {
                    Log.d(
                        TAG,
                        "label found: text: ${it.text}, confidence: ${it.confidence} index: ${it.index}"
                    )
                    if (it.text == "niroo") {
                        if(it.confidence >= .7){
                            //todo navigate to pdf list activity
                            Intent(this,
                                ContentActivity::class.java).apply{
                                startActivity(this)
                                finish()
                            }
                        }
                        else Toast.makeText(
                            this,
                            "لوگو تشخیص داده نشد. لطفا وضعیت دوربین را تغییر دهید.",
                            Toast.LENGTH_LONG
                        ).show()
                        progressBar.visibility = View.GONE
                        captureButton.isEnabled = true
                        captureButton.visibility = View.VISIBLE
                    }
                }
            }
            .addOnFailureListener { e ->
                /** Task failed with an exception */
                Toast.makeText(this, "مشکلی رخ داده است.", Toast.LENGTH_SHORT).show()
                finish()
            }

    }

    override fun onBackPressed() {
        if(shouldFinish)
            finish()
        else{
            shouldFinish = true
            handler.postDelayed({
                shouldFinish = false
            },2000)
            Toast.makeText(this, "برای خروج دکمه بازگشت را مجداا فشار دهید!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executer.shutdown()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}