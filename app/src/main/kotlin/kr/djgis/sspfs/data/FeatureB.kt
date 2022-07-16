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
    override fun toString(): String {
        return "FeatureB(cat_funl=$cat_funl, cat_funm=$cat_funm, cat_funu=$cat_funu, cat_impl=$cat_impl, cat_impm=$cat_impm, cat_impu=$cat_impu, fac_len=$fac_len, fac_wid=$fac_wid, riv_widl=$riv_widl, riv_widm=$riv_widm, riv_widu=$riv_widu, bnk_hgtl=$bnk_hgtl, bnk_hgtm=$bnk_hgtm, bnk_hgtu=$bnk_hgtu, crn_wdll=$crn_wdll, crn_wdlm=$crn_wdlm, crn_wdlu=$crn_wdlu, crn_wdrl=$crn_wdrl, crn_wdrm=$crn_wdrm, crn_wdru=$crn_wdru, dmg_lifl=$dmg_lifl, dmg_lifm=$dmg_lifm, dmg_lifu=$dmg_lifu, dmg_prol=$dmg_prol, dmg_prom=$dmg_prom, dmg_prou=$dmg_prou, dmg_txtl=$dmg_txtl, dmg_txtm=$dmg_txtm, dmg_txtu=$dmg_txtu, fld_cdel=$fld_cdel, fld_cdem=$fld_cdem, fld_cdeu=$fld_cdeu, fld_txtl=$fld_txtl, fld_txtm=$fld_txtm, fld_txtu=$fld_txtu, bnk_stll=$bnk_stll, bnk_stlm=$bnk_stlm, bnk_stlu=$bnk_stlu, bnk_strl=$bnk_strl, bnk_strm=$bnk_strm, bnk_stru=$bnk_stru, rev_mall=$rev_mall, rev_malm=$rev_malm, rev_malu=$rev_malu, rev_marl=$rev_marl, rev_marm=$rev_marm, rev_maru=$rev_maru, rev_stll=$rev_stll, rev_stlm=$rev_stlm, rev_stlu=$rev_stlu, rev_strl=$rev_strl, rev_strm=$rev_strm, rev_stru=$rev_stru, dor_pasl=$dor_pasl, dor_pasm=$dor_pasm, dor_pasu=$dor_pasu, riv_chgl=$riv_chgl, riv_chgm=$riv_chgm, riv_chgu=$riv_chgu, riv_depl=$riv_depl, riv_depm=$riv_depm, riv_depu=$riv_depu, riv_pltl=$riv_pltl, riv_pltm=$riv_pltm, riv_pltu=$riv_pltu, riv_posl=$riv_posl, riv_posm=$riv_posm, riv_posu=$riv_posu, riv_dmgl=$riv_dmgl, riv_dmgm=$riv_dmgm, riv_dmgu=$riv_dmgu, eva_prxl=$eva_prxl, eva_prxm=$eva_prxm, eva_prxu=$eva_prxu, eva_infl=$eva_infl, eva_infm=$eva_infm, eva_infu=$eva_infu, eva_opel=$eva_opel, eva_opem=$eva_opem, eva_opeu=$eva_opeu, eva_opil=$eva_opil, eva_opim=$eva_opim, eva_opiu=$eva_opiu) ${super.toString()}"
    }
}

object FeatureBDiffCallback : DiffUtil.ItemCallback<FeatureB>() {
    override fun areItemsTheSame(oldItem: FeatureB, newItem: FeatureB) = oldItem.fac_uid === newItem.fac_uid
    override fun areContentsTheSame(oldItem: FeatureB, newItem: FeatureB) = oldItem == newItem
}
