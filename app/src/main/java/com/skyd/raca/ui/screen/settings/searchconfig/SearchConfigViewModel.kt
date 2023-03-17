package com.skyd.raca.ui.screen.settings.searchconfig

import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUIChange
import com.skyd.raca.base.IUiEvent
import com.skyd.raca.model.respository.SearchConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@HiltViewModel
class SearchConfigViewModel @Inject constructor(private var searchConfigRepo: SearchConfigRepository) :
    BaseViewModel<SearchConfigState, IUiEvent, SearchConfigIntent>() {
    override fun initUiState(): SearchConfigState {
        return SearchConfigState(
            searchDomainResultUiState = SearchDomainResultUiState.INIT
        )
    }

    override fun IUIChange.checkStateOrEvent() = this as? SearchConfigState to this as? IUiEvent

    override fun Flow<SearchConfigIntent>.handleIntent(): Flow<IUIChange> = merge(
        filterIsInstance<SearchConfigIntent.GetSearchDomain>().flatMapConcat {
            searchConfigRepo.requestGetSearchDomain()
                .mapToUIChange { data ->
                    copy(searchDomainResultUiState = SearchDomainResultUiState.SUCCESS(data))
                }
                .defaultFinally()
        },

        filterIsInstance<SearchConfigIntent.SetSearchDomain>().flatMapConcat { intent ->
            searchConfigRepo.requestSetSearchDomain(intent.searchDomainBean)
                .mapToUIChange { data ->
                    copy(searchDomainResultUiState = SearchDomainResultUiState.SUCCESS(data))
                }
                .defaultFinally()
        },
    )
}