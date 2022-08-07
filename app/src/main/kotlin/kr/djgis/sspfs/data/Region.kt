/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class RegionList(
    @Json(name = "rowCount") val regionCount: Int,
    @Json(name = "rows") val regions: MutableSet<Region>,
)

@JsonClass(generateAdapter = true)
data class Region(
    val hjd_nam: String,
    val bjd_nam: String,
    val geom: FeatureGeom,
    val center: FeatureGeom,
) : Serializable
