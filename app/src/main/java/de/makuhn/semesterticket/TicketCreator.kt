package de.makuhn.semesterticket

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

object TicketCreator{


    suspend fun createTicket(fullSizeBitmap: Bitmap, context: Context): Ticket {
        return coroutineScope {
            // initialize bitmaps
            val page1 = OcrUtils.removeGrayText(PdfUtils.getLeftBitmap(fullSizeBitmap)!!)
            val page2 = PdfUtils.getCenterBitmap(fullSizeBitmap)!!
            val page3 = PdfUtils.getRightBitmap(fullSizeBitmap)!!
            val aztecCode = PdfUtils.getCodeBitmap(fullSizeBitmap)!!
            val ticketNumber = PdfUtils.getTicketNumberBitmap(fullSizeBitmap)!!


            val startDate = async { getStartDate(fullSizeBitmap) }.await()
            val endDate = async { getEndDate(fullSizeBitmap) }.await()
            val heading = async { getHeading(fullSizeBitmap) }.await()
            val subheading = async { getSubheading(fullSizeBitmap) }.await()
            val name = async { getName(fullSizeBitmap,heading) }.await()
            BitmapStorageHelper.saveBitmapToInternalStorage(context, "code1", aztecCode)

            Ticket(startDate, endDate, name, heading, subheading, page1, page2, page3, aztecCode, ticketNumber, fullSizeBitmap)
        }
    }

    private suspend fun getStartDate(fullSizeBitmap: Bitmap): String {
        val croppedBitmap = OcrUtils.removeGrayText(PdfUtils.getFromDateBitmap(fullSizeBitmap)!!)
        return OcrUtils.readWithCoroutine(croppedBitmap)
    }

    private suspend fun getEndDate(fullSizeBitmap: Bitmap): String {
        val croppedBitmap = OcrUtils.removeGrayText(PdfUtils.getToDateBitmap(fullSizeBitmap)!!)
        return OcrUtils.readWithCoroutine(croppedBitmap)
    }

    private suspend fun getName(fullSizeBitmap: Bitmap, heading:String): String {
        val croppedBitmap = if (!heading.contains("Semester")) {
                OcrUtils.removeGrayText(PdfUtils.getNameBitmapDT(fullSizeBitmap)!!)}
        else {
            OcrUtils.removeGrayText(PdfUtils.getNameBitmapST(fullSizeBitmap)!!)}

        val name =  OcrUtils.readWithCoroutine(croppedBitmap)
        return name.replace("\\n|\\r".toRegex(), " ")
    }

    private suspend fun getHeading(fullSizeBitmap: Bitmap): String {
        val croppedBitmap = PdfUtils.getHeadingBitmap(fullSizeBitmap)!!
        return OcrUtils.readWithCoroutine(croppedBitmap)
    }

    private suspend fun getSubheading(fullSizeBitmap: Bitmap): String {
        val croppedBitmap = PdfUtils.getSubheadingBitmap(fullSizeBitmap)!!
        return OcrUtils.readWithCoroutine(croppedBitmap)
    }
}