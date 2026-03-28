package com.uzazi.app.core.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.uzazi.app.core.data.entities.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(badges: List<BadgeEntity>)

    @Query("SELECT * FROM badges")
    fun getAll(): Flow<List<BadgeEntity>>

    @Update
    suspend fun updateBadge(badge: BadgeEntity)

    @Query("SELECT * FROM badges")
    suspend fun getAllEntitiesSync(): List<BadgeEntity>

    @Query("SELECT * FROM badges WHERE id = :id")
    suspend fun getById(id: String): BadgeEntity?
}
