package com.skyd.raca.model.respository

import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.dao.ArticleDao
import com.skyd.raca.model.bean.ArticleWithTags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ImportDataRepository(
    private val articleDao: ArticleDao
) : BaseRepository() {
    fun requestImportData(
        articleWithTagsList: List<ArticleWithTags>?,
    ): Flow<BaseData<Long>> = flow {
        if (articleWithTagsList == null) {
            error("articleWithTagsList is null")
        } else {
            val startTime = System.currentTimeMillis()
            if (!articleDao.importData(articleWithTagsList)) {
                error("importData failed!")
            }
            emitBaseData(BaseData<Long>().apply {
                code = 0
                data = System.currentTimeMillis() - startTime
            })
        }
    }
}