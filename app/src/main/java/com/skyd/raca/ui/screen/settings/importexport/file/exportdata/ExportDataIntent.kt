package com.skyd.raca.ui.screen.settings.importexport.file.exportdata

import android.net.Uri
import com.skyd.raca.base.IUiIntent

sealed class ExportDataIntent : IUiIntent {
    data class StartExport(val dirUri: Uri) : ExportDataIntent()
}