package com.skyd.raca.ui.screen.settings.data.importexport.file.exportdata

import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUIChange
import com.skyd.raca.base.IUiState
import com.skyd.raca.model.respository.ExportDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

class ExportDataViewModel(private var exportDataRepo: ExportDataRepository) :
    BaseViewModel<IUiState, ExportDataEvent, ExportDataIntent>() {
    override fun initUiState(): IUiState {
        return object : IUiState {}
    }

    override fun IUIChange.checkStateOrEvent() = this as? IUiState to this as? ExportDataEvent

    override fun Flow<ExportDataIntent>.handleIntent(): Flow<IUIChange> = merge(
        doIsInstance<ExportDataIntent.StartExport> { intent ->
            exportDataRepo.requestExportData(intent.dirUri)
                .mapToUIChange { data ->
                    ExportDataEvent(exportResultUiEvent = ExportResultUiEvent.Success(data))
                }
                .defaultFinally()
        },
    )
}
