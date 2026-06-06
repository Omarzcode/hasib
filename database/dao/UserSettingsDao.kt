package com.taqwa.hasib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.taqwa.hasib.database.entities.UserSettings

@Dao
interface UserSettingsDao {
    @Insert
    suspend fun insert(userSettings: UserSettings)

    @Update
    suspend fun update(userSettings: UserSettings)

    @Query("SELECT * FROM user_settings WHERE user_id = :userId")
    suspend fun getUserSettings(userId: Int): UserSettings?

    @Query("SELECT * FROM user_settings LIMIT 1")
    suspend fun getSettings(): UserSettings?
}
