package com.taqwa.hasib.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.taqwa.hasib.database.dao.CoachingEventDao
import com.taqwa.hasib.database.dao.DailyLogDao
import com.taqwa.hasib.database.dao.PrayerEntryDao
import com.taqwa.hasib.database.dao.UserSettingsDao
import com.taqwa.hasib.database.dao.WirdDao
import com.taqwa.hasib.database.dao.WirdEntryDao
import com.taqwa.hasib.database.entities.CoachingEvent
import com.taqwa.hasib.database.entities.DailyLog
import com.taqwa.hasib.database.entities.PrayerEntry
import com.taqwa.hasib.database.entities.UserSettings
import com.taqwa.hasib.database.entities.Wird
import com.taqwa.hasib.database.entities.WirdEntry

@Database(
    entities = [
        UserSettings::class,
        Wird::class,
        DailyLog::class,
        WirdEntry::class,
        PrayerEntry::class,
        CoachingEvent::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HasibDatabase : RoomDatabase() {
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun wirdDao(): WirdDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun wirdEntryDao(): WirdEntryDao
    abstract fun prayerEntryDao(): PrayerEntryDao
    abstract fun coachingEventDao(): CoachingEventDao

    companion object {
        @Volatile
        private var INSTANCE: HasibDatabase? = null

        fun getDatabase(context: Context): HasibDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HasibDatabase::class.java,
                    "hasib_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
