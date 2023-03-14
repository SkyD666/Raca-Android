package com.skyd.raca.ui.screen.add

import com.skyd.raca.appContext
import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUiIntent
import com.skyd.raca.model.preference.CurrentArticleUuidPreference
import com.skyd.raca.model.respository.AddRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(private var addRepository: AddRepository) :
    BaseViewModel<AddState, AddEvent, AddIntent>() {
    override fun initUiState(): AddState {
        return AddState(
            GetArticleWithTagsUiState.INIT
        )
    }

    override fun handleIntent(intent: IUiIntent) {
        when (intent) {
            is AddIntent.AddNewArticleWithTags -> {
                requestDataWithFlow(showLoading = false,
                    request = { addRepository.requestAddArticleWithTags(intent.articleWithTags) },
                    successCallback = {
                        CurrentArticleUuidPreference.put(
                            context = appContext,
                            scope = this,
                            value = it
                        )
                        sendUiEvent(
                            AddEvent(addArticleResultUiEvent = AddArticleResultUiEvent.SUCCESS(it))
                        )
                    }
                )
            }
            is AddIntent.GetArticleWithTags -> {
                requestDataWithFlow(showLoading = false,
                    request = { addRepository.requestGetArticleWithTags(intent.articleUuid) },
                    successCallback = {
                        sendUiState {
                            copy(getArticleWithTagsUiState = GetArticleWithTagsUiState.SUCCESS(it))
                        }
                    }
                )
            }
        }
    }
}