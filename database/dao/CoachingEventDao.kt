package com.taqwa.hasib.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.taqwa.hasib.database.entities.CoachingEvent

@Dao
interface CoachingEventDao {
    @Insert
    suspend fun insert(coachingEvent: CoachingEvent): Long

    @Update
    suspend fun update(coachingEvent: CoachingEvent)

    @Delete
    suspend fun delete(coachingEvent: CoachingEvent)

    @Query("SELECT * FROM coaching_events WHERE event_id = :eventId")
    suspend fun getEventById(eventId: Int): CoachingEvent?

    @Query("SELECT * FROM coaching_events WHERE user_id = :userId ORDER BY triggered_at DESC")
    suspend fun getEventsByUser(userId: Int): List<CoachingEvent>

    @Query("SELECT * FROM coaching_events WHERE user_id = :userId AND seen_at IS NULL ORDER BY triggered_at DESC")
    suspend fun getUnseenEvents(userId: Int): List<CoachingEvent>

    @Query("SELECT * FROM coaching_events WHERE user_id = :userId AND event_type = :eventType ORDER BY triggered_at DESC")
    suspend fun getEventsByType(userId: Int, eventType: String): List<CoachingEvent>

    @Query("SELECT * FROM coaching_events WHERE user_id = :userId AND wird_id = :wirdId ORDER BY triggered_at DESC")
    suspend fun getEventsByWird(userId: Int, wirdId: Int): List<CoachingEvent>

    @Query("SELECT COUNT(*) FROM coaching_events WHERE user_id = :userId AND seen_at IS NULL")
    suspend fun getUnseenEventCount(userId: Int): Int
}
