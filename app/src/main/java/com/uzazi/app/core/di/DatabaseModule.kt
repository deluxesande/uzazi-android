package com.uzazi.app.core.di

import android.content.Context
import com.uzazi.app.core.data.AppDatabase
import com.uzazi.app.core.data.daos.BadgeDao
import com.uzazi.app.core.data.daos.CheckInDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideCheckInDao(database: AppDatabase): CheckInDao {
        return database.checkInDao()
    }

    @Provides
    fun provideBadgeDao(database: AppDatabase): BadgeDao {
        return database.badgeDao()
    }

    @Provides
    fun provideChatMessageDao(database: AppDatabase): com.uzazi.app.core.data.daos.ChatMessageDao {
        return database.chatMessageDao()
    }
}
