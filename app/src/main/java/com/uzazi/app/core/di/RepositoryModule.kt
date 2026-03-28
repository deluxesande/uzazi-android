package com.uzazi.app.core.di

import com.uzazi.app.data.repositories.UserRepositoryImpl
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
        chatRepositoryImpl: com.uzazi.app.data.repositories.ChatRepositoryImpl
    ): com.uzazi.app.domain.repositories.ChatRepository

    @Binds
    @Singleton
    abstract fun bindBadgeRepository(
        badgeRepositoryImpl: com.uzazi.app.data.repositories.BadgeRepositoryImpl
    ): com.uzazi.app.domain.repositories.BadgeRepository
}
