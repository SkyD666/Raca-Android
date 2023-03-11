package com.skyd.raca.ui.screen.settings.importexport.file.exportdata

import android.util.Log
import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUiIntent
import com.skyd.raca.model.respository.ExportDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExportDataViewModel @Inject constructor(var exportDataRepo: ExportDataRepository) :
    BaseViewModel<ExportDataState, ExportDataIntent>() {
    override fun initUiState(): ExportDataState {
        return ExportDataState(ExportResultUiState.INIT)
    }

    override fun handleIntent(intent: IUiIntent) {
        when (intent) {
            is ExportDataIntent.StartExport -> {
                requestDataWithFlow(showLoading = true,
                    request = { exportDataRepo.requestExportData(intent.dirUri) },
                    successCallback = {
                        sendUiState {
                            Log.e("TAG", "copy: 123", )
                            copy(exportResultUiState = ExportResultUiState.SUCCESS(it))
                        }
                    }
                )
            }
        }
    }
}
