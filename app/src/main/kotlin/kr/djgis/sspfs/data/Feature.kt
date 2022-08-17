/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kr.djgis.sspfs.Config.DATETIME_FORMAT
import java.time.ZoneId.of
import java.time.ZonedDateTime.parse
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties

@JsonClass(generateAdapter = true)
data class FeatureList(
    @field:Json(name = "rowCount") val featureCount: Int,
    @field:Json(name = "rows") val features: MutableSet<Feature>,
)

@Suppress("PropertyName", "FunctionName")
@JsonClass(generateAdapter = true)
open class Feature {
    var fac_typ: String = ""
    var fac_uid: String = ""
    var fac_nam: String = ""
    var mng_nam: String? = null
    var own_nam: String? = null
    var mng_tel: String? = null
    var hzd_uid: String? = null
    var hzd_ymd: String? = null
    var fac_adl: String? = null
    var fac_adm: String? = null
    var fac_adu: String? = null
    var fac_pid: String? = null
    var pos_nam: String? = null
    var ben_txt: String? = null
    var cat_cde: String? = null
    var typ_cde: MutableSet<String>? = mutableSetOf()
    var sub_cnt: String? = null
    var sub_txt: String? = null
    var fun_cde: MutableSet<String>? = mutableSetOf()
    var pos_cde: MutableSet<String>? = mutableSetOf()
    var exm_opi: String? = null
    var exm_ymd: String? = null
    var exm_nam: String? = null
    var exm_chk: String? = null
    var img_fac: MutableList<FeatureAttachment> = mutableListOf()
    var img_rep: String? = null
    var geom: FeatureGeom? = null

    open fun getByKey(key: String): Any? {
        for (property in this::class.declaredMemberProperties) {
            if (property.name == key) return property.getter.call(this)
        }
        return Feature::class.declaredMemberProperties.find { it.name == key }?.call(this)
    }

    open fun setByKey(key: String, value: Any?) {
        for (property in this::class.declaredMemberProperties) {
            if (property.name == key) (property as? KMutableProperty<*>)?.setter?.call(this, value)
        }
    }

    open fun exm_ymd(): String = try {
        parse(exm_ymd).withZoneSameInstant(of("Asia/Seoul")).format(DATETIME_FORMAT)
    } catch (e: Exception) {
        ""
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Feature
        if (fac_uid != other.fac_uid) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toString(): String {
        return "Feature(fac_typ=$fac_typ, fac_uid=$fac_uid, fac_nam=$fac_nam, mng_nam=$mng_nam, own_nam=$own_nam, mng_tel=$mng_tel, hzd_uid=$hzd_uid, hzd_ymd=$hzd_ymd, fac_adl=$fac_adl, fac_adm=$fac_adm, fac_adu=$fac_adu, fac_pid=$fac_pid, pos_nam=$pos_nam, ben_txt=$ben_txt, cat_cde=$cat_cde, typ_cde=$typ_cde, sub_cnt=$sub_cnt, sub_txt=$sub_txt, fun_cde=$fun_cde, pos_cde=$pos_cde, exm_opi=$exm_opi, exm_ymd=$exm_ymd, exm_nam=$exm_nam, exm_chk=$exm_chk, img_fac=$img_fac, img_rep=$img_rep, geom=$geom)"
    }
}
