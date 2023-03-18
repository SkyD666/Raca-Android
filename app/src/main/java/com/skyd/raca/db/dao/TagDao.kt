package com.skyd.raca.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skyd.raca.model.bean.TAG_TABLE_NAME
import com.skyd.raca.model.bean.TagBean

@Dao
interface TagDao {
    @Query("SELECT * FROM $TAG_TABLE_NAME")
    fun getTagList(): List<TagBean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTags(tags: List<TagBean>)

    @Query(value = "DELETE FROM $TAG_TABLE_NAME WHERE articleUuid LIKE :articleUuid")
    fun deleteTags(articleUuid: String): Int

    @Query("DELETE FROM $TAG_TABLE_NAME")
    fun deleteAllTags()
}