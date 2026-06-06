package com.taqwa.hasib.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "log_id")
    val logId: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "log_date")
    val logDate: String,
    @ColumnInfo(name = "submitted_at")
    val submittedAt: String,
    @ColumnInfo(name = "last_edited_at")
    val lastEditedAt: String? = null,
    @ColumnInfo(name = "is_missed")
    val isMissed: Boolean = false
)
