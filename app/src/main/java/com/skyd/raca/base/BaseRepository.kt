package com.skyd.raca.base

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

open class BaseRepository {
    suspend fun <T : Any> executeRequest(block: suspend () -> BaseData<T>): Flow<BaseData<T>> =
        flow {
            val baseData = block.invoke()
            if (baseData.code == 0) {
                baseData.state = ReqState.Success
            } else {
                baseData.state = ReqState.Error
            }
            emit(baseData)
        }
}