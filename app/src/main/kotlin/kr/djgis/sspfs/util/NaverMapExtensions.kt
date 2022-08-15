/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.util

import com.naver.maps.geometry.LatLngBounds

fun LatLngBounds.round(): List<Double> {
    return listOf(
        this.westLongitude,
        this.southLatitude,
        this.eastLongitude,
        this.northLatitude,
    ).map { kotlin.math.round(it * 10e2) / 10e2 }
}
