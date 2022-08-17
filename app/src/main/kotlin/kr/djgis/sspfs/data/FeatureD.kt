/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeatureDList(
    @field:Json(name = "rowCount") val FeatureCount: Int,
    @field:Json(name = "rows") val features: MutableSet<FeatureD>,
)

@JsonClass(generateAdapter = true)
class FeatureD(
    var cat_imp: String?,
    var fac_len: String?,
    var fac_hgt: String?,
    var str_bod: String?,
    var str_wal: String?,
    var str_flr: String?,
    var str_jnt: String?,
    var str_hzd: String?,
    var fun_stt: String?,
    var fun_mch: String?,
    var dep_stt: String?,
    var etc_env: String?,
    var etc_fsh: String?,
    var eva_sur: String?,
    var eva_inf: String?,
    var eva_ope: String?,
    var eva_opi: String?,
) : Feature() {
    override fun toString(): String {
        return "FeatureD(cat_imp=$cat_imp, fac_len=$fac_len, fac_hgt=$fac_hgt, str_bod=$str_bod, str_wal=$str_wal, str_flr=$str_flr, str_jnt=$str_jnt, str_hzd=$str_hzd, fun_stt=$fun_stt, fun_mch=$fun_mch, dep_stt=$dep_stt, etc_env=$etc_env, etc_fsh=$etc_fsh, eva_sur=$eva_sur, eva_inf=$eva_inf, eva_ope=$eva_ope, eva_opi=$eva_opi) ${super.toString()}"
    }
}
