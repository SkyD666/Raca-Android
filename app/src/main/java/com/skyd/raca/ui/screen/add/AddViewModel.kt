package com.skyd.raca.ui.screen.add

import androidx.lifecycle.viewModelScope
import com.skyd.raca.appContext
import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUIChange
import com.skyd.raca.model.preference.CurrentArticleUuidPreference
import com.skyd.raca.model.respository.AddRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

class AddViewModel(private var addRepository: AddRepository) :
    BaseViewModel<AddState, AddEvent, AddIntent>() {
    override fun initUiState(): AddState {
        return AddState(
            GetArticleWithTagsUiState.Init
        )
    }

    override fun IUIChange.checkStateOrEvent() = this as? AddState to this as? AddEvent

    override fun Flow<AddIntent>.handleIntent(): Flow<IUIChange> = merge(
        doIsInstance<AddIntent.GetArticleWithTags> { intent ->
            addRepository.requestGetArticleWithTags(intent.articleUuid)
                .mapToUIChange { data ->
                    copy(getArticleWithTagsUiState = GetArticleWithTagsUiState.Success(data))
                }
                .defaultFinally()
        },

        doIsInstance<AddIntent.AddNewArticleWithTags> { intent ->
            addRepository.requestAddArticleWithTags(intent.articleWithTags)
                .mapToUIChange(onError = { data ->
                    if (data.code == -2) {
                        data.data?.let { sendUiIntent(AddIntent.GetArticleWithTags(it)) }
                        AddEvent(addArticleResultUiEvent = AddArticleResultUiEvent.Duplicate)
                    } else {
                        error(data.msg.toString())
                    }
                }) { data ->
                    CurrentArticleUuidPreference.put(
                        context = appContext,
                        scope = viewModelScope,
                        value = data
                    )
                    AddEvent(addArticleResultUiEvent = AddArticleResultUiEvent.Success(data))
                }
                .defaultFinally()
        }
    )
}