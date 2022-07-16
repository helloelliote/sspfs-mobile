/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
class FeatureFList(
    @Json(name = "rowCount") val featureCount: Int,
    @Json(name = "rows") val features: MutableSet<FeatureF>,
) : Serializable

@Suppress("PropertyName")
@JsonClass(generateAdapter = true)
class FeatureF(
    var cat_funl: String?,
    var cat_funm: String?,
    var cat_funu: String?,
    var cat_impl: String?,
    var cat_impm: String?,
    var cat_impu: String?,
    var fac_len: String?,
    var fac_wid: String?,
    var str_widl: String?,
    var str_widm: String?,
    var str_widu: String?,
    var str_pavl: String?,
    var str_pavm: String?,
    var str_pavu: String?,
    var str_shpl: String?,
    var str_shpm: String?,
    var str_shpu: String?,
    var str_safl: String?,
    var str_safm: String?,
    var str_safu: String?,
    var str_snkl: String?,
    var str_snkm: String?,
    var str_snku: String?,
    var fun_pavl: String?,
    var fun_pavm: String?,
    var fun_pavu: String?,
    var fun_unpl: String?,
    var fun_unpm: String?,
    var fun_unpu: String?,
    var fun_flal: String?,
    var fun_flam: String?,
    var fun_flau: String?,
    var dmg_subl: String?,
    var dmg_subm: String?,
    var dmg_subu: String?,
    var dmg_sewl: String?,
    var dmg_sewm: String?,
    var dmg_sewu: String?,
    var etc_posl: String?,
    var etc_posm: String?,
    var etc_posu: String?,
    var eva_rivl: String?,
    var eva_rivm: String?,
    var eva_rivu: String?,
    var eva_posl: String?,
    var eva_posm: String?,
    var eva_posu: String?,
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
        return "FeatureF(cat_funl=$cat_funl, cat_funm=$cat_funm, cat_funu=$cat_funu, cat_impl=$cat_impl, cat_impm=$cat_impm, cat_impu=$cat_impu, fac_len=$fac_len, fac_wid=$fac_wid, str_widl=$str_widl, str_widm=$str_widm, str_widu=$str_widu, str_pavl=$str_pavl, str_pavm=$str_pavm, str_pavu=$str_pavu, str_shpl=$str_shpl, str_shpm=$str_shpm, str_shpu=$str_shpu, str_safl=$str_safl, str_safm=$str_safm, str_safu=$str_safu, str_snkl=$str_snkl, str_snkm=$str_snkm, str_snku=$str_snku, fun_pavl=$fun_pavl, fun_pavm=$fun_pavm, fun_pavu=$fun_pavu, fun_unpl=$fun_unpl, fun_unpm=$fun_unpm, fun_unpu=$fun_unpu, fun_flal=$fun_flal, fun_flam=$fun_flam, fun_flau=$fun_flau, dmg_subl=$dmg_subl, dmg_subm=$dmg_subm, dmg_subu=$dmg_subu, dmg_sewl=$dmg_sewl, dmg_sewm=$dmg_sewm, dmg_sewu=$dmg_sewu, etc_posl=$etc_posl, etc_posm=$etc_posm, etc_posu=$etc_posu, eva_rivl=$eva_rivl, eva_rivm=$eva_rivm, eva_rivu=$eva_rivu, eva_posl=$eva_posl, eva_posm=$eva_posm, eva_posu=$eva_posu, eva_prxl=$eva_prxl, eva_prxm=$eva_prxm, eva_prxu=$eva_prxu, eva_infl=$eva_infl, eva_infm=$eva_infm, eva_infu=$eva_infu, eva_opel=$eva_opel, eva_opem=$eva_opem, eva_opeu=$eva_opeu, eva_opil=$eva_opil, eva_opim=$eva_opim, eva_opiu=$eva_opiu) ${super.toString()}"
    }
}

object FeatureFDiffCallback : DiffUtil.ItemCallback<FeatureF>() {
    override fun areItemsTheSame(oldItem: FeatureF, newItem: FeatureF) = oldItem.fac_uid === newItem.fac_uid
    override fun areContentsTheSame(oldItem: FeatureF, newItem: FeatureF) = oldItem == newItem
}
