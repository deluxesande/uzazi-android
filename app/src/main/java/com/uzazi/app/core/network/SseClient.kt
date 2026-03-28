package com.uzazi.app.core.network

import com.uzazi.app.BuildConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SseClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    fun streamChat(
        userMessage: String,
        sessionId: String,
        authToken: String,
        language: String
    ): Flow<String> = callbackFlow {
        val jsonBody = JSONObject().apply {
            put("message", userMessage)
            put("sessionId", sessionId)
            put("language", language)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("${BuildConfig.BASE_URL}api/chat/stream")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $authToken")
            .addHeader("Accept", "text/event-stream")
            .build()

        val factory = EventSources.createFactory(okHttpClient)
        var currentRetry = 0
        val maxRetries = 3
        var eventSource: EventSource? = null

        fun connect() {
            eventSource = factory.newEventSource(request, object : EventSourceListener() {
                override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                    if (data == "[DONE]") {
                        channel.close()
                    } else {
                        trySend(data)
                    }
                }

                override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                    if (currentRetry < maxRetries) {
                        currentRetry++
                        val backoffMillis = (1000 * Math.pow(2.0, (currentRetry - 1).toDouble())).toLong()
                        
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).kotlinx.coroutines.launch {
                            delay(backoffMillis)
                            connect()
                        }
                    } else {
                        close(t ?: Exception("SSE Stream Failed after retries"))
                    }
                }

                override fun onClosed(eventSource: EventSource) {
                    channel.close()
                }
            })
        }

        connect()

        awaitClose {
            eventSource?.cancel()
        }
    }
}
