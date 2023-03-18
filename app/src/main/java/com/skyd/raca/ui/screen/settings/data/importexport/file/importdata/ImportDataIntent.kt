package com.skyd.raca.ui.screen.settings.data.importexport.file.importdata

import android.net.Uri
import com.skyd.raca.base.IUiIntent

sealed class ImportDataIntent : IUiIntent {
    data class StartImport(val articleUri: Uri, val tagUri: Uri) : ImportDataIntent()
}