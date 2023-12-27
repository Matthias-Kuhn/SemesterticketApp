package de.makuhn.semesterticket
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.IOException

object PdfUtils {

    private const val PDF_RENDER_PIXEL_WIDTH = 4000

    fun renderFirstPage(context: Context, uri: Uri): Bitmap {

        val parcelFileDescriptor = getParcelFileDescriptor(context, uri)!!
        val pdfRenderer = PdfRenderer(parcelFileDescriptor)
        val firstPage = pdfRenderer.openPage(0)
        val targetHeight = calculatePdfPixelHeight(firstPage)
        val bitmap = Bitmap.createBitmap(PDF_RENDER_PIXEL_WIDTH, targetHeight, Bitmap.Config.ARGB_8888)

        firstPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, PDF_RENDER_PIXEL_WIDTH, targetHeight, true)
        firstPage.close()
        pdfRenderer.close()

        return resizedBitmap
    }

    private fun calculatePdfPixelHeight(firstPage: PdfRenderer.Page): Int {
        val aspectRatio: Float = firstPage.width.toFloat() / firstPage.height.toFloat()
        return (PDF_RENDER_PIXEL_WIDTH / aspectRatio).toInt()
    }

    private fun getParcelFileDescriptor(context: Context, uri: Uri): ParcelFileDescriptor? {
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun cropBitmap(bitmap: Bitmap, left: Int, top: Int, width: Int, height: Int): Bitmap? {
        // 100 equals 1cm
        return try {
            Bitmap.createBitmap(bitmap,
                convertToPixels(left),
                convertToPixels(top),
                convertToPixels(width),
                convertToPixels(height))
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

    private fun convertToPixels(value: Int): Int = ((PDF_RENDER_PIXEL_WIDTH / 2100.0) * value).toInt()


    fun getLeftBitmap(bitmap: Bitmap): Bitmap? {
        return cropBitmap(bitmap, 208, 269, 523, 653)
    }
    fun getCenterBitmap(bitmap: Bitmap): Bitmap? {
        return cropBitmap(bitmap, 739, 269, 523, 653)
    }

    fun getRightBitmap(bitmap: Bitmap): Bitmap? {
        return cropBitmap(bitmap, 1267, 269, 523, 653)
    }

    fun getCodeBitmap(bitmap: Bitmap): Bitmap? {
        return cropBitmap(bitmap, 800, 350, 400, 400)
    }

    fun getHeadingBitmap(bitmap: Bitmap): Bitmap? {
        return cropBitmap(bitmap, 674, 119, 635, 76)
    }

    fun getSubheadingBitmap(bitmap: Bitmap): Bitmap? {
        return cropBitmap(bitmap, 674, 196, 635, 67)
    }

    fun getNameBitmap(bitmap: Bitmap): Bitmap? {
        return cropBitmap(bitmap, 226, 649, 489, 92)
    }

    fun getTicketNumberBitmap(bitmap: Bitmap): Bitmap? {
        return cropBitmap(bitmap, 743, 750, 515, 166)
    }

    fun getFromDateBitmap(bitmap: Bitmap): Bitmap? {
        return cropBitmap(bitmap, 231, 340, 160, 32)
    }
}
