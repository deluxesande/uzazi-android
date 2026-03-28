package com.uzazi.app.domain.repositories

import com.uzazi.app.core.data.UserStats
import com.uzazi.app.domain.models.Badge
import kotlinx.coroutines.flow.Flow

interface BadgeRepository {
    fun getAllBadges(): Flow<List<Badge>>
    suspend fun checkAndUnlockBadges(stats: UserStats): List<Badge>
}
