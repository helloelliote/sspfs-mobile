/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
class FeatureAList(
    @Json(name = "rowCount") val featureCount: Int,
    @Json(name = "rows") val features: MutableSet<FeatureA>,
) : Serializable

@Suppress("PropertyName")
@JsonClass(generateAdapter = true)
class FeatureA(
    var cat_typ: String?,
    var cat_pos: String?,
    var cat_fun: String?,
    var cat_imp: String?,
    var fac_len: String?,
    var fac_wid: String?,
    var fac_hgt: String?,
    var fac_gap: String?,
    var sec_flr: String?,
    var sec_wid: String?,
    var sec_hgt: String?,
    var sec_dia: String?,
    var sec_col: String?,
    var riv_upp: String?,
    var riv_mid: String?,
    var riv_low: String?,
    var str_dmg: String?,
    var str_reb: String?,
    var str_hol: String?,
    var str_old: String?,
    var fld_dmg: String?,
    var fld_wal: String?,
    var etc_trf: String?,
    var etc_trh: String?,
    var eva_stt: String?,
    var eva_sur: String?,
    var eva_pxm: String?,
    var eva_inf: String?,
    var eva_roa: String?,
    var eva_ope: String?,
    var eva_opi: String?,
) : Feature() {

}

object FeatureADiffCallback : DiffUtil.ItemCallback<FeatureA>() {
    override fun areItemsTheSame(oldItem: FeatureA, newItem: FeatureA) = oldItem.fac_uid === newItem.fac_uid
    override fun areContentsTheSame(oldItem: FeatureA, newItem: FeatureA) = oldItem == newItem
}
