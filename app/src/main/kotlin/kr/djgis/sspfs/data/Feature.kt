/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import androidx.recyclerview.widget.DiffUtil
import com.google.gson.Gson
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import kr.djgis.sspfs.Config.DATETIME_FORMAT
import kr.djgis.sspfs.Config.DATETIME_FORMAT_RECEIVE
import kr.djgis.sspfs.Config.DATETIME_ZONE
import java.io.Serializable
import java.time.LocalDateTime.parse

@JsonClass(generateAdapter = true)
data class FeatureList(
    @Json(name = "rowCount") val featureCount: Int,
    @Json(name = "rows") val features: MutableSet<Feature>,
) : Serializable

@JsonClass(generateAdapter = true)
data class Feature(
    var id: String,
    var pk0: String?,
    var pk4: String,
    var a1: String?,
    var a2: String?,
    var a3: String?,
    var a4: String?,
    var a5: String?,
    var a6: String?,
    var b1: String?,
    var b2: String?,
    var b3: String?,
    var c1: String?,
    var c2: String?,
    var d1: String?,
    var d2: String?,
    var d3: String?,
    var e1: String?,
    var e2: String?,
    var e3: String?,
    var f1: String?,
    var f2: String?,
    var g1: String?,
    var image: List<FeatureAttachment>?,
    var geom: FeatureGeom,
) : Serializable {
    val dateA6: String? = a6?.let(::parseDate)
    val dateB2: String? = b2?.let(::parseDate)
    val dateB3: String? = b3?.let(::parseDate)

    private fun parseDate(string: String): String? {
        return parse(string, DATETIME_FORMAT_RECEIVE).atZone(DATETIME_ZONE).format(DATETIME_FORMAT)
    }
}

@JsonClass(generateAdapter = true)
data class FeatureJson(
    var id: String,
    var pk0: String?,
    var pk4: String,
    var a1: String?,
    var a2: String?,
    var a3: String?,
    var a4: String?,
    var a5: String?,
    var a6: String?,
    var b1: String?,
    var b2: String?,
    var b3: String?,
    var c1: Int?,
    var c2: Double?,
    var d1: String?,
    var d2: String?,
    var d3: String?,
    var e1: Double?,
    var e2: Double?,
    var e3: Int?,
    var f1: String?,
    var f2: String?,
    var g1: String?,
    var image: Any?,
    var geom: Any,
) : Serializable

class FeatureJsonAdapter {
    @FromJson
    fun fromJson(json: FeatureJson): Feature {
        return Feature(
            id = json.id,
            pk0 = json.pk0,
            pk4 = json.pk4,
            a1 = json.a1,
            a2 = json.a2,
            a3 = json.a3,
            a4 = json.a4,
            a5 = json.a5,
            a6 = json.a6,
            b1 = json.b1,
            b2 = json.b2,
            b3 = json.b3,
            c1 = json.c1?.toString(),
            c2 = json.c2?.toString(),
            d1 = json.d1,
            d2 = json.d2,
            d3 = json.d3,
            e1 = json.e1?.toString(),
            e2 = json.e2?.toString(),
            e3 = json.e3?.toString(),
            f1 = json.f1,
            f2 = json.f2,
            g1 = json.g1,
            image = image(json),
            geom = geom(json),
        )
    }

    private fun image(json: FeatureJson): List<FeatureAttachment> {
        val list = mutableListOf<FeatureAttachment>()
        val jsonArray = Gson().toJsonTree(json.image).asJsonArray
        jsonArray.forEach {
            val item = it.asJsonObject
            list.add(FeatureAttachment(
                url = item["url"]?.asString,
                type = item["type"].asString,
            ))
        }
        return list
    }

    private fun geom(json: FeatureJson): FeatureGeom {
        val jsonObject = Gson().toJsonTree(json.geom).asJsonObject
        return FeatureGeom(
            type = jsonObject["type"].asString,
            coordinates = jsonObject["coordinates"].asJsonArray,
        )
    }

    @ToJson
    fun toJson(feature: Feature): FeatureJson {
        return FeatureJson(
            id = feature.id,
            pk0 = feature.pk0,
            pk4 = feature.pk4,
            a1 = feature.a1,
            a2 = feature.a2,
            a3 = feature.a3,
            a4 = feature.a4,
            a5 = feature.a5,
            a6 = feature.a6,
            b1 = feature.b1,
            b2 = feature.b2,
            b3 = feature.b3,
            c1 = feature.c1?.toInt(),
            c2 = feature.c2?.toDouble(),
            d1 = feature.d1?.trim(),
            d2 = feature.d2?.trim(),
            d3 = feature.d3?.trim(),
            e1 = feature.e1?.toDouble(),
            e2 = feature.e2?.toDouble(),
            e3 = feature.e3?.toInt(),
            f1 = feature.f1,
            f2 = feature.f2,
            g1 = feature.g1?.trim(),
            image = Gson().toJson(feature.image),
            geom = Gson().toJson(feature.geom),
        )
    }
}

object FeatureDiffCallback : DiffUtil.ItemCallback<Feature>() {
    override fun areItemsTheSame(oldItem: Feature, newItem: Feature) = oldItem.id === newItem.id
    override fun areContentsTheSame(oldItem: Feature, newItem: Feature) = oldItem == newItem
}
