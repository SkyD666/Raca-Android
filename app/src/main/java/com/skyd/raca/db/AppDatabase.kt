package com.skyd.raca.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.skyd.raca.appContext
import com.skyd.raca.db.dao.ArticleDao
import com.skyd.raca.db.dao.TagDao
import com.skyd.raca.model.bean.ArticleBean
import com.skyd.raca.model.bean.TagBean

const val APP_DATA_BASE_FILE_NAME = "app.db"

@Database(
    entities = [ArticleBean::class, TagBean::class], version = 1
)
@TypeConverters(
    value = []
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao
    abstract fun tagDao(): TagDao

    companion object {
        private var instance: AppDatabase? = null

        private val migrations = arrayOf<Migration>()

        fun getInstance(context: Context): AppDatabase {
            return if (instance == null) {
                synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        APP_DATA_BASE_FILE_NAME
                    )
                        .addMigrations(*migrations)
                        .build()
                }
            } else {
                instance as AppDatabase
            }

        }
    }
}

val appDataBase by lazy { AppDatabase.getInstance(appContext) }