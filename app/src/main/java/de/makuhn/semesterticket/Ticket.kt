package de.makuhn.semesterticket

import android.graphics.Bitmap
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Ticket(
    startDateString: String,
    endDateString: String,
    var name: String,
    var heading: String,
    var subheading: String,
    var page1: Bitmap,
    var page2: Bitmap,
    var page3: Bitmap,
    var aztec_code: Bitmap,
    var ticketNumber: Bitmap,
    var fullSizeBitmap: Bitmap
) {

    lateinit var type: Type
    lateinit var startDate: LocalDateTime
    lateinit var endDate: LocalDateTime

    init {
        setType(heading)
        setStartDate(startDateString)
        setEndDate(endDateString)
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