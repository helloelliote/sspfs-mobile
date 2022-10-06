/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class POIList(
    @field:Json(name = "rowCount") val poiCount: Int,
    @field:Json(name = "rows") val pois: MutableSet<POI>,
)

@JsonClass(generateAdapter = true)
data class POI(
    val fac_typ: String,
    val typ_nam: String,
    val geom: FeatureGeom
)
