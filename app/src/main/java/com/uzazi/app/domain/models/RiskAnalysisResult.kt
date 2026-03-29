package com.uzazi.app.domain.models

data class RiskAnalysisResult(
    val riskLevel: RiskLevel,
    val clinicalSummary: String
)
