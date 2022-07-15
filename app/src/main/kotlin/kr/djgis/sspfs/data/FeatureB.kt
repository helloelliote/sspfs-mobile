/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
class FeatureBList(
    @Json(name = "rowCount") val featureCount: Int,
    @Json(name = "rows") val features: MutableSet<FeatureB>,
) : Serializable

@Suppress("PropertyName")
@JsonClass(generateAdapter = true)
class FeatureB(
    var cat_funl: String?,
    var cat_funm: String?,
    var cat_funu: String?,
    var cat_impl: String?,
    var cat_impm: String?,
    var cat_impu: String?,
    var fac_len: String?,
    var fac_wid: String?,
    var riv_widl: String?,
    var riv_widm: String?,
    var riv_widu: String?,
    var bnk_hgtl: String?,
    var bnk_hgtm: String?,
    var bnk_hgtu: String?,
    var crn_wdll: String?,
    var crn_wdlm: String?,
    var crn_wdlu: String?,
    var crn_wdrl: String?,
    var crn_wdrm: String?,
    var crn_wdru: String?,
    var dmg_lifl: String?,
    var dmg_lifm: String?,
    var dmg_lifu: String?,
    var dmg_prol: String?,
    var dmg_prom: String?,
    var dmg_prou: String?,
    var dmg_txtl: String?,
    var dmg_txtm: String?,
    var dmg_txtu: String?,
    var fld_cdel: String?,
    var fld_cdem: String?,
    var fld_cdeu: String?,
    var fld_txtl: String?,
    var fld_txtm: String?,
    var fld_txtu: String?,
    var bnk_stll: String?,
    var bnk_stlm: String?,
    var bnk_stlu: String?,
    var bnk_strl: String?,
    var bnk_strm: String?,
    var bnk_stru: String?,
    var rev_mall: String?,
    var rev_malm: String?,
    var rev_malu: String?,
    var rev_marl: String?,
    var rev_marm: String?,
    var rev_maru: String?,
    var rev_stll: String?,
    var rev_stlm: String?,
    var rev_stlu: String?,
    var rev_strl: String?,
    var rev_strm: String?,
    var rev_stru: String?,
    var dor_pasl: String?,
    var dor_pasm: String?,
    var dor_pasu: String?,
    var riv_chgl: String?,
    var riv_chgm: String?,
    var riv_chgu: String?,
    var riv_depl: String?,
    var riv_depm: String?,
    var riv_depu: String?,
    var riv_pltl: String?,
    var riv_pltm: String?,
    var riv_pltu: String?,
    var riv_posl: String?,
    var riv_posm: String?,
    var riv_posu: String?,
    var riv_dmgl: String?,
    var riv_dmgm: String?,
    var riv_dmgu: String?,
    var eva_prxl: String?,
    var eva_prxm: String?,
    var eva_prxu: String?,
    var eva_infl: String?,
    var eva_infm: String?,
    var eva_infu: String?,
    var eva_opel: String?,
    var eva_opem: String?,
    var eva_opeu: String?,
    var eva_opil: String?,
    var eva_opim: String?,
    var eva_opiu: String?,
) : Feature() {

}

object FeatureBDiffCallback : DiffUtil.ItemCallback<FeatureB>() {
    override fun areItemsTheSame(oldItem: FeatureB, newItem: FeatureB) = oldItem.fac_uid === newItem.fac_uid
    override fun areContentsTheSame(oldItem: FeatureB, newItem: FeatureB) = oldItem == newItem
}
