package de.makuhn.semesterticket

import android.graphics.Bitmap
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

object OcrUtils {

    fun read(bitmap: Bitmap, callback: (String) -> Unit) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                val resultText = visionText.text
                callback(resultText)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // Handle the failure if needed
                callback("") // or handle failure differently
            }
    }
}