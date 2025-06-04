package com.skyd.raca.ui.screen.home

import androidx.lifecycle.viewModelScope
import com.skyd.raca.appContext
import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUIChange
import com.skyd.raca.base.IUiEvent
import com.skyd.raca.ext.dataStore
import com.skyd.raca.ext.get
import com.skyd.raca.model.preference.CurrentArticleUuidPreference
import com.skyd.raca.model.respository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

class HomeViewModel(private var homeRepo: HomeRepository) :
    BaseViewModel<HomeState, IUiEvent, HomeIntent>() {
    override fun initUiState(): HomeState {
        return HomeState(
            ArticleDetailUiState.Init(
                appContext.dataStore
                    .get(CurrentArticleUuidPreference.key) ?: CurrentArticleUuidPreference.default
            ),
            SearchResultUiState.Init,
        )
    }

    override fun IUIChange.checkStateOrEvent() = this as? HomeState? to this as? IUiEvent

    override fun Flow<HomeIntent>.handleIntent(): Flow<IUIChange> = merge(
        doIsInstance<HomeIntent.GetArticleWithTagsList> { intent ->
            homeRepo.requestArticleWithTagsList(intent.keyword)
                .mapToUIChange { data ->
                    copy(searchResultUiState = SearchResultUiState.Success(data))
                }
                .defaultFinally()
        },

        doIsInstance<HomeIntent.GetArticleDetails> { intent ->
            if (intent.articleUuid.isBlank()) {
                flow {
                    emit(uiStateFlow.value.copy(articleDetailUiState = ArticleDetailUiState.Init()))
                }.defaultFinally()
            } else {
                homeRepo.requestArticleWithTagsDetail(intent.articleUuid)
                    .mapToUIChange { data ->
                        CurrentArticleUuidPreference.put(
                            context = appContext,
                            scope = viewModelScope,
                            value = data.article.uuid
                        )
                        copy(articleDetailUiState = ArticleDetailUiState.Success(data))
                    }
                    .defaultFinally()
            }
        },

        doIsInstance<HomeIntent.DeleteArticleWithTags> { intent ->
            homeRepo.requestDeleteArticleWithTagsDetail(intent.articleUuid)
                .mapToUIChange {
                    CurrentArticleUuidPreference.put(
                        context = appContext,
                        scope = viewModelScope,
                        value = CurrentArticleUuidPreference.default
                    )
                    copy(articleDetailUiState = ArticleDetailUiState.Init())
                }
                .defaultFinally()
        },
    )
}