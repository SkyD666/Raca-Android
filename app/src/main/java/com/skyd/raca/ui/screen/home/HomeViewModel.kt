package com.skyd.raca.ui.screen.home

import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUiEvent
import com.skyd.raca.base.IUiIntent
import com.skyd.raca.config.currentArticleUuid
import com.skyd.raca.model.respository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private var homeRepo: HomeRepository) :
    BaseViewModel<HomeState, IUiEvent, HomeIntent>() {
    override fun initUiState(): HomeState {
        return HomeState(
            ArticleDetailUiState.INIT(currentArticleUuid),
            SearchResultUiState.INIT,
        )
    }

    override fun handleIntent(intent: IUiIntent) {
        when (intent) {
            is HomeIntent.GetArticleWithTagsList -> {
                requestDataWithFlow(showLoading = false,
                    request = { homeRepo.requestArticleWithTagsList(intent.keyword) },
                    successCallback = { data ->
                        sendUiState {
                            copy(searchResultUiState = SearchResultUiState.SUCCESS(data))
                        }
                    }
                )
            }
            is HomeIntent.GetArticleDetails -> {
                requestDataWithFlow(showLoading = false,
                    request = { homeRepo.requestArticleWithTagsDetail(intent.articleUuid) },
                    successCallback = { data ->
                        sendUiState {
                            copy(articleDetailUiState = ArticleDetailUiState.SUCCESS(data))
                        }
                    }
                )
            }
            is HomeIntent.DeleteArticleWithTags -> {
                requestDataWithFlow(showLoading = false,
                    request = { homeRepo.requestDeleteArticleWithTagsDetail(intent.articleUuid) },
                    successCallback = {
                        sendUiState {
                            copy(articleDetailUiState = ArticleDetailUiState.INIT(""))
                        }
                    }
                )
            }
        }
    }
}