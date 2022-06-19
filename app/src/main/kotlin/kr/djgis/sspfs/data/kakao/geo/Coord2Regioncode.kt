/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data.kakao.geo

import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Coord2Regioncode(
    val documents: MutableList<DocumentCR>,
    val meta: MetaCR,
) : Serializable

@JsonClass(generateAdapter = true)
data class DocumentCR(
    val region_type: String = "",
    val address_name: String = "",
    val region_1depth_name: String = "",
    val region_2depth_name: String = "",
    val region_3depth_name: String = "",
    val region_4depth_name: String = "",
    val code: String = "",
    val x: String = "",
    val y: String = "",
)

@JsonClass(generateAdapter = true)
data class MetaCR(
    val total_count: Int,
)

object DocumentCRDiffCallback : DiffUtil.ItemCallback<DocumentCR>() {
    override fun areItemsTheSame(oldItem: DocumentCR, newItem: DocumentCR) = oldItem.code == newItem.code
    override fun areContentsTheSame(oldItem: DocumentCR, newItem: DocumentCR) = oldItem == newItem
}
