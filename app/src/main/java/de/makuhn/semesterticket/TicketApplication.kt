package de.makuhn.semesterticket

import android.app.Application
import de.makuhn.semesterticket.data.database.TicketRoomDatabase
import de.makuhn.semesterticket.repository.TicketRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TicketApplication : Application () {
    val applicationScope = CoroutineScope(SupervisorJob())


    val database by lazy { TicketRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { TicketRepository(database.ticketDao()) }
}