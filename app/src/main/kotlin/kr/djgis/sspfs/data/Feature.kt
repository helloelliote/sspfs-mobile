/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import com.squareup.moshi.Json
import kr.djgis.sspfs.Config.DATETIME_FORMAT
import kr.djgis.sspfs.Config.DATETIME_FORMAT_RECEIVE
import kr.djgis.sspfs.Config.DATETIME_ZONE
import java.time.LocalDateTime.parse
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties

data class FeatureList(
    @Json(name = "rowCount") val featureCount: Int,
    @Json(name = "rows") val features: MutableSet<Feature>,
)

@Suppress("PropertyName")
open class Feature {
    var fac_typ: String? = null
    var fac_uid: String? = null
    var fac_nam: String? = null
    var mng_nam: String? = null
    var own_nam: String? = null
    var mng_tel: String? = null
    var hzd_uid: String? = null
    var hzd_ymd: String? = null
    var fac_adm: String? = null
    var fac_pid: String? = null
    var ben_txt: String? = null
    var sub_cnt: String? = null
    var sub_txt: String? = null
    var fun_cde: String? = null
    var pos_cde: String? = null
    var exm_opi: String? = null
    var exm_ymd: String? = null
    var exm_nam: String? = null
    var exm_chk: String? = null
    var img_fac: String? = null
    var img_rep: String? = null
    var geom: FeatureGeom? = null
    //    var image: List<FeatureAttachment>? = null

    val exm_ymd_date: String? = exm_ymd?.let(::parseDate)

    private fun parseDate(string: String): String? {
        return parse(string, DATETIME_FORMAT_RECEIVE).atZone(DATETIME_ZONE).format(DATETIME_FORMAT)
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

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
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
