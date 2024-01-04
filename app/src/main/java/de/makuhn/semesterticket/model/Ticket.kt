package de.makuhn.semesterticket.model

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.makuhn.semesterticket.data.BitmapStorageHelper
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey(autoGenerate = true) val ticketId: Long = 0,
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


    fun getValidityString(): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val startString = validityStartDate.format(dateFormatter)

        // only print hours if it is not to midnight
        val isLastPossibleTime = validityEndDate.toLocalTime() == LocalTime.MAX
        val endString = if (isLastPossibleTime) {
            validityEndDate.format(dateFormatter)
        } else {
            validityEndDate.format(dateTimeFormatter)
        }
        return "$startString - $endString"
    }
    fun isValid(): Boolean {
        val currentDate = LocalDateTime.now()
        return currentDate.isAfter(validityStartDate) && currentDate.isBefore(validityEndDate)
    }

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