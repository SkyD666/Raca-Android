package com.skyd.raca.model.respository

import androidx.core.text.isDigitsOnly
import com.skyd.raca.appContext
import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.ext.startWithBlank
import com.skyd.raca.ext.toPinyin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream


class AbstractEmojiRepository : BaseRepository() {
    private lateinit var abstractEmojiDict: MutableMap<String, String>
    private lateinit var abstractEmojiPinyinDict: MutableMap<String, String>

    fun requestConvert(article: String): Flow<BaseData<String>> = flow {
        checkDict()
        emitBaseData(BaseData<String>().apply {
            code = 0
            data = getResultString(article)
        })
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun checkDict() {
        if (!this@AbstractEmojiRepository::abstractEmojiDict.isInitialized) {
            abstractEmojiDict = Json.decodeFromStream<HashMap<String, String>>(
                appContext.assets.open("abstract_emoji_dict.json")
            ).toSortedMap(reverseOrder())
        }
        if (!this@AbstractEmojiRepository::abstractEmojiPinyinDict.isInitialized) {
            val map = mutableMapOf<String, String>()
            abstractEmojiDict.forEach { (t, u) ->
                if (t.isDigitsOnly()) {
                    map[t] = u
                } else {
                    map[t.toPinyin()] = u
                }
            }
            abstractEmojiPinyinDict = map
        }
    }

    private fun getResultString(article: String): String {
        val stringBuilder = StringBuilder()
        var theRestText = StringBuilder(article)
        val abstractEmojiDictKeys = abstractEmojiDict.keys
        val abstractEmojiPinyinDictKeys = abstractEmojiPinyinDict.keys

        while (theRestText.isNotEmpty()) {
            // 空格这些字符直接保留原样
            if (theRestText.startWithBlank()) {
                stringBuilder.append(theRestText[0])
                theRestText = theRestText.deleteAt(0)//
            } else {
                var prefix = ""
                run prefixForEach@{
                    abstractEmojiDictKeys.forEach {
                        if (theRestText.startsWith(it)) {
                            prefix = it
                            return@prefixForEach
                        }
                    }
                }
                if (prefix.isNotEmpty()) {
                    theRestText = theRestText.deleteRange(
                        startIndex = 0,
                        endIndex = prefix.length
                    ) //.substringAfter(prefix)
                    stringBuilder.append(abstractEmojiDict[prefix])
                } else {
                    val theRestTextPinyin = theRestText.toString().toPinyin()
                    var pinyinPrefix = ""
                    run prefixForEach@{
                        abstractEmojiPinyinDictKeys.forEach {
                            if (theRestTextPinyin.startsWith(it) &&
                                theRestTextPinyin.substringAfter(it)
                                    .run { isEmpty() || startWithBlank() }
                            ) {
                                pinyinPrefix = it
                                return@prefixForEach
                            }
                        }
                    }
                    if (pinyinPrefix.isNotEmpty()) {
                        val wordCount = pinyinPrefix.count { it == ' ' } + 1
                        theRestText = theRestText.deleteRange(
                            startIndex = 0,
                            endIndex = wordCount
                        )//.substring(wordCount)
                        stringBuilder.append(abstractEmojiPinyinDict[pinyinPrefix])
                    } else {
                        stringBuilder.append(theRestText[0])
                        theRestText = theRestText.deleteAt(0)//.substring(1)
                    }
                }
            }
        }
        return stringBuilder.toString()
    }
}