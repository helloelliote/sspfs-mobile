/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import com.squareup.moshi.Moshi

object MoshiBuilder {

    val moshi: Moshi = Moshi.Builder().build()
}
