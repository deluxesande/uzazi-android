package com.uzazi.app.domain.repositories

import com.uzazi.app.domain.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // Email/Password
    suspend fun signInWithEmail(email: String, password: String): Flow<Result<User>>
    suspend fun signUpWithEmail(email: String, password: String, name: String): Flow<Result<User>>
    
    // Google
    suspend fun signInWithGoogle(idToken: String): Flow<Result<User>>
    
    // Phone (keeping for flexibility)
    suspend fun signInWithPhone(phone: String): Flow<Result<String>>
    suspend fun verifyOtp(verificationId: String, otp: String): Flow<Result<User>>
    
    suspend fun saveUserProfile(user: User): Flow<Result<Unit>>
    fun getCurrentUser(): User?
    suspend fun signOut()
}
