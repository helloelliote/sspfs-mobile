/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeatureAttachment(
    var url: String?,
    var name: String?,
    val type: String?,
    @field:Json(ignore = true)
    var uri: Uri? = null,
)
