package com.uzazi.app.data.repositories

import com.uzazi.app.core.data.UserStats
import com.uzazi.app.core.data.daos.BadgeDao
import com.uzazi.app.core.data.entities.BadgeEntity
import com.uzazi.app.domain.models.Badge
import com.uzazi.app.domain.repositories.BadgeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BadgeRepositoryImpl @Inject constructor(
    private val badgeDao: BadgeDao
) : BadgeRepository {

    override fun getAllBadges(): Flow<List<Badge>> {
        return badgeDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun checkAndUnlockBadges(stats: UserStats): List<Badge> {
        val allEntities = badgeDao.getAllEntitiesSync()
        val newlyUnlocked = mutableListOf<Badge>()

        allEntities.forEach { entity ->
            if (entity.unlockedAt == null && shouldUnlock(entity.id, stats)) {
                val updated = entity.copy(unlockedAt = System.currentTimeMillis())
                badgeDao.updateBadge(updated)
                newlyUnlocked.add(updated.toDomain())
            }
        }

        return newlyUnlocked
    }

    private fun shouldUnlock(id: String, stats: UserStats): Boolean {
        return when (id) {
            "1" -> stats.totalPetals > 0 // First Bloom: checked in once
            "2" -> stats.streakCount >= 3 // Garden Starter
            "3" -> stats.streakCount >= 7 // Blooming Mama
            "4" -> stats.streakCount >= 30 // Bloom Queen
            "5" -> stats.totalPetals >= 30 // Full Garden
            "6" -> stats.nightSessionCount >= 1 // Night Owl
            "7" -> stats.sharedCount >= 1 // Brave Voice
            "8" -> stats.soughtHelpCount >= 1 // Resilient Mama
            "comeback" -> stats.comebackCount >= 1 // Comeback Mama
            else -> false
        }
    }

    private fun BadgeEntity.toDomain() = Badge(
        id = id,
        name = name,
        description = description,
        iconResId = iconResId,
        unlockedAt = unlockedAt
    )
    
    private suspend fun BadgeDao.getAllEntitiesSync(): List<BadgeEntity> {
        return badgeDao.getAllEntitiesSync()
    }
}
