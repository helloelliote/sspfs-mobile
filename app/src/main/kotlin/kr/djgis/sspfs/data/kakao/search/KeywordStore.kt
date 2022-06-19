/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data.kakao.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object KeywordStore {

    private val allDocuments = mutableListOf(DOCUMENT_NONE)
    private val allHistory = mutableListOf<Document>()

    val DOCUMENT: MutableLiveData<MutableList<Document>> = MutableLiveData()
    val HISTORY: MutableLiveData<MutableList<Document>> = MutableLiveData()

    private val moshiDocument: JsonAdapter<Document> by lazy {
        Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build().adapter(Document::class.java)
            .nullSafe()
    }

    init {
        DOCUMENT.value = allDocuments
        HISTORY.value = allHistory
    }

    fun addHistory(document: Document) {
        HISTORY.value?.let {
            when (it.contains(document)) {
                true -> {
                    it.remove(document)
                    it.add(0, document)
                }
                false -> {
                    it.add(0, document)
                }
            }
        }
    }

    fun resetAll(): Int? {
        return DOCUMENT.value?.let {
            val itemCount = it.size
            it.clear()
            it.add(DOCUMENT_NONE)
            return@let itemCount
        }
    }

    fun onDestroy() {
        HISTORY.value?.forEach {
            val string = moshiDocument.toJson(it)
            Log.d("SVI", string)
        }
    }
}
