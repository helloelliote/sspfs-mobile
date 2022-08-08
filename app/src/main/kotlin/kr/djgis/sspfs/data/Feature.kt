/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import com.squareup.moshi.Json
import kr.djgis.sspfs.Config.DATETIME_FORMAT
import java.io.Serializable
import java.time.ZoneId.of
import java.time.ZonedDateTime.parse
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties

data class FeatureList(
    @Json(name = "rowCount") val featureCount: Int,
    @Json(name = "rows") val features: MutableSet<Feature>,
)

@Suppress("PropertyName", "FunctionName")
open class Feature : Serializable {
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
/*

@JsonClass(generateAdapter = true)
data class FeatureJson(
    var fac_typ: String?,
    var fac_uid: String,
    var fac_nam: String?,
    var mng_nam: String?,
    var own_nam: String?,
    var mng_tel: String?,
    var hzd_uid: String?,
    var hzd_ymd: String?,
    var fac_adm: String?,
    var fac_pid: String?,
    var ben_txt: String?,
    var sub_cnt: String?,
    var sub_txt: String?,
    var fun_cde: String?,
    var pos_cde: String?,
    var exm_opi: String?,
    var exm_ymd: String?,
    var exm_nam: String?,
    var exm_chk: String?,
    var img_fac: String?,
    var img_rep: String?,
//    var image: Any? = null
    var geom: Any,
) : Serializable

class FeatureJsonAdapter {
    @FromJson
    fun fromJson(json: FeatureJson): Feature {
        return Feature(
            fac_uid = json.fac_uid,
            fac_typ = json.fac_typ,
            fac_nam = json.fac_nam,
//            image = image(json),
            geom = geom(json),
        )
    }

*/
/*    private fun image(json: FeatureJson): List<FeatureAttachment> {
        val list = mutableListOf<FeatureAttachment>()
        val jsonArray = Gson().toJsonTree(json.image).asJsonArray
        jsonArray.forEach {
            val item = it.asJsonObject
            list.add(FeatureAttachment(
                url = item["url"]?.asString,
                type = item["type"].asString,
            ))
        }
        return list
    }*//*


    private fun geom(json: FeatureJson): FeatureGeom {
        val jsonObject = Gson().toJsonTree(json.geom).asJsonObject
        return FeatureGeom(
            type = jsonObject["type"].asString,
            coordinates = jsonObject["coordinates"].asJsonArray,
        )
    }

    @ToJson
    fun toJson(feature: Feature): FeatureJson {
        return FeatureJson(
            fac_uid = feature.fac_uid,
            fac_typ = feature.fac_typ,
            fac_nam = feature.fac_nam,
//            image = Gson().toJson(feature.image),
            geom = Gson().toJson(feature.geom),
        )
    }
}

object FeatureDiffCallback : DiffUtil.ItemCallback<Feature>() {
    override fun areItemsTheSame(oldItem: Feature, newItem: Feature) = oldItem.fac_uid === newItem.fac_uid
    override fun areContentsTheSame(oldItem: Feature, newItem: Feature) = oldItem == newItem
}
*/
