package com.uzazi.app.data.repositories

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.uzazi.app.core.security.SecureStorage
import com.uzazi.app.domain.models.User
import com.uzazi.app.domain.repositories.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val secureStorage: SecureStorage
) : UserRepository {

    override suspend fun signInWithPhone(phone: String): Flow<Result<String>> = callbackFlow {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-verification not handled here for simplicity
            }

            override fun onVerificationFailed(e: FirebaseException) {
                trySend(Result.failure(e))
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                trySend(Result.success(verificationId))
            }
        }

        // Note: This requires an Activity context in a real app, 
        // but for Phase 2 we mock the trigger or expect it from the caller.
        // In a real implementation, we'd pass the activity or use a provider.
        // For the sake of the scaffold, we'll assume the caller provides context if needed.
        // Here we just define the flow logic.
        
        awaitClose { }
    }

    override suspend fun verifyOtp(verificationId: String, otp: String): Flow<Result<User>> = flow {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Auth failed")
            
            val user = User(
                id = firebaseUser.uid,
                name = "", 
                email = firebaseUser.email ?: "",
                phoneNumber = firebaseUser.phoneNumber,
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
            val firebaseUser = authResult.user ?: throw Exception("Auth failed")
            
            val user = User(
                id = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                phoneNumber = firebaseUser.phoneNumber,
                babyBirthDate = null
            )
            
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
            "points" to user.points
        )
        firestore.collection("users").document(user.id).set(userMap).await()
        
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
