package com.skyd.raca.ui.screen.settings.searchconfig

import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUIChange
import com.skyd.raca.base.IUiEvent
import com.skyd.raca.model.respository.SearchConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

class SearchConfigViewModel(private var searchConfigRepo: SearchConfigRepository) :
    BaseViewModel<SearchConfigState, IUiEvent, SearchConfigIntent>() {
    override fun initUiState(): SearchConfigState {
        return SearchConfigState(
            searchDomainResultUiState = SearchDomainResultUiState.Init
        )
    }

    override fun IUIChange.checkStateOrEvent() = this as? SearchConfigState to this as? IUiEvent

    override fun Flow<SearchConfigIntent>.handleIntent(): Flow<IUIChange> = merge(
        doIsInstance<SearchConfigIntent.GetSearchDomain> {
            searchConfigRepo.requestGetSearchDomain()
                .mapToUIChange { data ->
                    copy(searchDomainResultUiState = SearchDomainResultUiState.Success(data))
                }
                .defaultFinally()
        },

        doIsInstance<SearchConfigIntent.SetSearchDomain> { intent ->
            searchConfigRepo.requestSetSearchDomain(intent.searchDomainBean)
                .mapToUIChange { data ->
                    copy(searchDomainResultUiState = SearchDomainResultUiState.Success(data))
                }
                .defaultFinally()
        },
    )
}