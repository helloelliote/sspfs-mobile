/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class DistrictList(
    @Json(name = "rowCount") val regionCount: Int,
    @Json(name = "rows") val districts: MutableSet<District>,
)

@JsonClass(generateAdapter = true)
data class District(
    val hjd_nam: String,
    val bjd_nam: String,
    val geom: FeatureGeom,
    val center: FeatureGeom,
) : Serializable
