package com.festive.logodetector

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAutoML()
    }

    private fun initAutoML() {
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
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.niroo_test_image)
        val image = InputImage.fromBitmap(bitmap, 0)


        /** run model on the provided image */
        labeler.process(image)
            .addOnSuccessListener { labels ->
                /** Task completed successfully so navigate to corresponding list of folders of PDF */
                labels.iterator().forEachRemaining {
                    Log.d(
                        TAG,
                        "label found: text: ${it.text}, confidence: ${it.confidence} index: ${it.index}"
                    )
                }
            }
            .addOnFailureListener { e ->
                /** Task failed with an exception */
                Toast.makeText(this, "مشکلی رخ داده است.", Toast.LENGTH_SHORT).show()
            }

    }
}