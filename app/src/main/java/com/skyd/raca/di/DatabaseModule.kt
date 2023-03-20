package com.skyd.raca.di

import android.content.Context
import com.skyd.raca.db.AppDatabase
import com.skyd.raca.db.dao.ArticleDao
import com.skyd.raca.db.dao.SearchDomainDao
import com.skyd.raca.db.dao.TagDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideArticleDao(database: AppDatabase): ArticleDao = database.articleDao()

    @Provides
    @Singleton
    fun provideTagDao(database: AppDatabase): TagDao = database.tagDao()

    @Provides
    @Singleton
    fun provideGroupDao(database: AppDatabase): SearchDomainDao = database.searchDomainDao()
}
