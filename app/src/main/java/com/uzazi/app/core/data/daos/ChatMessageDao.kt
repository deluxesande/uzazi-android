package com.uzazi.app.core.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uzazi.app.core.data.entities.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE id LIKE :sessionId || '%' ORDER BY timestamp ASC")
    fun getSessionMessages(sessionId: String): Flow<List<ChatMessageEntity>>

    @Query("DELETE FROM chat_messages WHERE timestamp < :threshold")
    suspend fun deleteOldMessages(threshold: Long)
}
