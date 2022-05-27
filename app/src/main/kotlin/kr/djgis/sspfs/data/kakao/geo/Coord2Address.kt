/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data.kakao.geo

import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Coord2Address(
    val documents: MutableList<DocumentCA>,
    val meta: MetaCA,
) : Serializable

@JsonClass(generateAdapter = true)
data class DocumentCA(
    val road_address: RoadAddressCA?,
    val address: AddressCA,
)

@JsonClass(generateAdapter = true)
data class RoadAddressCA(
    val address_name: String = "",
    val region_1depth_name: String = "",
    val region_2depth_name: String = "",
    val region_3depth_name: String = "",
    val road_name: String = "",
    val underground_yn: String = "",
    val main_building_no: String = "",
    val sub_building_no: String = "",
    val building_name: String = "",
    val zone_no: String = "",
)

@JsonClass(generateAdapter = true)
data class AddressCA(
    val address_name: String = "",
    val region_1depth_name: String = "",
    val region_2depth_name: String = "",
    val region_3depth_name: String = "",
    val mountain_yn: String = "",
    val main_address_no: String = "",
    val sub_address_no: String = "",
)

@JsonClass(generateAdapter = true)
data class MetaCA(
    val total_count: Int,
)

object DocumentCADiffCallback : DiffUtil.ItemCallback<DocumentCA>() {
    override fun areItemsTheSame(oldItem: DocumentCA, newItem: DocumentCA) =
        oldItem.address.address_name == newItem.address.address_name
    override fun areContentsTheSame(oldItem: DocumentCA, newItem: DocumentCA) = oldItem == newItem
}
