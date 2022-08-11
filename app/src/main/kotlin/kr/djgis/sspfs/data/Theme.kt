/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ThemeList(
    @Json(name = "rowCount") val themeCount: Int,
    @Json(name = "rows") val themes: MutableSet<Theme>,
)

@JsonClass(generateAdapter = true)
data class Theme(
    val geom: FeatureGeom,
) : Serializable
