/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.util

import kr.djgis.sspfs.Config.DATETIME_FORMAT
import kr.djgis.sspfs.Config.DATETIME_FORMAT_RECEIVE
import kr.djgis.sspfs.Config.DATETIME_ZONE
import kr.djgis.sspfs.data.Feature
import java.time.LocalDateTime.parse

fun Feature.parseDate(): String? {
    return try {
        "조사 일자\n${parse(this.exm_ymd, DATETIME_FORMAT_RECEIVE).atZone(DATETIME_ZONE).format(DATETIME_FORMAT)}"
    } catch (e: Exception) {
        null
    }
}
