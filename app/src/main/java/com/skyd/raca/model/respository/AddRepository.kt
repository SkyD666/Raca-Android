package com.skyd.raca.model.respository

import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.appDataBase
import com.skyd.raca.model.bean.ArticleWithTags
import javax.inject.Inject

class AddRepository @Inject constructor() : BaseRepository() {
    suspend fun requestAddArticleWithTags(articleWithTags: ArticleWithTags): BaseData<Long> {
        return executeRequest {
            BaseData<Long>().apply {
                code = 0
                data = appDataBase.articleDao().addArticleWithTags(articleWithTags)
            }
        }
    }

    suspend fun requestGetArticleWithTags(articleId: Long): BaseData<ArticleWithTags> {
        return executeRequest {
            BaseData<ArticleWithTags>().apply {
                code = 0
                data = appDataBase.articleDao().getArticleWithTags(articleId)
            }
        }
    }
}