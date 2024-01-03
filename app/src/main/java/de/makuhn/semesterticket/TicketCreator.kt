package de.makuhn.semesterticket

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.jvm.Throws

object TicketCreator{

    suspend fun createTicket(fullSizeBitmap: Bitmap, context: Context): Ticket {
        return coroutineScope {

            val ticketTitle = async { extractTicketTitle(fullSizeBitmap) }.await()
            val ticketType = getTicketTypeFromTitle(ticketTitle)

            // crop bitmaps
            val aztecCodeBitmap = TicketCropUtils.cropToAztecCode(fullSizeBitmap)
            val ticketNumberBitmap = TicketCropUtils.cropToTicketNumber(fullSizeBitmap)

            // perform ocr
            val ticketSubtitle = async { extractTicketSubtitle(fullSizeBitmap) }.await()
            val validityStartDate = async { extractValidityStartDate(fullSizeBitmap) }.await()
            val validityEndDate = async { extractValidityEndDate(fullSizeBitmap) }.await()
            val passengerName = async { extractPassengerName(fullSizeBitmap, ticketType) }.await()

            // Todo store Bitmaps and get paths
            // BitmapStorageHelper.saveBitmapToInternalStorage(context, "code1", aztecCodeBitmap)

            Ticket(ticketType, ticketTitle, ticketSubtitle, validityStartDate, validityEndDate, passengerName, "", "", "")
        }
    }


    private suspend fun extractTicketTitle(fullSizeBitmap: Bitmap): String {
        val croppedBitmap = TicketCropUtils.cropToTitle(fullSizeBitmap)
        return OcrUtils.readWithCoroutine(croppedBitmap)
    }

    private fun getTicketTypeFromTitle(ticketTitle: String): Ticket.Type {
        return if (ticketTitle.contains("Semester")) {
            Ticket.Type.SEMESTERTICKET
        } else if (ticketTitle.contains("Deutschland")) {
            Ticket.Type.DEUTSCHLANDTICKET
        } else {
            throw UnrecognizedTicketException("Could not read title from Ticket")
        }
    }

    private suspend fun extractTicketSubtitle(fullSizeBitmap: Bitmap): String {
        val croppedBitmap = TicketCropUtils.cropToSubtitle(fullSizeBitmap)
        return OcrUtils.readWithCoroutine(croppedBitmap)
    }

    private suspend fun extractValidityStartDate(fullSizeBitmap: Bitmap): LocalDateTime {
        val croppedBitmap = OcrUtils.removeGrayText(TicketCropUtils.cropToValidityStartDate(fullSizeBitmap))
        val stringResult =  OcrUtils.readWithCoroutine(croppedBitmap)

        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val localDate = LocalDate.parse(stringResult, dateFormatter)
        return LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
    }

    private suspend fun extractValidityEndDate(fullSizeBitmap: Bitmap): LocalDateTime {
        val croppedBitmap = OcrUtils.removeGrayText(TicketCropUtils.cropToValidityEndDate(fullSizeBitmap))
        val stringResult =  OcrUtils.readWithCoroutine(croppedBitmap)

        return if (stringResult.length < 12) {
            val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val localDate = LocalDate.parse(stringResult, dateFormatter)
            LocalDateTime.of(localDate, LocalTime.MAX)
        } else {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            LocalDateTime.parse(stringResult, dateTimeFormatter)
        }
    }

    private suspend fun extractPassengerName(fullSizeBitmap: Bitmap, type: Ticket.Type): String {
        val croppedBitmap = if (type == Ticket.Type.DEUTSCHLANDTICKET) {
            OcrUtils.removeGrayText(TicketCropUtils.cropToPassengerName_DEUTSCHLANDTICKET(fullSizeBitmap))}
        else {
            OcrUtils.removeGrayText(TicketCropUtils.cropToPassengerName_SEMESTERTICKET(fullSizeBitmap))}

        val name =  OcrUtils.readWithCoroutine(croppedBitmap)
        return name.replace("\\n|\\r".toRegex(), " ")
    }
}