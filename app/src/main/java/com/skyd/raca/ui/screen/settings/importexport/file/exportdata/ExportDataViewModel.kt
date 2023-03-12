package com.skyd.raca.ui.screen.settings.importexport.file.exportdata

import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUiIntent
import com.skyd.raca.base.IUiState
import com.skyd.raca.model.respository.ExportDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExportDataViewModel @Inject constructor(private var exportDataRepo: ExportDataRepository) :
    BaseViewModel<IUiState, ExportDataEvent, ExportDataIntent>() {
    override fun initUiState(): IUiState {
        return object : IUiState {}
    }

    override fun handleIntent(intent: IUiIntent) {
        when (intent) {
            is ExportDataIntent.StartExport -> {
                requestDataWithFlow(showLoading = true,
                    request = { exportDataRepo.requestExportData(intent.dirUri) },
                    successCallback = {
                        sendUiEvent(
                            ExportDataEvent(exportResultUiEvent = ExportResultUiEvent.SUCCESS(it))
                        )
                    }
                )
            }
        }
    }
}
