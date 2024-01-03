package de.makuhn.semesterticket

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TicketRepository(private val ticketDao: TicketDao) {

    val allTickets: Flow<List<Ticket>> = ticketDao.getAllTickets()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(ticket: Ticket) {
        ticketDao.insertTicket(ticket)
    }

}