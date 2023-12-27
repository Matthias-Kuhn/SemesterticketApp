package de.makuhn.semesterticket

import android.graphics.Bitmap
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Ticket(fullSizeBitmap: Bitmap) {

    var type: Type? = null
    var startDate: LocalDateTime? = null
    var endDate: LocalDateTime? = null

    var name = ""
    var heading = ""
    var subheading = ""

    var page1: Bitmap
    var page2: Bitmap
    var page3: Bitmap
    var aztec_code: Bitmap
    var ticketNumber: Bitmap

    // Todo: Save pdf file


    init {
        page1 = OcrUtils.removeGrayText(PdfUtils.getLeftBitmap(fullSizeBitmap)!!)
        page2 = PdfUtils.getCenterBitmap(fullSizeBitmap)!!
        page3 = PdfUtils.getRightBitmap(fullSizeBitmap)!!
        aztec_code = PdfUtils.getCodeBitmap(fullSizeBitmap)!!
        ticketNumber = PdfUtils.getTicketNumberBitmap(fullSizeBitmap)!!

        val bitmapStartDate = OcrUtils.removeGrayText(PdfUtils.getFromDateBitmap(fullSizeBitmap)!!)
        val bitmapName = OcrUtils.removeGrayText(PdfUtils.getNameBitmap(fullSizeBitmap)!!)
        val bitmapHeading = PdfUtils.getHeadingBitmap(fullSizeBitmap)!!
        val bitmapSubheading = PdfUtils.getSubheadingBitmap(fullSizeBitmap)!!

        OcrUtils.read(bitmapStartDate) {
            setStartDate(it)
        }

        OcrUtils.read(bitmapName) {
            name = it.replace("\\n|\\r".toRegex(), " ")
        }

        OcrUtils.read(bitmapHeading) {
            heading = it
            setType(it)
        }
        OcrUtils.read(bitmapSubheading) {
            subheading = it
        }


    }

    fun setType(typeString: String) {
        type = if (typeString.contains("Deutschland")) {
            Type.DEUTSCHLANDTICKET
        } else {
            Type.SEMESTERTICKET
        }
    }

    fun setStartDate(dateString: String) {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val localDate = LocalDate.parse(dateString, dateFormatter)
        val localDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT)

        startDate = localDateTime
    }

    fun setEndDate(dateString: String) {
        endDate = if (dateString.length < 12) {
            val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val localDate = LocalDate.parse(dateString, dateFormatter)
            val localDateTime = LocalDateTime.of(localDate, LocalTime.MAX)
            localDateTime
        } else {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            val localDateTime = LocalDateTime.parse(dateString, dateTimeFormatter)
            localDateTime
        }
    }


    enum class Type {
        SEMESTERTICKET,
        DEUTSCHLANDTICKET
    }
}