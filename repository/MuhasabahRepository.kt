package com.taqwa.hasib.repository

import com.taqwa.hasib.database.dao.DailyLogDao
import com.taqwa.hasib.database.dao.PrayerEntryDao
import com.taqwa.hasib.database.dao.UserSettingsDao
import com.taqwa.hasib.database.dao.WirdDao
import com.taqwa.hasib.database.dao.WirdEntryDao
import com.taqwa.hasib.database.entities.DailyLog
import com.taqwa.hasib.database.entities.PrayerEntry
import com.taqwa.hasib.database.entities.WirdEntry
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Repository layer for Muhasabah app.
 * Handles all data persistence logic with domain-aware operations.
 * Day boundaries are Fajr-to-Fajr as configured in user settings.
 */
class MuhasabahRepository(
    private val dailyLogDao: DailyLogDao,
    private val wirdEntryDao: WirdEntryDao,
    private val prayerEntryDao: PrayerEntryDao,
    private val wirdDao: WirdDao,
    private val userSettingsDao: UserSettingsDao
) {
    companion object {
        private const val DEFAULT_USER_ID = 1
        private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }

    /**
     * Calculates the log_date (Fajr cycle date) for a given DateTime.
     * 
     * Day logic:
     * - If current time >= Fajr time: log_date is today's date
     * - If current time < Fajr time: log_date is yesterday's date
     * 
     * @param currentTime The time to calculate from (default: now)
     * @param fajrTimeStr The Fajr time in "HH:mm" format (e.g., "05:00")
     * @return The log_date as ISO date string (YYYY-MM-DD)
     */
    private fun calculateFajrCycleDate(
        currentTime: LocalDateTime = LocalDateTime.now(),
        fajrTimeStr: String
    ): String {
        val fajrTime = LocalTime.parse(fajrTimeStr, timeFormatter)
        val currentDate = if (currentTime.toLocalTime() >= fajrTime) {
            currentTime.toLocalDate()
        } else {
            currentTime.toLocalDate().minusDays(1)
        }
        return currentDate.format(dateFormatter)
    }

    /**
     * Gets the Fajr time from user settings.
     * Falls back to default 05:00 if not configured.
     */
    private suspend fun getFajrTime(): String {
        val settings = userSettingsDao.getSettings() ?: return "05:00"
        return settings.fajrTime
    }

    /**
     * Gets today's Fajr cycle log date.
     */
    private suspend fun getTodaysLogDate(): String {
        val fajrTime = getFajrTime()
        return calculateFajrCycleDate(LocalDateTime.now(), fajrTime)
    }

    /**
     * Creates a new daily log for today's Fajr cycle.
     * 
     * @param userId The user ID (default: 1 for single-user app)
     * @return The log_id of the created log, or -1L if log already exists
     */
    suspend fun createDailyLog(userId: Int = DEFAULT_USER_ID): Long {
        val logDate = getTodaysLogDate()
        
        // Check if log already exists for this date
        val existingLog = dailyLogDao.getLogByDate(userId, logDate)
        if (existingLog != null) {
            return -1L // Log already exists
        }

        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val newLog = DailyLog(
            logId = 0, // Auto-generate
            userId = userId,
            logDate = logDate,
            submittedAt = now,
            lastEditedAt = null,
            isMissed = false
        )

        return dailyLogDao.insert(newLog)
    }

    /**
     * Checks if today's log exists.
     * 
     * @param userId The user ID (default: 1)
     * @return true if a log exists for today's Fajr cycle, false otherwise
     */
    suspend fun doesTodaysLogExist(userId: Int = DEFAULT_USER_ID): Boolean {
        val logDate = getTodaysLogDate()
        return dailyLogDao.getLogByDate(userId, logDate) != null
    }

    /**
     * Gets today's daily log, creating it if needed.
     * 
     * @param userId The user ID (default: 1)
     * @return The today's DailyLog entity
     */
    suspend fun getTodaysLog(userId: Int = DEFAULT_USER_ID): DailyLog? {
        val logDate = getTodaysLogDate()
        var log = dailyLogDao.getLogByDate(userId, logDate)
        
        if (log == null) {
            val logId = createDailyLog(userId)
            if (logId > 0) {
                log = dailyLogDao.getLogById(logId.toInt())
            }
        }
        
        return log
    }

    /**
     * Marks a wird entry with a status.
     * Creates the entry if it doesn't exist, updates if it does.
     * 
     * @param logId The daily log ID
     * @param wirdId The wird ID
     * @param status The status ("done", "missed", or "skipped")
     * @param valueCompleted The numeric value for quantitative wirds (null for boolean wirds)
     * @return The entry_id of the marked entry
     */
    suspend fun markWirdEntry(
        logId: Int,
        wirdId: Int,
        status: String,
        valueCompleted: Int? = null
    ): Long {
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        
        // Check if entry already exists
        var entry = wirdEntryDao.getEntry(logId, wirdId)
        
        return if (entry != null) {
            // Update existing entry
            val updatedEntry = entry.copy(
                status = status,
                valueCompleted = valueCompleted,
                recordedAt = now
            )
            wirdEntryDao.update(updatedEntry)
            entry.entryId.toLong()
        } else {
            // Create new entry
            val newEntry = WirdEntry(
                entryId = 0, // Auto-generate
                logId = logId,
                wirdId = wirdId,
                status = status,
                valueCompleted = valueCompleted,
                recordedAt = now
            )
            wirdEntryDao.insert(newEntry)
        }
    }

    /**
     * Updates the state of a prayer entry.
     * Creates the entry if it doesn't exist, updates if it does.
     * 
     * @param logId The daily log ID
     * @param prayerName The prayer name ("fajr", "dhuhr", "asr", "maghrib", "isha")
     * @param status The prayer status ("on_time", "late", or "missed")
     * @return The prayer_id of the updated entry
     */
    suspend fun updatePrayerStatus(
        logId: Int,
        prayerName: String,
        status: String
    ): Long {
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        
        // Check if entry already exists
        var entry = prayerEntryDao.getPrayerEntry(logId, prayerName)
        
        return if (entry != null) {
            // Update existing entry
            val updatedEntry = entry.copy(
                status = status,
                recordedAt = now
            )
            prayerEntryDao.update(updatedEntry)
            entry.prayerId.toLong()
        } else {
            // Create new entry
            val newEntry = PrayerEntry(
                prayerId = 0, // Auto-generate
                logId = logId,
                prayerName = prayerName,
                status = status,
                recordedAt = now
            )
            prayerEntryDao.insert(newEntry)
        }
    }

    /**
     * Soft-deletes a wird and all its future entries.
     * Historical data is preserved.
     * 
     * @param wirdId The wird ID to soft-delete
     * @param userId The user ID (default: 1)
     */
    suspend fun softDeleteWird(wirdId: Int, userId: Int = DEFAULT_USER_ID) {
        val wird = wirdDao.getWirdById(wirdId) ?: return
        
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val updatedWird = wird.copy(
            isDeleted = true,
            deletedAt = now
        )
        
        wirdDao.update(updatedWird)
    }

    /**
     * Gets all active wirds for a user (excludes soft-deleted wirds).
     * 
     * @param userId The user ID (default: 1)
     * @return List of active wirds
     */
    suspend fun getActiveWirds(userId: Int = DEFAULT_USER_ID) =
        wirdDao.getActiveWirds(userId)

    /**
     * Gets all custom wirds for a user (excludes locked and soft-deleted wirds).
     * 
     * @param userId The user ID (default: 1)
     * @return List of custom wirds
     */
    suspend fun getCustomWirds(userId: Int = DEFAULT_USER_ID) =
        wirdDao.getCustomWirds(userId)

    /**
     * Gets all entries for today's log.
     * 
     * @param userId The user ID (default: 1)
     * @return Pair of (wird_entries, prayer_entries)
     */
    suspend fun getTodaysEntries(userId: Int = DEFAULT_USER_ID): Pair<List<WirdEntry>, List<PrayerEntry>>? {
        val todaysLog = getTodaysLog(userId) ?: return null
        val wirdEntries = wirdEntryDao.getEntriesByLog(todaysLog.logId)
        val prayerEntries = prayerEntryDao.getEntriesByLog(todaysLog.logId)
        return Pair(wirdEntries, prayerEntries)
    }

    /**
     * Gets completion statistics for today's log.
     * 
     * @param userId The user ID (default: 1)
     * @return Triple of (completedWirdsCount, totalWirdsCount, isToday)
     */
    suspend fun getTodaysCompletionStats(userId: Int = DEFAULT_USER_ID): Triple<Int, Int, Boolean>? {
        val todaysLog = getTodaysLog(userId) ?: return null
        val completedCount = wirdEntryDao.getCompletedWirdsCount(todaysLog.logId)
        val totalCount = wirdEntryDao.getTotalWirdsCount(todaysLog.logId)
        return Triple(completedCount, totalCount, true)
    }
}
