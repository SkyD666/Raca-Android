package com.skyd.raca.ui.screen.settings.searchconfig

import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUiEvent
import com.skyd.raca.base.IUiIntent
import com.skyd.raca.model.respository.SearchConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchConfigViewModel @Inject constructor(private var searchConfigRepo: SearchConfigRepository) :
    BaseViewModel<SearchConfigState, IUiEvent, SearchConfigIntent>() {
    override fun initUiState(): SearchConfigState {
        return SearchConfigState(
            searchDomainResultUiState = SearchDomainResultUiState.INIT
        )
    }

    override fun handleIntent(intent: IUiIntent) {
        when (intent) {
            is SearchConfigIntent.GetSearchDomain -> {
                requestDataWithFlow(showLoading = true,
                    request = {
                        searchConfigRepo.requestGetSearchDomain()
                    },
                    successCallback = {
                        sendUiState {
                            copy(searchDomainResultUiState = SearchDomainResultUiState.SUCCESS(it))
                        }
                    }
                )
            }
            is SearchConfigIntent.SetSearchDomain -> {
                requestDataWithFlow(showLoading = true,
                    request = {
                        searchConfigRepo.requestSetSearchDomain(intent.searchDomainBean)
                    },
                    successCallback = {
                        sendUiState {
                            copy(searchDomainResultUiState = SearchDomainResultUiState.SUCCESS(it))
                        }
                    }
                )
            }
        }
    }
}