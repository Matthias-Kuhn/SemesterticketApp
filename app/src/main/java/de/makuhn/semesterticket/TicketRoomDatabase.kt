package de.makuhn.semesterticket

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Ticket::class), version = 1)
@TypeConverters(LocalDateTimeConverter::class)
public abstract class TicketRoomDatabase : RoomDatabase() {
    abstract fun ticketDao(): TicketDao


    private class TicketDatabaseCallback(
        private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var ticktDao = database.ticketDao()


                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TicketRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TicketRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TicketRoomDatabase::class.java,
                    "ticket_database"
                )
                    .addCallback(TicketDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}