package de.makuhn.semesterticket

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

    enum class Type {
        SEMESTERTICKET,
        DEUTSCHLANDTICKET
    }
}