package de.makuhn.semesterticket

import android.content.Context
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Ticket(
    val ticketType: Type,
    val ticketTitle: String,
    val ticketSubtitle: String,
    val validityStartDate: LocalDateTime,
    val validityEndDate: LocalDateTime,
    val passengerName: String,
    val aztecCodeImagePath: String,
    val ticketNumberImagePath: String,
    val fullSizeTicketImagePath: String
) {

    fun onDelete(context: Context) {
        BitmapStorageHelper.deleteFileFromInternalStorage(context, aztecCodeImagePath)
        BitmapStorageHelper.deleteFileFromInternalStorage(context, ticketNumberImagePath)
        BitmapStorageHelper.deleteFileFromInternalStorage(context, fullSizeTicketImagePath)
    }

    enum class Type {
        SEMESTERTICKET,
        DEUTSCHLANDTICKET
    }
}