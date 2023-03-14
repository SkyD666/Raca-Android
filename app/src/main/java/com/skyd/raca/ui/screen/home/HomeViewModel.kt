package com.skyd.raca.ui.screen.home

import com.skyd.raca.appContext
import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUiEvent
import com.skyd.raca.base.IUiIntent
import com.skyd.raca.ext.dataStore
import com.skyd.raca.ext.get
import com.skyd.raca.model.preference.CurrentArticleUuidPreference
import com.skyd.raca.model.respository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private var homeRepo: HomeRepository) :
    BaseViewModel<HomeState, IUiEvent, HomeIntent>() {
    override fun initUiState(): HomeState {
        return HomeState(
            ArticleDetailUiState.INIT(
                appContext.dataStore
                    .get(CurrentArticleUuidPreference.key) ?: CurrentArticleUuidPreference.default
            ),
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
                if (intent.articleUuid.isBlank()) {
                    sendUiState {
                        copy(articleDetailUiState = ArticleDetailUiState.INIT())
                    }
                } else {
                    requestDataWithFlow(showLoading = false,
                        request = { homeRepo.requestArticleWithTagsDetail(intent.articleUuid) },
                        successCallback = { data ->
                            CurrentArticleUuidPreference.put(
                                context = appContext,
                                scope = this,
                                value = data.article.uuid
                            )
                            sendUiState {
                                copy(articleDetailUiState = ArticleDetailUiState.SUCCESS(data))
                            }
                        }
                    )
                }
            }
            is HomeIntent.DeleteArticleWithTags -> {
                requestDataWithFlow(showLoading = false,
                    request = { homeRepo.requestDeleteArticleWithTagsDetail(intent.articleUuid) },
                    successCallback = {
                        CurrentArticleUuidPreference.put(
                            context = appContext,
                            scope = this,
                            value = CurrentArticleUuidPreference.default
                        )
                        sendUiState {
                            copy(articleDetailUiState = ArticleDetailUiState.INIT())
                        }
                    }
                )
            }
        }
    }
}