/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
class FeatureEList(
    @Json(name = "rowCount") val featureCount: Int,
    @Json(name = "rows") val features: MutableSet<FeatureE>,
) : Serializable

@Suppress("PropertyName")
@JsonClass(generateAdapter = true)
class FeatureE(

) : Feature() {

}

object FeatureEDiffCallback : DiffUtil.ItemCallback<FeatureE>() {
    override fun areItemsTheSame(oldItem: FeatureE, newItem: FeatureE) = oldItem.fac_uid === newItem.fac_uid
    override fun areContentsTheSame(oldItem: FeatureE, newItem: FeatureE) = oldItem == newItem
}
