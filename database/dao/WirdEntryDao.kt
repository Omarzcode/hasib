package com.taqwa.hasib.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.taqwa.hasib.database.entities.WirdEntry

@Dao
interface WirdEntryDao {
    @Insert
    suspend fun insert(wirdEntry: WirdEntry): Long

    @Update
    suspend fun update(wirdEntry: WirdEntry)

    @Delete
    suspend fun delete(wirdEntry: WirdEntry)

    @Query("SELECT * FROM wird_entries WHERE entry_id = :entryId")
    suspend fun getEntryById(entryId: Int): WirdEntry?

    @Query("SELECT * FROM wird_entries WHERE log_id = :logId")
    suspend fun getEntriesByLog(logId: Int): List<WirdEntry>

    @Query("SELECT * FROM wird_entries WHERE wird_id = :wirdId")
    suspend fun getEntriesByWird(wirdId: Int): List<WirdEntry>

    @Query("SELECT * FROM wird_entries WHERE log_id = :logId AND wird_id = :wirdId")
    suspend fun getEntry(logId: Int, wirdId: Int): WirdEntry?

    @Query("SELECT COUNT(*) FROM wird_entries WHERE log_id = :logId AND status = 'done'")
    suspend fun getCompletedWirdsCount(logId: Int): Int

    @Query("SELECT COUNT(*) FROM wird_entries WHERE log_id = :logId")
    suspend fun getTotalWirdsCount(logId: Int): Int

    @Query("SELECT COUNT(*) FROM wird_entries WHERE wird_id = :wirdId AND status = 'done'")
    suspend fun getCompletedCountForWird(wirdId: Int): Int
}
