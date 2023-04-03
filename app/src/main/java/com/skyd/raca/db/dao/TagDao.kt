package com.skyd.raca.db.dao

import androidx.room.*
import com.skyd.raca.model.bean.TAG_TABLE_NAME
import com.skyd.raca.model.bean.TagBean
import com.skyd.raca.model.bean.TagBean.Companion.ARTICLE_UUID_COLUMN

@Dao
interface TagDao {
    @Transaction
    @Query("SELECT * FROM $TAG_TABLE_NAME")
    fun getTagList(): List<TagBean>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTags(tags: List<TagBean>)

    @Transaction
    @Query(value = "DELETE FROM $TAG_TABLE_NAME WHERE $ARTICLE_UUID_COLUMN LIKE :articleUuid")
    fun deleteTags(articleUuid: String): Int

    @Transaction
    @Query("DELETE FROM $TAG_TABLE_NAME")
    fun deleteAllTags()
}