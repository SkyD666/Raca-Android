package com.skyd.raca.di

import com.skyd.raca.model.respository.AbstractEmojiRepository
import com.skyd.raca.model.respository.AddRepository
import com.skyd.raca.model.respository.DataRepository
import com.skyd.raca.model.respository.ExportDataRepository
import com.skyd.raca.model.respository.HomeRepository
import com.skyd.raca.model.respository.ImportDataRepository
import com.skyd.raca.model.respository.SearchConfigRepository
import com.skyd.raca.model.respository.WebDavRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory { AbstractEmojiRepository() }
    factory { AddRepository(get()) }
    factory { DataRepository(get()) }
    factory { ExportDataRepository(get(), get()) }
    factory { HomeRepository(get()) }
    factory { ImportDataRepository(get()) }
    factory { SearchConfigRepository(get()) }
    factory { WebDavRepository(get()) }
}