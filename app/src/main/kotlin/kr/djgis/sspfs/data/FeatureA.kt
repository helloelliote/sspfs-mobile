/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import androidx.recyclerview.widget.DiffUtil
import com.google.gson.Gson
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import kr.djgis.sspfs.Config.DATETIME_FORMAT
import kr.djgis.sspfs.Config.DATETIME_FORMAT_RECEIVE
import kr.djgis.sspfs.Config.DATETIME_ZONE
import java.io.Serializable
import java.time.LocalDateTime
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties

@JsonClass(generateAdapter = true)
data class FeatureAList(
    @Json(name = "rowCount") val featureCount: Int,
    @Json(name = "rows") val features: MutableSet<FeatureA>,
) : Serializable

@JsonClass(generateAdapter = true)
data class FeatureA(
    var fac_uid: String?,
    var fac_nam: String?,
    var mng_nam: String?,
    var own_nam: String?,
    var mnz_tel: String?,
    var hzd_uid: String?,
    var hzd_ymd: String?,
    var fac_adm: String?,
    var fac_pid: String?,
    var pos_nam: String?,
    var ben_txt: String?,
    var cat_cde: String?,
    var typ_cde: String?,
    var typ_txt: String?,
    var sub_cnt: String?,
    var sub_txt: String?,
    var fun_cde: String?,
    var pos_cde: String?,
    var exm_opi: String?,
    var exm_ymd: String?,
    var exm_nam: String?,
    var fac_len: String?,
    var fac_wid: String?,
    var fac_hgt: String?,
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
//    var img_fac: List<FeatureAttachment>?,
//    var img_rep: List<FeatureAttachment>?,
    var img_fac: Any?,
    var img_rep: Any?,
    var geom: FeatureGeom,
) : FeatureBase() {
    val exm_ymd_date: String? = exm_ymd?.let(::parseDate)

    private fun parseDate(string: String): String? {
        return LocalDateTime.parse(string, DATETIME_FORMAT_RECEIVE).atZone(DATETIME_ZONE).format(DATETIME_FORMAT)
    }

    fun setByKey(key: String, value: Any?) {
        for (property in this::class.declaredMemberProperties) {
            if (property.name == key) (property as? KMutableProperty<*>)?.setter?.call(this, value)
        }
    }

    fun getByKey(key: String): Any? {
        for (property in this::class.declaredMemberProperties) {
            if (property.name == key) return property.getter.call(this)
        }
        return null
    }
}

@JsonClass(generateAdapter = true)
data class FeatureAJson(
    var fac_uid: String?,
    var fac_nam: String?,
    var mng_nam: String?,
    var own_nam: String?,
    var mnz_tel: String?,
    var hzd_uid: String?,
    var hzd_ymd: String?,
    var fac_adm: String?,
    var fac_pid: String?,
    var pos_nam: String?,
    var ben_txt: String?,
    var cat_cde: String?,
    var typ_cde: String?,
    var typ_txt: String?,
    var sub_cnt: String?,
    var sub_txt: String?,
    var fun_cde: String?,
    var pos_cde: String?,
    var exm_opi: String?,
    var exm_ymd: String?,
    var exm_nam: String?,
    var fac_len: String?,
    var fac_wid: String?,
    var fac_hgt: String?,
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
    var img_fac: Any?,
    var img_rep: Any?,
    var geom: Any,
//    var img_fac: List<FeatureAttachment>?,
//    var img_rep: List<FeatureAttachment>?,
)

class FeatureAJsonAdapter {
    @FromJson
    fun fromJson(json: FeatureAJson): FeatureA {
        // TODO: 시설물 따라 다른 값 리턴
        return FeatureA(
            fac_uid = json.fac_uid,
            fac_nam = json.fac_nam,
            mng_nam = json.mng_nam,
            own_nam = json.own_nam,
            mnz_tel = json.mnz_tel,
            hzd_uid = json.hzd_uid,
            hzd_ymd = json.hzd_ymd,
            fac_adm = json.fac_adm,
            fac_pid = json.fac_pid,
            pos_nam = json.pos_nam,
            ben_txt = json.ben_txt,
            cat_cde = json.cat_cde,
            typ_cde = json.typ_cde,
            typ_txt = json.typ_txt,
            sub_cnt = json.sub_cnt,
            sub_txt = json.sub_txt,
            fun_cde = json.fun_cde,
            pos_cde = json.pos_cde,
            exm_opi = json.exm_opi,
            exm_ymd = json.exm_ymd,
            exm_nam = json.exm_nam,
            fac_len = json.fac_len,
            fac_wid = json.fac_wid,
            fac_hgt = json.fac_hgt,
            sec_flr = json.sec_flr,
            sec_wid = json.sec_wid,
            sec_hgt = json.sec_hgt,
            sec_dia = json.sec_dia,
            sec_col = json.sec_col,
            riv_upp = json.riv_upp,
            riv_mid = json.riv_mid,
            riv_low = json.riv_low,
            str_dmg = json.str_dmg,
            str_reb = json.str_reb,
            str_hol = json.str_hol,
            str_old = json.str_old,
            fld_dmg = json.fld_dmg,
            fld_wal = json.fld_wal,
            etc_trf = json.etc_trf,
            etc_trh = json.etc_trh,
            eva_stt = json.eva_stt,
            eva_sur = json.eva_sur,
            eva_pxm = json.eva_pxm,
            eva_inf = json.eva_inf,
            eva_roa = json.eva_roa,
            eva_ope = json.eva_ope,
            eva_opi = json.eva_opi,
            img_fac = json.img_fac,
            img_rep = json.img_rep,
            geom = geom(json),
        )
    }

    private fun geom(json: FeatureAJson): FeatureGeom {
        val jsonObject = Gson().toJsonTree(json.geom).asJsonObject
        return FeatureGeom(
            type = jsonObject["type"].asString,
            coordinates = jsonObject["coordinates"].asJsonArray,
        )
    }

    @ToJson
    fun toJson(feature: FeatureA): FeatureAJson {
        return FeatureAJson(
            fac_uid = feature.fac_uid,
            fac_nam = feature.fac_nam,
            mng_nam = feature.mng_nam,
            own_nam = feature.own_nam,
            mnz_tel = feature.mnz_tel,
            hzd_uid = feature.hzd_uid,
            hzd_ymd = feature.hzd_ymd,
            fac_adm = feature.fac_adm,
            fac_pid = feature.fac_pid,
            pos_nam = feature.pos_nam,
            ben_txt = feature.ben_txt,
            cat_cde = feature.cat_cde,
            typ_cde = feature.typ_cde,
            typ_txt = feature.typ_txt,
            sub_cnt = feature.sub_cnt,
            sub_txt = feature.sub_txt,
            fun_cde = feature.fun_cde,
            pos_cde = feature.pos_cde,
            exm_opi = feature.exm_opi,
            exm_ymd = feature.exm_ymd,
            exm_nam = feature.exm_nam,
            fac_len = feature.fac_len,
            fac_wid = feature.fac_wid,
            fac_hgt = feature.fac_hgt,
            sec_flr = feature.sec_flr,
            sec_wid = feature.sec_wid,
            sec_hgt = feature.sec_hgt,
            sec_dia = feature.sec_dia,
            sec_col = feature.sec_col,
            riv_upp = feature.riv_upp,
            riv_mid = feature.riv_mid,
            riv_low = feature.riv_low,
            str_dmg = feature.str_dmg,
            str_reb = feature.str_reb,
            str_hol = feature.str_hol,
            str_old = feature.str_old,
            fld_dmg = feature.fld_dmg,
            fld_wal = feature.fld_wal,
            etc_trf = feature.etc_trf,
            etc_trh = feature.etc_trh,
            eva_stt = feature.eva_stt,
            eva_sur = feature.eva_sur,
            eva_pxm = feature.eva_pxm,
            eva_inf = feature.eva_inf,
            eva_roa = feature.eva_roa,
            eva_ope = feature.eva_ope,
            eva_opi = feature.eva_opi,
            img_fac = feature.img_fac,
            img_rep = feature.img_rep,
            geom = feature.geom,
        )
    }
}

object FeatureADiffCallback : DiffUtil.ItemCallback<FeatureA>() {
    override fun areItemsTheSame(oldItem: FeatureA, newItem: FeatureA) = oldItem.fac_uid === newItem.fac_uid
    override fun areContentsTheSame(oldItem: FeatureA, newItem: FeatureA) = oldItem == newItem
}
