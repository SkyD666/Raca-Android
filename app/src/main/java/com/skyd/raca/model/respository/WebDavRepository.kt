package com.skyd.raca.model.respository

import com.skyd.raca.appContext
import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.appDataBase
import com.skyd.raca.model.bean.ArticleWithTags
import com.skyd.raca.model.bean.BackupInfo
import com.skyd.raca.model.bean.WebDavResultInfo
import com.skyd.raca.util.md5
import com.thegrizzlylabs.sardineandroid.Sardine
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject


class WebDavRepository @Inject constructor() : BaseRepository() {
    companion object {
        const val APP_DIR = "Raca/"
        const val BACKUP_DIR = "Backup/"
        const val BACKUP_INFO_FILE = "BackupInfo"
    }

    suspend fun requestRemoteRecycleBin(
        website: String,
        username: String,
        password: String
    ): BaseData<List<BackupInfo>> {
        return executeRequest {
            val sardine: Sardine = initWebDav(website, username, password)
            val backupInfoMap: List<BackupInfo> = getMd5UuidKeyBackupInfoMap(sardine, website)
                .filter { it.value.isDeleted }.values.toList()
            BaseData<List<BackupInfo>>().apply {
                code = 0
                data = backupInfoMap
            }
        }
    }

    suspend fun requestDeleteFromRemoteRecycleBin(
        website: String,
        username: String,
        password: String,
        uuid: String
    ): BaseData<Unit> {
        return executeRequest {
            val sardine: Sardine = initWebDav(website, username, password)
            val backupInfoMap = getMd5UuidKeyBackupInfoMap(sardine, website).values
                .associateBy { it.uuid }.toMutableMap()
            backupInfoMap.remove(uuid)
            updateBackupInfo(sardine, website, backupInfoMap.values.toList())
            sardine.delete(website + APP_DIR + BACKUP_DIR + uuid)
            BaseData<Unit>().apply {
                code = 0
                data = Unit
            }
        }
    }

    suspend fun requestClearRemoteRecycleBin(
        website: String,
        username: String,
        password: String,
    ): BaseData<Unit> {
        return executeRequest {
            val sardine: Sardine = initWebDav(website, username, password)
            val (willBeDeletedMap, othersMap) = getMd5UuidKeyBackupInfoMap(sardine, website).run {
                filter { it.value.isDeleted } to filter { !it.value.isDeleted }
            }
            updateBackupInfo(sardine, website, othersMap.values.toList())
            willBeDeletedMap.forEach { (_, u) ->
                sardine.delete(website + APP_DIR + BACKUP_DIR + u.uuid)
            }
            BaseData<Unit>().apply {
                code = 0
                data = Unit
            }
        }
    }

    suspend fun requestDownload(
        website: String,
        username: String,
        password: String
    ): BaseData<WebDavResultInfo> {
        return executeRequest {
            val startTime = System.currentTimeMillis()
            val allArticleWithTagsList = appDataBase.articleDao().getAllArticleWithTagsList()
            val sardine: Sardine = initWebDav(website, username, password)
            val backupInfoMap: MutableMap<String, BackupInfo> =
                getMd5UuidKeyBackupInfoMap(sardine, website).toMutableMap()
            val waitToAddList = mutableListOf<ArticleWithTags>()
            val excludedMap = excludeRemoteUnchanged(backupInfoMap, allArticleWithTagsList)
            excludedMap.forEach { entry ->
                val inputAsString = sardine.get(website + APP_DIR + BACKUP_DIR + entry.value.uuid)
                    .bufferedReader().use { it.readText() }
                waitToAddList += Json.decodeFromString<ArticleWithTags>(inputAsString)
            }
            appDataBase.articleDao().webDavImportData(waitToAddList)
            BaseData<WebDavResultInfo>().apply {
                code = 0
                data = WebDavResultInfo(
                    time = System.currentTimeMillis() - startTime,
                    count = waitToAddList.size
                )
            }
        }
    }

    suspend fun requestUpload(
        website: String,
        username: String,
        password: String
    ): BaseData<WebDavResultInfo> {
        return executeRequest {
            val startTime = System.currentTimeMillis()
            val allArticleWithTagsList = appDataBase.articleDao().getAllArticleWithTagsList()
            val sardine: Sardine = initWebDav(website, username, password)
            val backupInfoMap: MutableMap<String, BackupInfo> =
                getMd5UuidKeyBackupInfoMap(sardine, website).toMutableMap()
            val (excludedList, willBeDeletedMap) = excludeLocalUnchanged(
                backupInfoMap,
                allArticleWithTagsList
            )
            willBeDeletedMap.forEach { (_, u) ->
                backupInfoMap[u.contentMd5 + u.uuid]?.isDeleted = true
            }
            excludedList.forEach {
                val file = toFile(it)
                sardine.put(website + APP_DIR + BACKUP_DIR + file.name, file, "text/*")
                file.deleteRecursively()
                val md5 = it.md5()
                val uuid = it.article.uuid
                backupInfoMap[md5 + uuid] = BackupInfo(
                    uuid = uuid,
                    contentMd5 = md5,
                    modifiedTime = System.currentTimeMillis(),
                    isDeleted = false
                )
            }
            updateBackupInfo(sardine, website, backupInfoMap)
            BaseData<WebDavResultInfo>().apply {
                code = 0
                data = WebDavResultInfo(
                    time = System.currentTimeMillis() - startTime,
                    count = excludedList.size + willBeDeletedMap.size
                )
            }
        }
    }

    private fun excludeRemoteUnchanged(
        backupInfoMap: Map<String, BackupInfo>,
        allArticleWithTagsList: List<ArticleWithTags>
    ): Map<String, BackupInfo> {
        val md5UuidKeyMap = backupInfoMap.toMutableMap()
        val uuidKeyMap = backupInfoMap.values.associateBy { it.uuid }.toMutableMap()
        allArticleWithTagsList.forEach {
            val md5 = it.md5()
            val uuid = it.article.uuid
            val backupInfo = backupInfoMap[md5 + uuid]
            if (backupInfo != null && (backupInfo.isDeleted || backupInfo.uuid == uuid)) {
                md5UuidKeyMap.remove(md5 + uuid)
            }
            uuidKeyMap.remove(uuid)
        }
        uuidKeyMap.forEach { (_, u) ->
            md5UuidKeyMap.remove(u.contentMd5 + u.uuid)
        }
        return md5UuidKeyMap
    }

    private fun excludeLocalUnchanged(
        md5UuidKeyBackupInfoMap: Map<String, BackupInfo>,
        allArticleWithTagsList: List<ArticleWithTags>
    ): Pair<List<ArticleWithTags>, Map<String, BackupInfo>> {
        // logical delete
        val uuidKeyMap = md5UuidKeyBackupInfoMap.values.associateBy { it.uuid }.toMutableMap()
        val mutableList = allArticleWithTagsList.toMutableList()
        var md5: String
        allArticleWithTagsList.forEach {
            md5 = it.md5()
            val uuid = it.article.uuid
            val backupInfo = md5UuidKeyBackupInfoMap[md5 + uuid]
            if (backupInfo != null) {
                if (backupInfo.uuid == uuid && !backupInfo.isDeleted) {
                    mutableList.remove(it)
                }
            }
            uuidKeyMap.remove(uuid)
        }
        return mutableList to uuidKeyMap.filter { !it.value.isDeleted }
    }

    private fun getMd5UuidKeyBackupInfoMap(
        sardine: Sardine,
        website: String,
    ): Map<String, BackupInfo> {
        return if (sardine.exists(website + APP_DIR + BACKUP_INFO_FILE)) {
            val inputAsString = sardine.get(website + APP_DIR + BACKUP_INFO_FILE)
                .bufferedReader().use { it.readText() }
            Json.decodeFromString<List<BackupInfo>>(inputAsString)
                .associateBy { it.contentMd5 + it.uuid }
        } else mapOf()
    }

    private fun updateBackupInfo(
        sardine: Sardine,
        website: String,
        backupInfoMap: Map<String, BackupInfo>
    ) = updateBackupInfo(sardine, website, backupInfoMap.values.toList())

    private fun updateBackupInfo(
        sardine: Sardine,
        website: String,
        backupInfoList: List<BackupInfo>
    ) {
        val file = File(appContext.filesDir, BACKUP_INFO_FILE)
        file.printWriter().use { out ->
            out.println(Json.encodeToString(backupInfoList))
        }
        sardine.put(website + APP_DIR + BACKUP_INFO_FILE, file, "text/*")
    }

    private fun toFile(articleWithTags: ArticleWithTags): File {
        val file = File(appContext.filesDir, articleWithTags.article.uuid)
        file.printWriter().use { out ->
            out.println(Json.encodeToString(articleWithTags))
        }
        return file
    }

    private fun initWebDav(
        website: String, username: String, password: String
    ): Sardine {
        val sardine: Sardine = OkHttpSardine()
        sardine.setCredentials(username, password)
        if (!sardine.exists(website + APP_DIR)) {
            sardine.createDirectory(website + APP_DIR)
        }
        if (!sardine.exists(website + APP_DIR + BACKUP_DIR)) {
            sardine.createDirectory(website + APP_DIR + BACKUP_DIR)
        }
        return sardine
    }
}