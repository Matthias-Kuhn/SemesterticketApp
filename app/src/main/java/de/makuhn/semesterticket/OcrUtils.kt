package de.makuhn.semesterticket

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
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


    fun removeGrayText(bitmap: Bitmap): Bitmap {
        val resultBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        val paint = Paint()
        val colorMatrix = ColorMatrix()

        // Reduce the saturation to desaturate the image
        colorMatrix.setSaturation(0.5f)

        // Preserve black color by boosting its saturation
        colorMatrix.postConcat(ColorMatrix(floatArrayOf(
            2f, 0f, 0f, 0f, 0f,  // Red
            0f, 2f, 0f, 0f, 0f,  // Green
            0f, 0f, 2f, 0f, 0f,  // Blue
            0f, 0f, 0f, 1f, 0f   // Alpha (1 means preserve alpha)
        )))

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return resultBitmap
    }
}