package com.taqwa.hasib.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "wird_entries",
    foreignKeys = [
        ForeignKey(
            entity = DailyLog::class,
            parentColumns = ["log_id"],
            childColumns = ["log_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Wird::class,
            parentColumns = ["wird_id"],
            childColumns = ["wird_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        androidx.room.Index("log_id"),
        androidx.room.Index("wird_id")
    ]
)
data class WirdEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entry_id")
    val entryId: Int = 0,
    @ColumnInfo(name = "log_id")
    val logId: Int,
    @ColumnInfo(name = "wird_id")
    val wirdId: Int,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "value_completed")
    val valueCompleted: Int? = null,
    @ColumnInfo(name = "recorded_at")
    val recordedAt: String
)
