/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import android.graphics.Color.*
import androidx.annotation.ColorInt

enum class FeatureType(
    val type: String,
    @ColorInt val color: Int,
) {
    A("소교량", RED) {
        override fun next() = B
    },
    B("세천", BLUE) {
        override fun next() = C
    },
    C("취입보", YELLOW) {
        override fun next() = D
    },
    D("낙차공", YELLOW) {
        override fun next() = E
    },
    E("농로", GREEN) {
        override fun next() = D
    },
    F("마을진입로", CYAN) {
        override fun next() = A
    },
    ;

    abstract fun next(): FeatureType

    companion object {
        fun toColor(type: String) = values().find { it.type == type.trim() }!!.color

        fun toColor(feature: Feature) = values().find { it.name == feature.fac_typ.trim() }!!.color

        fun keys(): List<String> = values().map { it.name }
        fun types(): List<String> = values().map { it.type }
    }
}
