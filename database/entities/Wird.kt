package com.taqwa.hasib.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wirds")
data class Wird(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "wird_id")
    val wirdId: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "is_locked")
    val isLocked: Boolean,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "target_value")
    val targetValue: Int? = null,
    @ColumnInfo(name = "target_unit")
    val targetUnit: String? = null,
    @ColumnInfo(name = "frequency")
    val frequency: String,
    @ColumnInfo(name = "frequency_detail")
    val frequencyDetail: String? = null,
    @ColumnInfo(name = "tier_required")
    val tierRequired: Int,
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,
    @ColumnInfo(name = "deleted_at")
    val deletedAt: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String
)
