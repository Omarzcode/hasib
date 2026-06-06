package com.taqwa.hasib.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.taqwa.hasib.database.entities.Wird

@Dao
interface WirdDao {
    @Insert
    suspend fun insert(wird: Wird): Long

    @Update
    suspend fun update(wird: Wird)

    @Delete
    suspend fun delete(wird: Wird)

    @Query("SELECT * FROM wirds WHERE wird_id = :wirdId")
    suspend fun getWirdById(wirdId: Int): Wird?

    @Query("SELECT * FROM wirds WHERE user_id = :userId AND is_deleted = 0")
    suspend fun getActiveWirds(userId: Int): List<Wird>

    @Query("SELECT * FROM wirds WHERE user_id = :userId")
    suspend fun getAllWirds(userId: Int): List<Wird>

    @Query("SELECT * FROM wirds WHERE user_id = :userId AND is_locked = 1 AND tier_required <= :tier")
    suspend fun getLockedWirds(userId: Int, tier: Int): List<Wird>

    @Query("SELECT * FROM wirds WHERE user_id = :userId AND is_locked = 0 AND is_deleted = 0")
    suspend fun getCustomWirds(userId: Int): List<Wird>
}
