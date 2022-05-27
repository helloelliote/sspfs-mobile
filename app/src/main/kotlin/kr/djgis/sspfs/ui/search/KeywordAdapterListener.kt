/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.search

import android.view.View
import kr.djgis.sspfs.data.kakao.search.Document

interface KeywordAdapterListener {
    fun onKeywordItemClicked(cardView: View, document: Document)
}
