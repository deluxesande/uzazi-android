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

    private const val MODEL_NAME = "gemini-1.5-flash"
    private const val LOCATION = "us-central1"

    @Provides
    @Singleton
    fun provideFirebaseAI(): FirebaseAI {
        // Using Vertex AI backend with specified location
        return FirebaseAI.getInstance(FirebaseApp.getInstance(), GenerativeBackend.vertexAI(LOCATION))
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
