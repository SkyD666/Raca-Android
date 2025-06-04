package com.skyd.raca.di

import com.skyd.raca.ui.screen.add.AddViewModel
import com.skyd.raca.ui.screen.home.HomeViewModel
import com.skyd.raca.ui.screen.minitool.abstractemoji.AbstractEmojiViewModel
import com.skyd.raca.ui.screen.settings.data.DataViewModel
import com.skyd.raca.ui.screen.settings.data.importexport.cloud.webdav.WebDavViewModel
import com.skyd.raca.ui.screen.settings.data.importexport.file.exportdata.ExportDataViewModel
import com.skyd.raca.ui.screen.settings.data.importexport.file.importdata.ImportDataViewModel
import com.skyd.raca.ui.screen.settings.searchconfig.SearchConfigViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AddViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { AbstractEmojiViewModel(get()) }
    viewModel { DataViewModel(get()) }
    viewModel { ExportDataViewModel(get()) }
    viewModel { ImportDataViewModel(get()) }
    viewModel { WebDavViewModel(get()) }
    viewModel { SearchConfigViewModel(get()) }
}