package com.uzazi.app.core.di

import com.google.cloud.translate.v3.TranslationServiceClient
import com.google.cloud.vertexai.VertexAI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    private const val PROJECT_ID = "ai4health-491609"
    private const val LOCATION = "us-central1"

    @Provides
    @Singleton
    fun provideVertexAI(): VertexAI {
        return VertexAI(PROJECT_ID, LOCATION)
    }

    @Provides
    @Singleton
    fun provideTranslationServiceClient(): TranslationServiceClient {
        return TranslationServiceClient.create()
    }

    @Provides
    @Singleton
    fun provideProjectId(): String = PROJECT_ID
}
