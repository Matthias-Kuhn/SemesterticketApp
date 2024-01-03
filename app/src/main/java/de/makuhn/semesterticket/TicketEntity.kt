package de.makuhn.semesterticket

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TicketEntity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startDateString: String,
    val endDateString: String,
    val name: String,
    val aztec_code: ByteArray,
    val ticketNumber: ByteArray,
    val fullSizeBitmap: ByteArray
)