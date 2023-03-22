package com.skyd.raca.ui.screen.minitool.abstractemoji

import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUIChange
import com.skyd.raca.base.IUiEvent
import com.skyd.raca.model.respository.AbstractEmojiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@HiltViewModel
class AbstractEmojiViewModel @Inject constructor(private var abstractEmojiRepo: AbstractEmojiRepository) :
    BaseViewModel<AbstractEmojiState, IUiEvent, AbstractEmojiIntent>() {
    override fun initUiState(): AbstractEmojiState {
        return AbstractEmojiState(abstractEmojiResultUiState = AbstractEmojiResultUiState.Init)
    }

    override fun IUIChange.checkStateOrEvent() = this as? AbstractEmojiState to this as? IUiEvent

    override fun Flow<AbstractEmojiIntent>.handleIntent(): Flow<IUIChange> = merge(
        doIsInstance<AbstractEmojiIntent.Convert> { intent ->
            abstractEmojiRepo.requestConvert(intent.article)
                .mapToUIChange { data ->
                    copy(abstractEmojiResultUiState = AbstractEmojiResultUiState.Success(data))
                }
                .defaultFinally()
        },

        doIsInstance<AbstractEmojiIntent.Reset> {
            abstractEmojiRepo.requestReset()
                .mapToUIChange {
                    copy(abstractEmojiResultUiState = AbstractEmojiResultUiState.Init)
                }
                .defaultFinally()
        },
    )
}