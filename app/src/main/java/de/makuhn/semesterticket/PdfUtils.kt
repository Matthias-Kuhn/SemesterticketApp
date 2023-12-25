package de.makuhn.semesterticket
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URL

object PdfUtils {


    fun getParcelFileDescriptor(context: Context, uri: Uri): ParcelFileDescriptor? {
        try {
            return context.contentResolver.openFileDescriptor(uri, "r")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun renderFirstPage(context: Context, uri: Uri): Bitmap {
        val targetWidth = 4000
        val parcelFileDescriptor = getParcelFileDescriptor(context, uri)!!


        val pdfRenderer: PdfRenderer = PdfRenderer(parcelFileDescriptor)
        val firstPage = pdfRenderer.openPage(0)

        // Calculate the aspect ratio to maintain the original aspect ratio when resizing
        val aspectRatio: Float = firstPage.width.toFloat() / firstPage.height.toFloat()
        val targetHeight: Int = (targetWidth / aspectRatio).toInt()

        // Create a Bitmap object to hold the rendered page
        val bitmap = Bitmap.createBitmap(firstPage.width, firstPage.height, Bitmap.Config.ARGB_8888)

        // Render the page content onto the Bitmap
        firstPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        // Resize the Bitmap to the target width while maintaining the aspect ratio
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)

        // Close the page and renderer
        firstPage.close()
        pdfRenderer.close()

        return resizedBitmap
    }

    fun cropBitmap(bitmap: Bitmap, left: Int, top: Int, right: Int, bottom: Int): Bitmap? {
        // Ensure that the provided coordinates are within the bounds of the original bitmap
        if (left < 0 || top < 0 || right > bitmap.width || bottom > bitmap.height) {
            return null
        }

        // Create a new Bitmap with the specified crop area
        return try {
            Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
        } catch (e: IllegalArgumentException) {
            // IllegalArgumentException can be thrown if the provided coordinates are invalid
            e.printStackTrace()
            null
        }
    }
}
