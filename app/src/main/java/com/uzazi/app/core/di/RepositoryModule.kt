package com.uzazi.app.core.di

import com.uzazi.app.data.repositories.AiAnalysisRepositoryImpl
import com.uzazi.app.data.repositories.BadgeRepositoryImpl
import com.uzazi.app.data.repositories.ChatRepositoryImpl
import com.uzazi.app.data.repositories.CheckInRepositoryImpl
import com.uzazi.app.data.repositories.UserRepositoryImpl
import com.uzazi.app.domain.repositories.AiAnalysisRepository
import com.uzazi.app.domain.repositories.BadgeRepository
import com.uzazi.app.domain.repositories.ChatRepository
import com.uzazi.app.domain.repositories.CheckInRepository
import com.uzazi.app.domain.repositories.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindCheckInRepository(
        checkInRepositoryImpl: CheckInRepositoryImpl
    ): CheckInRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindBadgeRepository(
        badgeRepositoryImpl: BadgeRepositoryImpl
    ): BadgeRepository
    @Binds
    @Singleton
    abstract fun bindAiAnalysisRepository(
        impl: AiAnalysisRepositoryImpl
    ): AiAnalysisRepository
}
