package de.makuhn.semesterticket

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface TicketDao {
    @Insert
    suspend fun insertTicket(ticket: Ticket)

    @Query("SELECT * FROM tickets")
    fun getAllTickets(): Flow<List<Ticket>>
}