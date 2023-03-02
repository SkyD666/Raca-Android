package com.skyd.raca.model.respository

import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.appDataBase
import com.skyd.raca.model.bean.ArticleWithTags
import javax.inject.Inject

class ImportDataRepository @Inject constructor() : BaseRepository() {
    suspend fun requestImportData(articleWithTagsList: List<ArticleWithTags>?): BaseData<Long> {
        return executeRequest {
            if (articleWithTagsList == null) {
                error("articleWithTagsList is null")
            } else {
                val startTime = System.currentTimeMillis()
                if (!appDataBase.articleDao().importData(articleWithTagsList)) {
                    error("importData failed!")
                }
                BaseData<Long>().apply {
                    code = 0
                    data = System.currentTimeMillis() - startTime
                }
            }
        }
    }
}