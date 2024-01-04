package de.makuhn.semesterticket.repository

import androidx.annotation.WorkerThread
import de.makuhn.semesterticket.data.local.TicketDao
import de.makuhn.semesterticket.model.Ticket
import kotlinx.coroutines.flow.Flow

class TicketRepository(private val ticketDao: TicketDao) {

    val allTickets: Flow<List<Ticket>> = ticketDao.getAllTickets()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(ticket: Ticket) {
        ticketDao.insertTicket(ticket)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(ticket: Ticket) {
        ticketDao.deleteTicket(ticket)
    }

}