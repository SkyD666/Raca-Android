package com.skyd.raca.model.respository

import com.skyd.raca.appContext
import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.dao.ArticleDao
import com.skyd.raca.model.bean.*
import com.skyd.raca.util.md5
import com.thegrizzlylabs.sardineandroid.Sardine
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import javax.inject.Inject


class WebDavRepository @Inject constructor(private val articleDao: ArticleDao) : BaseRepository() {
    companion object {
        const val APP_DIR = "Raca/"
        const val BACKUP_DIR = "Backup/"
        const val BACKUP_INFO_FILE = "BackupInfo"
    }

    suspend fun requestRemoteRecycleBin(
        website: String,
        username: String,
        password: String
    ): Flow<BaseData<List<BackupInfo>>> {
        return flow {
            val sardine: Sardine = initWebDav(website, username, password)
            val backupInfoMap: List<BackupInfo> = getMd5UuidKeyBackupInfoMap(sardine, website)
                .filter { it.value.isDeleted }.values.toList()
            emitBaseData(BaseData<List<BackupInfo>>().apply {
                code = 0
                data = backupInfoMap
            })
        }
    }

    suspend fun requestRestoreFromRemoteRecycleBin(
        website: String,
        username: String,
        password: String,
        uuid: String
    ): Flow<BaseData<Unit>> {
        return flow {
            val sardine: Sardine = initWebDav(website, username, password)
            val backupInfoMap = getMd5UuidKeyBackupInfoMap(sardine, website).values
                .associateBy { it.uuid }.toMutableMap()
            backupInfoMap[uuid]?.let {
                backupInfoMap[uuid] = it.copy(isDeleted = false)
            }
            updateBackupInfo(sardine, website, backupInfoMap.values.toList())
            emitBaseData(BaseData<Unit>().apply {
                code = 0
                data = Unit
            })
        }
    }

    suspend fun requestDeleteFromRemoteRecycleBin(
        website: String,
        username: String,
        password: String,
        uuid: String
    ): Flow<BaseData<Unit>> {
        return flow {
            val sardine: Sardine = initWebDav(website, username, password)
            val backupInfoMap = getMd5UuidKeyBackupInfoMap(sardine, website).values
                .associateBy { it.uuid }.toMutableMap()
            backupInfoMap.remove(uuid)
            updateBackupInfo(sardine, website, backupInfoMap.values.toList())
            sardine.delete(website + APP_DIR + BACKUP_DIR + uuid)
            emitBaseData(BaseData<Unit>().apply {
                code = 0
                data = Unit
            })
        }
    }

    suspend fun requestClearRemoteRecycleBin(
        website: String,
        username: String,
        password: String,
    ): Flow<BaseData<Unit>> {
        return flow {
            val sardine: Sardine = initWebDav(website, username, password)
            val (willBeDeletedMap, othersMap) = getMd5UuidKeyBackupInfoMap(sardine, website).run {
                filter { it.value.isDeleted } to filter { !it.value.isDeleted }
            }
            updateBackupInfo(sardine, website, othersMap.values.toList())
            willBeDeletedMap.forEach { (_, u) ->
                sardine.delete(website + APP_DIR + BACKUP_DIR + u.uuid)
            }
            emitBaseData(BaseData<Unit>().apply {
                code = 0
                data = Unit
            })
        }
    }

    suspend fun requestDownload(
        website: String,
        username: String,
        password: String
    ): Flow<BaseData<WebDavInfo>> {
        return flow {
            val startTime = System.currentTimeMillis()
            val allArticleWithTagsList = articleDao.getAllArticleWithTagsList()
            val sardine: Sardine = initWebDav(website, username, password)
            val backupInfoMap: MutableMap<String, BackupInfo> =
                getMd5UuidKeyBackupInfoMap(sardine, website).toMutableMap()
            val waitToAddList = mutableListOf<ArticleWithTags>()
            val (excludedMap, willBeDeletedList) =
                excludeRemoteUnchanged(backupInfoMap, allArticleWithTagsList)
            val totalCount = excludedMap.size + willBeDeletedList.size
            var currentCount = 0
            willBeDeletedList.forEach {
                articleDao.deleteArticleWithTags(articleUuid = it)
                emitProgressData(current = ++currentCount, total = totalCount)
            }
            excludedMap.forEach { entry ->
                sardine.get(website + APP_DIR + BACKUP_DIR + entry.value.uuid).use { inputStream ->
                    waitToAddList += Json.decodeFromStream<ArticleWithTags>(inputStream)
                }
                emitProgressData(current = ++currentCount, total = totalCount)
            }
            articleDao.webDavImportData(waitToAddList)
            emitBaseData(BaseData<WebDavInfo>().apply {
                code = 0
                data = WebDavResultInfo(
                    time = System.currentTimeMillis() - startTime,
                    count = totalCount
                )
            })
        }
    }

    suspend fun requestUpload(
        website: String,
        username: String,
        password: String
    ): Flow<BaseData<WebDavInfo>> {
        return flow {
            val startTime = System.currentTimeMillis()
            val allArticleWithTagsList = articleDao.getAllArticleWithTagsList()
            val sardine: Sardine = initWebDav(website, username, password)
            var backupInfoMap: MutableMap<String, BackupInfo> =
                getMd5UuidKeyBackupInfoMap(sardine, website).toMutableMap()
            val (excludedList, willBeDeletedMap) = excludeLocalUnchanged(
                backupInfoMap,      // 这里需要md5+uuid map
                allArticleWithTagsList
            )
            backupInfoMap = backupInfoMap.values.associateBy { it.uuid }.toMutableMap()
            val totalCount = excludedList.size + willBeDeletedMap.size
            var currentCount = 0
            willBeDeletedMap.forEach { (_, u) ->
                backupInfoMap[/*u.contentMd5 + */u.uuid]?.isDeleted = true
                emitProgressData(current = ++currentCount, total = totalCount)
            }
            excludedList.forEach {
                val file = toFile(it)
                sardine.put(website + APP_DIR + BACKUP_DIR + file.name, file, "text/*")
                file.deleteRecursively()
                val md5 = it.md5()
                val uuid = it.article.uuid
                backupInfoMap[uuid] = BackupInfo(
                    uuid = uuid,
                    contentMd5 = md5,
                    modifiedTime = System.currentTimeMillis(),
                    isDeleted = false
                )
                emitProgressData(current = ++currentCount, total = totalCount)
            }
            updateBackupInfo(sardine, website, backupInfoMap.values.toList())
            emitBaseData(BaseData<WebDavInfo>().apply {
                code = 0
                data = WebDavResultInfo(
                    time = System.currentTimeMillis() - startTime,
                    count = excludedList.size + willBeDeletedMap.size
                )
            })
        }
    }

    private fun excludeRemoteUnchanged(
        backupInfoMap: Map<String, BackupInfo>,
        allArticleWithTagsList: List<ArticleWithTags>
    ): Pair<Map<String, BackupInfo>, List<String>> {
        val md5UuidKeyMap = backupInfoMap.toMutableMap()
        val willBeDeletedList = mutableListOf<String>()
        allArticleWithTagsList.forEach {
            val md5 = it.md5()
            val uuid = it.article.uuid
            val backupInfo = backupInfoMap[md5 + uuid]
            if (backupInfo != null) {
                if (backupInfo.isDeleted) {
                    // 在本地但在远端回收站的段落，稍后会在本地被移除
                    willBeDeletedList.add(it.article.uuid)
                }
                md5UuidKeyMap.remove(md5 + uuid)
            }
        }
        // 过滤除掉不在本地但在远端回收站的段落
        return md5UuidKeyMap.filter { !it.value.isDeleted } to willBeDeletedList
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
            sardine.get(website + APP_DIR + BACKUP_INFO_FILE).use { inputStream ->
                Json.decodeFromStream<List<BackupInfo>>(inputStream)
                    .distinctBy { it.uuid }             // 保证uuid唯一
                    .associateBy { it.contentMd5 + it.uuid }
            }
        } else mapOf()
    }

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

    private suspend fun FlowCollector<BaseData<WebDavInfo>>.emitProgressData(
        current: Int,
        total: Int
    ) {
        emitBaseData(BaseData<WebDavInfo>().apply {
            code = 0
            data = WebDavWaitingInfo(current = current, total = total)
        })
    }
}