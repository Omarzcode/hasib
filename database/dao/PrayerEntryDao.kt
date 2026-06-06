package com.taqwa.hasib.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.taqwa.hasib.database.entities.PrayerEntry

@Dao
interface PrayerEntryDao {
    @Insert
    suspend fun insert(prayerEntry: PrayerEntry): Long

    @Update
    suspend fun update(prayerEntry: PrayerEntry)

    @Delete
    suspend fun delete(prayerEntry: PrayerEntry)

    @Query("SELECT * FROM prayer_entries WHERE prayer_id = :prayerId")
    suspend fun getEntryById(prayerId: Int): PrayerEntry?

    @Query("SELECT * FROM prayer_entries WHERE log_id = :logId")
    suspend fun getEntriesByLog(logId: Int): List<PrayerEntry>

    @Query("SELECT * FROM prayer_entries WHERE log_id = :logId AND prayer_name = :prayerName")
    suspend fun getPrayerEntry(logId: Int, prayerName: String): PrayerEntry?

    @Query("SELECT COUNT(*) FROM prayer_entries WHERE log_id = :logId AND status = 'on_time'")
    suspend fun getOnTimeCount(logId: Int): Int

    @Query("SELECT COUNT(*) FROM prayer_entries WHERE log_id = :logId AND status = 'late'")
    suspend fun getLateCount(logId: Int): Int

    @Query("SELECT COUNT(*) FROM prayer_entries WHERE log_id = :logId AND status = 'missed'")
    suspend fun getMissedCount(logId: Int): Int
}
