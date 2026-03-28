package com.uzazi.app.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.uzazi.app.core.security.SecureStorage
import com.uzazi.app.domain.models.User
import com.uzazi.app.domain.repositories.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val secureStorage: SecureStorage
) : UserRepository {

    override suspend fun signInWithEmail(email: String, password: String): Flow<Result<User>> = flow {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Login failed")
            
            val user = User(
                id = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: email,
                phoneNumber = firebaseUser.phoneNumber,
                babyBirthDate = null
            )
            
            saveUserToFirestore(user)
            emit(Result.success(user))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String, name: String): Flow<Result<User>> = flow {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Registration failed")
            
            val user = User(
                id = firebaseUser.uid,
                name = name,
                email = email,
                phoneNumber = null,
                babyBirthDate = null
            )
            
            saveUserToFirestore(user)
            emit(Result.success(user))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Flow<Result<User>> = flow {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Google Auth failed")
            
            val user = User(
                id = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                phoneNumber = firebaseUser.phoneNumber,
                babyBirthDate = null
            )
            
            // Try to save to Firestore, but don't let a failure here block the login flow
            try {
                saveUserToFirestore(user)
            } catch (e: Exception) {
                // Log the error but continue so the user can still use the app
                android.util.Log.e("Auth", "Firestore sync failed: ${e.message}")
            }
            
            emit(Result.success(user))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // Phone Auth (kept for complete interface coverage)
    override suspend fun signInWithPhone(phone: String): Flow<Result<String>> = callbackFlow {
        // Implementation omitted for brevity as you primarily use Email/Google
        awaitClose { }
    }

    override suspend fun verifyOtp(verificationId: String, otp: String): Flow<Result<User>> = flow {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Auth failed")
            val user = User(firebaseUser.uid, "", firebaseUser.email ?: "", firebaseUser.phoneNumber, null)
            saveUserToFirestore(user)
            emit(Result.success(user))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private suspend fun saveUserToFirestore(user: User) {
        val userMap = hashMapOf(
            "id" to user.id,
            "name" to user.name,
            "email" to user.email,
            "phoneNumber" to user.phoneNumber,
            "role" to "mama",
            "points" to user.points,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("users").document(user.id).set(userMap, com.google.firebase.firestore.SetOptions.merge()).await()
        
        secureStorage.saveString(SecureStorage.KEY_USER_ID, user.id)
        auth.currentUser?.getIdToken(false)?.await()?.token?.let {
            secureStorage.saveString(SecureStorage.KEY_AUTH_TOKEN, it)
        }
    }

    override suspend fun saveUserProfile(user: User): Flow<Result<Unit>> = flow {
        try {
            firestore.collection("users").document(user.id).set(user, com.google.firebase.firestore.SetOptions.merge()).await()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            phoneNumber = firebaseUser.phoneNumber,
            babyBirthDate = secureStorage.getLong(SecureStorage.KEY_DELIVERY_DATE).takeIf { it != 0L }
        )
    }

    override suspend fun signOut() {
        auth.signOut()
        secureStorage.clear()
    }
}
