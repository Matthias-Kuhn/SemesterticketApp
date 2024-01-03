package de.makuhn.semesterticket

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TicketApplication : Application () {
    val applicationScope = CoroutineScope(SupervisorJob())


    val database by lazy { TicketRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { TicketRepository(database.ticketDao()) }
}