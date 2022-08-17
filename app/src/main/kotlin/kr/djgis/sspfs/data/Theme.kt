/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ThemeList(
    @field:Json(name = "rowCount") val themeCount: Int,
    @field:Json(name = "rows") val themes: MutableSet<Theme>,
)

@JsonClass(generateAdapter = true)
data class Theme(
    val geom: FeatureGeom,
)
