package com.skyd.raca.ui.screen.settings.data

import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUIChange
import com.skyd.raca.base.IUiState
import com.skyd.raca.config.refreshArticleData
import com.skyd.raca.model.respository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(private var dataRepo: DataRepository) :
    BaseViewModel<IUiState, DataEvent, DataIntent>() {
    override fun initUiState(): IUiState {
        return object : IUiState {}
    }

    override fun IUIChange.checkStateOrEvent() = this as? IUiState to this as? DataEvent

    override fun Flow<DataIntent>.handleIntent(): Flow<IUIChange> = merge(
        doIsInstance<DataIntent.Start> {
            dataRepo.requestDeleteAllData()
                .mapToUIChange { data ->
                    DataEvent(deleteAllResultUiEvent = DeleteAllResultUiEvent.Success(data))
                }
                .defaultFinally()
                .onCompletion {
                    refreshArticleData.tryEmit(Unit)
                }
        },
    )
}