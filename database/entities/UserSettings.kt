package com.taqwa.hasib.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: Int = 1,
    @ColumnInfo(name = "fajr_time")
    val fajrTime: String,
    @ColumnInfo(name = "preset_tier")
    val presetTier: Int,
    @ColumnInfo(name = "language")
    val language: String,
    @ColumnInfo(name = "notification_time")
    val notificationTime: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String
)
