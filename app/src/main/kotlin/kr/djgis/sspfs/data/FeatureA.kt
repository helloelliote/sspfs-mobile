/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties

@JsonClass(generateAdapter = true)
class FeatureAList(
    @Json(name = "rowCount") val featureCount: Int,
    @Json(name = "rows") val features: MutableSet<FeatureA>,
) : Serializable

@Suppress("PropertyName")
@JsonClass(generateAdapter = true)
class FeatureA(
    var cat_typ: String? = "0",
    var cat_pos: String? = "0",
    var cat_fun: String? = "0",
    var cat_imp: String? = "1",
    var fac_len: String?,
    var fac_wid: String?,
    var fac_hgt: String?,
    var fac_gap: String?,
    var sec_flr: String? = "1",
    var sec_wid: String?,
    var sec_hgt: String?,
    var sec_dia: String?,
    var sec_col: String?,
    var riv_upp: String?,
    var riv_mid: String?,
    var riv_low: String?,
    var str_dmg: String? = "1",
    var str_reb: String? = "1",
    var str_hol: String? = "1",
    var str_old: String? = "1",
    var fld_dmg: String? = "1",
    var fld_wal: String? = "1",
    var etc_trf: String? = "1",
    var etc_trh: String? = "1",
    var eva_stt: String? = "1",
    var eva_sur: String? = "1",
    var eva_pxm: String? = "1",
    var eva_inf: String? = "1",
    var eva_roa: String? = "0",
    var eva_ope: String? = "0",
    var eva_opi: String?,
) : Feature(), Serializable {
    override fun toString(): String {
        return "FeatureA(cat_typ=$cat_typ, cat_pos=$cat_pos, cat_fun=$cat_fun, cat_imp=$cat_imp, fac_len=$fac_len, fac_wid=$fac_wid, fac_hgt=$fac_hgt, fac_gap=$fac_gap, sec_flr=$sec_flr, sec_wid=$sec_wid, sec_hgt=$sec_hgt, sec_dia=$sec_dia, sec_col=$sec_col, riv_upp=$riv_upp, riv_mid=$riv_mid, riv_low=$riv_low, str_dmg=$str_dmg, str_reb=$str_reb, str_hol=$str_hol, str_old=$str_old, fld_dmg=$fld_dmg, fld_wal=$fld_wal, etc_trf=$etc_trf, etc_trh=$etc_trh, eva_stt=$eva_stt, eva_sur=$eva_sur, eva_pxm=$eva_pxm, eva_inf=$eva_inf, eva_roa=$eva_roa, eva_ope=$eva_ope, eva_opi=$eva_opi) ${super.toString()}"
    }
}

object FeatureADiffCallback : DiffUtil.ItemCallback<FeatureA>() {
    override fun areItemsTheSame(oldItem: FeatureA, newItem: FeatureA) = oldItem.fac_uid === newItem.fac_uid
    override fun areContentsTheSame(oldItem: FeatureA, newItem: FeatureA) = oldItem == newItem
}
