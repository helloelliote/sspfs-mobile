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

) : Feature() {

}

object FeatureBDiffCallback : DiffUtil.ItemCallback<FeatureB>() {
    override fun areItemsTheSame(oldItem: FeatureB, newItem: FeatureB) = oldItem.fac_uid === newItem.fac_uid
    override fun areContentsTheSame(oldItem: FeatureB, newItem: FeatureB) = oldItem == newItem
}
