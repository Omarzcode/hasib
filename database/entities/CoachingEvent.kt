package com.taqwa.hasib.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coaching_events")
data class CoachingEvent(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "event_id")
    val eventId: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "event_type")
    val eventType: String,
    @ColumnInfo(name = "wird_id")
    val wirdId: Int? = null,
    @ColumnInfo(name = "triggered_at")
    val triggeredAt: String,
    @ColumnInfo(name = "seen_at")
    val seenAt: String? = null,
    @ColumnInfo(name = "action_taken")
    val actionTaken: String? = null
)
