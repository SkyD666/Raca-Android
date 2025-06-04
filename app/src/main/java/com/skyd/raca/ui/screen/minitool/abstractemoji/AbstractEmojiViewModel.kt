package com.skyd.raca.ui.screen.minitool.abstractemoji

import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUIChange
import com.skyd.raca.base.IUiEvent
import com.skyd.raca.model.respository.AbstractEmojiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

class AbstractEmojiViewModel(private var abstractEmojiRepo: AbstractEmojiRepository) :
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
    )
}