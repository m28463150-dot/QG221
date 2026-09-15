package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        TrackEntity::class,
        EventEntity::class,
        EventTrackCrossRef::class,
        TicketTypeEntity::class,
        TicketEntity::class,
        TransactionEntity::class,
        ArtistWalletEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        AppNotificationEntity::class,
        TrackCommentEntity::class,
        ArtistTipEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class QuayGuetDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun eventDao(): EventDao
    abstract fun ticketDao(): TicketDao
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun notificationDao(): NotificationDao
    abstract fun commentDao(): CommentDao
    abstract fun tipDao(): TipDao

    companion object {
        @Volatile
        private var INSTANCE: QuayGuetDatabase? = null

        fun getInstance(context: Context): QuayGuetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuayGuetDatabase::class.java,
                    "quay_guet_221.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
