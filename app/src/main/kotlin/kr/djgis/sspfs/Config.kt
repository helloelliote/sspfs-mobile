/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs

import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import java.time.format.DateTimeFormatter

object Config {

    const val BASE_URL = "http://djgis.iptime.org:5605/"

    val LATLNG_GYEONGJU: LatLng = LatLng(35.85618, 129.222614)
    val EXTENT_GYEONGJU: LatLngBounds = LatLngBounds(LatLng(35.62, 128.95), LatLng(36.08, 129.56))

    const val ANIMATION_FAST_MILLIS = 50L
    const val ANIMATION_SLOW_MILLIS = 100L

    val DATETIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm")

    const val EXM_CHK_SAVE = "1"
    const val EXM_CHK_EXCLUDE = "2"
}
