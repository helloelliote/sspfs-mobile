/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DistrictList(
    @field:Json(name = "rowCount") val regionCount: Int,
    @field:Json(name = "rows") val districts: MutableSet<District>,
)

@JsonClass(generateAdapter = true)
data class District(
    val hjd_nam: String,
    val bjd_nam: String,
    val geom: FeatureGeom,
    val center: FeatureGeom,
)
