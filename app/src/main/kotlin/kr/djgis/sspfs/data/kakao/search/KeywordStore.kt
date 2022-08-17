/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data.kakao.search

import androidx.lifecycle.MutableLiveData

object KeywordStore {

    private val allDocuments = mutableListOf(DOCUMENT_NONE)
    private val allHistory = mutableListOf<Document>()

    val DOCUMENT: MutableLiveData<MutableList<Document>> = MutableLiveData()
    val HISTORY: MutableLiveData<MutableList<Document>> = MutableLiveData()

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
}
