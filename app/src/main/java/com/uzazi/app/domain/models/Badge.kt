package com.uzazi.app.domain.models

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val iconResId: Int,
    val unlockedAt: Long? = null
)
