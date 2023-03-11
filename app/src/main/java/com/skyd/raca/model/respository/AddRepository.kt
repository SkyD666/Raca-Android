package com.skyd.raca.model.respository

import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.appDataBase
import com.skyd.raca.model.bean.ArticleWithTags
import javax.inject.Inject

class AddRepository @Inject constructor() : BaseRepository() {
    suspend fun requestAddArticleWithTags(articleWithTags: ArticleWithTags): BaseData<String> {
        return executeRequest {
            BaseData<String>().apply {
                code = 0
                data = appDataBase.articleDao().addArticleWithTags(articleWithTags)
            }
        }
    }

    suspend fun requestGetArticleWithTags(articleUuid: String): BaseData<ArticleWithTags> {
        return executeRequest {
            BaseData<ArticleWithTags>().apply {
                code = 0
                data = appDataBase.articleDao().getArticleWithTags(articleUuid)
            }
        }
    }
}