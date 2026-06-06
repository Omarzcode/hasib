package com.taqwa.hasib.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "prayer_entries",
    foreignKeys = [
        ForeignKey(
            entity = DailyLog::class,
            parentColumns = ["log_id"],
            childColumns = ["log_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        androidx.room.Index("log_id")
    ]
)
data class PrayerEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "prayer_id")
    val prayerId: Int = 0,
    @ColumnInfo(name = "log_id")
    val logId: Int,
    @ColumnInfo(name = "prayer_name")
    val prayerName: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "recorded_at")
    val recordedAt: String
)
