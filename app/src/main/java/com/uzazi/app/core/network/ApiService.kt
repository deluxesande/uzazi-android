package com.uzazi.app.core.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/checkin")
    suspend fun submitCheckIn(@Body r: CheckInRequest): Response<CheckInResponse>

    @GET("api/checkin/history/{userId}")
    suspend fun getHistory(@Path("userId") userId: String): Response<List<CheckInResponse>>

    @POST("api/risk/score")
    suspend fun getRiskScore(@Body r: RiskRequest): Response<RiskResponse>

    @POST("api/absence/flag")
    suspend fun flagAbsence(@Body r: AbsenceRequest): Response<Unit>

    @POST("api/qr/generate")
    suspend fun generateQr(@Body r: QrRequest): Response<QrResponse>
}

data class CheckInRequest(
    val userId: String,
    val mood: Int,
    val symptoms: List<String>,
    val connection: Int,
    val sleep: Int,
    val support: Int,
    val timestamp: Long
)

data class CheckInResponse(
    val id: String,
    val userId: String,
    val mood: Int,
    val riskLevel: String,
    val timestamp: Long
)

data class RiskRequest(val checkInId: String)
data class RiskResponse(val riskLevel: String, val score: Int, val feedback: String)

data class AbsenceRequest(val userId: String, val lastSeen: Long)

data class QrRequest(val userId: String)
data class QrResponse(val qrCodeBase64: String)
