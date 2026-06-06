package com.taqwa.hasib.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.taqwa.hasib.database.entities.DailyLog

@Dao
interface DailyLogDao {
    @Insert
    suspend fun insert(dailyLog: DailyLog): Long

    @Update
    suspend fun update(dailyLog: DailyLog)

    @Delete
    suspend fun delete(dailyLog: DailyLog)

    @Query("SELECT * FROM daily_logs WHERE log_id = :logId")
    suspend fun getLogById(logId: Int): DailyLog?

    @Query("SELECT * FROM daily_logs WHERE user_id = :userId AND log_date = :logDate")
    suspend fun getLogByDate(userId: Int, logDate: String): DailyLog?

    @Query("SELECT * FROM daily_logs WHERE user_id = :userId ORDER BY log_date DESC")
    suspend fun getAllLogs(userId: Int): List<DailyLog>

    @Query("SELECT * FROM daily_logs WHERE user_id = :userId AND log_date >= :startDate AND log_date <= :endDate ORDER BY log_date DESC")
    suspend fun getLogsByDateRange(userId: Int, startDate: String, endDate: String): List<DailyLog>

    @Query("SELECT COUNT(*) FROM daily_logs WHERE user_id = :userId")
    suspend fun getTotalLogCount(userId: Int): Int

    @Query("SELECT COUNT(*) FROM daily_logs WHERE user_id = :userId AND is_missed = 1")
    suspend fun getMissedDaysCount(userId: Int): Int
}
