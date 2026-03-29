package com.uzazi.app.core.di

import com.google.firebase.FirebaseApp
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.GenerativeBackend
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    private const val MODEL_NAME = "gemini-2.0-flash"

    @Provides
    @Singleton
    fun provideFirebaseAI(): FirebaseAI {
        // Switching to Google AI backend which uses the API Key from google-services.json
        // and doesn't require full Vertex AI Google Cloud setup.
        return FirebaseAI.getInstance(FirebaseApp.getInstance(), GenerativeBackend.googleAI())
    }

    @Provides
    @Singleton
    @Named("risk_analyzer")
    fun provideRiskAnalyzerModel(firebaseAI: FirebaseAI): GenerativeModel {
        return firebaseAI.generativeModel(MODEL_NAME)
    }

    @Provides
    @Singleton
    @Named("companion")
    fun provideCompanionModel(firebaseAI: FirebaseAI): GenerativeModel {
        return firebaseAI.generativeModel(MODEL_NAME)
    }
}
