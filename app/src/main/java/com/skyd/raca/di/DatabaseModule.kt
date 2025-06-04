package com.skyd.raca.di

import com.skyd.raca.db.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.getInstance(get()) }
    single { get<AppDatabase>().articleDao() }
    single { get<AppDatabase>().tagDao() }
    single { get<AppDatabase>().searchDomainDao() }
}