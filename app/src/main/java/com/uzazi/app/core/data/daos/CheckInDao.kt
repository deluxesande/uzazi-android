package com.uzazi.app.core.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uzazi.app.core.data.entities.CheckInEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckInEntity)

    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC")
    fun getAll(): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM check_ins WHERE isSynced = 0")
    suspend fun getUnsynced(): List<CheckInEntity>

    @Query("UPDATE check_ins SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC LIMIT 1")
    fun getLatest(): Flow<CheckInEntity?>
}
