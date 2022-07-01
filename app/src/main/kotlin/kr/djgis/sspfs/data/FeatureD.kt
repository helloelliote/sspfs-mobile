/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
class FeatureDList(
    @Json(name = "rowCount") val FeatureCount: Int,
    @Json(name = "rows") val features: MutableSet<FeatureD>,
) : Serializable

@Suppress("PropertyName")
@JsonClass(generateAdapter = true)
class FeatureD(

) : Feature() {

}

object FeatureDDiffCallback : DiffUtil.ItemCallback<FeatureD>() {
    override fun areItemsTheSame(oldItem: FeatureD, newItem: FeatureD) = oldItem.fac_uid === newItem.fac_uid
    override fun areContentsTheSame(oldItem: FeatureD, newItem: FeatureD) = oldItem == newItem
}
