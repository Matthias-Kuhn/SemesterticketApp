package de.makuhn.semesterticket.utils

import android.graphics.Bitmap

object TicketCropUtils {
    fun cropToAztecCode(bitmap: Bitmap): Bitmap {
        return PdfUtils.cropBitmap(bitmap, 800, 350, 400, 400)
    }
    fun cropToTitle(bitmap: Bitmap): Bitmap {
        return PdfUtils.cropBitmap(bitmap, 533, 119, 918, 76)
    }
    fun cropToSubtitle(bitmap: Bitmap): Bitmap {
        return PdfUtils.cropBitmap(bitmap, 674, 196, 635, 67)
    }
    fun cropToPassengerName_SEMESTERTICKET(bitmap: Bitmap): Bitmap {
        return PdfUtils.cropBitmap(bitmap, 226, 649, 489, 92)
    }
    fun cropToPassengerName_DEUTSCHLANDTICKET(bitmap: Bitmap): Bitmap {
        return PdfUtils.cropBitmap(bitmap, 226, 677, 489, 92)
    }
    fun cropToTicketNumber(bitmap: Bitmap): Bitmap {
        return PdfUtils.cropBitmap(bitmap, 743, 750, 515, 166)
    }
    fun cropToValidityStartDate(bitmap: Bitmap): Bitmap {
        return PdfUtils.cropBitmap(bitmap, 226, 328, 175, 44)
    }
    fun cropToValidityEndDate(bitmap: Bitmap): Bitmap {
        return PdfUtils.cropBitmap(bitmap, 419, 328, 280, 44)
    }
    fun cropToLeftTicketPage(bitmap: Bitmap): Bitmap {
        return PdfUtils.cropBitmap(bitmap, 208, 269, 523, 653)
    }
    fun cropToCenterTicketPage(bitmap: Bitmap): Bitmap {
        return PdfUtils.cropBitmap(bitmap, 739, 269, 523, 653)
    }
    fun cropToRightTicketPage(bitmap: Bitmap): Bitmap {
        return PdfUtils.cropBitmap(bitmap, 1267, 269, 523, 653)
    }
}