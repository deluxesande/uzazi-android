package com.uzazi.app.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.uzazi.app.core.data.daos.BadgeDao
import com.uzazi.app.core.data.daos.CheckInDao
import com.uzazi.app.core.data.entities.BadgeEntity
import com.uzazi.app.core.data.entities.ChatMessageEntity
import com.uzazi.app.core.data.entities.CheckInEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [CheckInEntity::class, ChatMessageEntity::class, BadgeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun checkInDao(): CheckInDao
    abstract fun badgeDao(): BadgeDao
    abstract fun chatMessageDao(): com.uzazi.app.core.data.daos.ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "uzazi_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                seedBadges(database.badgeDao())
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedBadges(badgeDao: BadgeDao) {
            val badges = listOf(
                BadgeEntity("1", "First Bloom", "Completed your first check-in", 0),
                BadgeEntity("2", "Garden Starter", "3 day streak", 0),
                BadgeEntity("3", "Blooming Mama", "7 day streak", 0),
                BadgeEntity("4", "Garden Master", "30 day streak", 0),
                BadgeEntity("5", "Self Care Queen", "Earned 50 petals", 0),
                BadgeEntity("6", "Midnight Warrior", "Spoke to Night Companion", 0),
                BadgeEntity("7", "Hydration Hero", "Logged 8 glasses of water", 0),
                BadgeEntity("8", "Full Bloom", "Earned 100 petals", 0)
            )
            badgeDao.insertAll(badges)
        }
    }
}
