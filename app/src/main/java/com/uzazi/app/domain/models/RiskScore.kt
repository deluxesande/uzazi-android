package com.uzazi.app.domain.models

data class RiskScore(
    val level: RiskLevel,
    val score: Int,
    val feedback: String
)

enum class RiskLevel {
    LOW, MEDIUM, HIGH, UNKNOWN
}
