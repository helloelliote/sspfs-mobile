/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Result(
    val rowCount: String,
    val rows: List<Any>,
)
