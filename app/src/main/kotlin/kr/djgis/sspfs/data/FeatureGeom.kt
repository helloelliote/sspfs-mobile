package kr.djgis.sspfs.data

import com.naver.maps.geometry.LatLng

data class FeatureGeom(
    val type: String,
    val coordinates: List<Any>,
) {
    val latLngs: List<List<LatLng>> = when (type) {
        "Point" -> {
            val list = mutableListOf(mutableListOf<LatLng>())
            val point = LatLng(coordinates[1] as Double, coordinates[0] as Double)
            list[0].add(point)
            list
        }

        "LineString" -> {
            val list = mutableListOf(mutableListOf<LatLng>())
            @Suppress("UNCHECKED_CAST") val points = coordinates as List<List<Double>>
            points.forEach {
                val point = LatLng(it[1], it[0])
                list[0].add(point)
            }
            list
        }

        "MultiLineString" -> {
            val list = mutableListOf(mutableListOf<LatLng>())
            @Suppress("UNCHECKED_CAST") val lines = coordinates as List<List<List<Double>>>
            lines.forEach { line ->
                val points = mutableListOf<LatLng>()
                line.forEach {
                    val point = LatLng(it[1], it[0])
                    points.add(point)
                }
                list.add(points)
            }
            list.filter { it.size > 1 }
        }

        "MultiPolygon" -> {
            val list = mutableListOf(mutableListOf<LatLng>())
            @Suppress("UNCHECKED_CAST") val polygons = coordinates as List<List<List<List<Double>>>>
            polygons[0].forEach { polygon ->
                val points = mutableListOf<LatLng>()
                polygon.forEach {
                    val point = LatLng(it[1], it[0])
                    points.add(point)
                }
                list.add(points)
            }
            list.filter { it.size > 1 }
        }

        else -> listOf()
    }
}
