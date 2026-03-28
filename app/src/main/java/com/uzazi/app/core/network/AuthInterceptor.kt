package com.uzazi.app.core.network

import com.uzazi.app.core.security.SecureStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val secureStorage: SecureStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = secureStorage.getString(SecureStorage.KEY_AUTH_TOKEN)
        val request = chain.request().newBuilder()
        
        if (token != null) {
            request.addHeader("Authorization", "Bearer $token")
        }
        
        return chain.proceed(request.build())
    }
}
