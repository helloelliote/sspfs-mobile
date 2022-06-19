package kr.djgis.sspfs.data

import com.google.gson.JsonArray
import com.naver.maps.geometry.LatLng
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class FeatureGeom(
    val type: String,
    val coordinates: JsonArray,
) : Serializable {
    val latLngs: List<List<LatLng>> = when (type) {
        "Point" -> {
            val list = mutableListOf(mutableListOf<LatLng>())
            val point = LatLng(coordinates[1].asDouble, coordinates[0].asDouble)
            list[0].add(point)
            list
        }
        "LineString" -> {
            val list = mutableListOf(mutableListOf<LatLng>())
            val points = coordinates.asJsonArray
            points.forEach {
                val point = LatLng(it.asJsonArray[1].asDouble, it.asJsonArray[0].asDouble)
                list[0].add(point)
            }
            list
        }
        "MultiLineString" -> {
            val list = mutableListOf(mutableListOf<LatLng>())
            val lines = coordinates.asJsonArray
            lines.forEach { line ->
                val points = mutableListOf<LatLng>()
                line.asJsonArray.forEach {
                    val point = LatLng(it.asJsonArray[1].asDouble, it.asJsonArray[0].asDouble)
                    points.add(point)
                }
                list.add(points)
            }
            list.filter { it.size > 1 }
        }
        else -> listOf()
    }
}
