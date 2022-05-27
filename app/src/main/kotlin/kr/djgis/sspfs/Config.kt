/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs

import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Config {

    val LATLNG_GYEONGJU: LatLng = LatLng(35.85618, 129.222614)
    val EXTENT_GYEONGJU: LatLngBounds = LatLngBounds(LatLng(35.62, 128.95), LatLng(36.08, 129.56))

    const val ANIMATION_FAST_MILLIS = 50L
    const val ANIMATION_SLOW_MILLIS = 100L

    val DATETIME_ZONE: ZoneId = ZoneId.of("GMT+9")
    val DATETIME_FORMAT_RECEIVE: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz")
    val DATETIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh:mm")
}
