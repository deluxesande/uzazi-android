package com.uzazi.app.domain.models

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val babyBirthDate: Long?,
    val points: Int = 0,
    val trustedContactPhone: String? = null
)
