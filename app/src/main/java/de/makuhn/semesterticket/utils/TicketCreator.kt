package de.makuhn.semesterticket.utils

import android.content.Context
import android.graphics.Bitmap
import de.makuhn.semesterticket.model.Ticket
import de.makuhn.semesterticket.model.UnrecognizedTicketException
import de.makuhn.semesterticket.data.BitmapStorageHelper
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TicketCreator{

    suspend fun createTicket(fullSizeBitmap: Bitmap, context: Context): Ticket {
        return coroutineScope {

            val ticketTitle = async { extractTicketTitle(fullSizeBitmap) }.await()
            val ticketType = getTicketTypeFromTitle(ticketTitle)

            // perform ocr
            val ticketSubtitle = async { extractTicketSubtitle(fullSizeBitmap) }.await()
            val validityStartDate = async { extractValidityStartDate(fullSizeBitmap) }.await()
            val validityEndDate = async { extractValidityEndDate(fullSizeBitmap) }.await()
            val passengerName = async { extractPassengerName(fullSizeBitmap, ticketType) }.await()

            // crop bitmaps and store them in the file system
            val currentTimestamp = System.currentTimeMillis().toString()
            val aztecCodeImagePath = cropAndStoreAztecCode(context, fullSizeBitmap, currentTimestamp)
            val ticketNumberImagePath = cropAndStoreTicketNumber(context, fullSizeBitmap, currentTimestamp)
            val fullSizeTicketImagePath = storeFullSizeTicket(context, fullSizeBitmap, currentTimestamp)

            Ticket(0, ticketType, ticketTitle, ticketSubtitle, validityStartDate, validityEndDate, passengerName, aztecCodeImagePath, ticketNumberImagePath, fullSizeTicketImagePath)
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
        val croppedBitmap =
            OcrUtils.removeGrayText(TicketCropUtils.cropToValidityStartDate(fullSizeBitmap))
        val stringResult = OcrUtils.readWithCoroutine(croppedBitmap)

        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val localDate = LocalDate.parse(stringResult, dateFormatter)
        return LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
    }

    private suspend fun extractValidityEndDate(fullSizeBitmap: Bitmap): LocalDateTime {
        val croppedBitmap =
            OcrUtils.removeGrayText(TicketCropUtils.cropToValidityEndDate(fullSizeBitmap))
        val stringResult = OcrUtils.readWithCoroutine(croppedBitmap)

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
            OcrUtils.removeGrayText(
                TicketCropUtils.cropToPassengerName_DEUTSCHLANDTICKET(
                    fullSizeBitmap
                )
            )
        }
        else {
            OcrUtils.removeGrayText(
                TicketCropUtils.cropToPassengerName_SEMESTERTICKET(
                    fullSizeBitmap
                )
            )
        }

        val name = OcrUtils.readWithCoroutine(croppedBitmap)
        return name.replace("\\n|\\r".toRegex(), " ")
    }

    fun cropAndStoreAztecCode(context: Context, fullSizeBitmap: Bitmap, currentTimestamp: String): String {
        val filename = "${currentTimestamp}Code.jpg"
        val aztecCodeBitmap = TicketCropUtils.cropToAztecCode(fullSizeBitmap)
        BitmapStorageHelper.saveBitmapToInternalStorage(context, filename, aztecCodeBitmap)
        return filename
    }

    fun cropAndStoreTicketNumber(context: Context, fullSizeBitmap: Bitmap, currentTimestamp: String): String {
        val filename = "${currentTimestamp}Number.jpg"
        val croppedBitmap = TicketCropUtils.cropToTicketNumber(fullSizeBitmap)
        BitmapStorageHelper.saveBitmapToInternalStorage(context, filename, croppedBitmap)
        return filename
    }

    fun storeFullSizeTicket(context: Context, fullSizeBitmap: Bitmap, currentTimestamp: String): String {
        val filename = "${currentTimestamp}Ticket.jpg"
        BitmapStorageHelper.saveBitmapToInternalStorage(context, filename, fullSizeBitmap)
        return filename
    }
}