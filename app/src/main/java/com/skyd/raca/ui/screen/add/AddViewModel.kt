package com.skyd.raca.ui.screen.add

import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUiEvent
import com.skyd.raca.base.IUiIntent
import com.skyd.raca.model.respository.AddRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(private var addRepository: AddRepository) :
    BaseViewModel<AddState, IUiEvent, AddIntent>() {
    override fun initUiState(): AddState {
        return AddState(
            AddArticleResultUiState.INIT,
            GetArticleWithTagsUiState.INIT
        )
    }

    override fun handleIntent(intent: IUiIntent) {
        when (intent) {
            is AddIntent.AddNewArticleWithTags -> {
                requestDataWithFlow(showLoading = false,
                    request = { addRepository.requestAddArticleWithTags(intent.articleWithTags) },
                    successCallback = {
                        sendUiState {
                            copy(
                                addArticleResultUiState = AddArticleResultUiState.SUCCESS(it),
                                getArticleWithTagsUiState = GetArticleWithTagsUiState.INIT
                            )
                        }
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