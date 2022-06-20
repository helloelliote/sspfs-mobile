/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data.kakao.search

import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.JsonClass
import java.io.Serializable
import kotlin.math.round

@JsonClass(generateAdapter = true)
data class Keyword(
    val documents: MutableList<Document>,
    val meta: Meta,
) : Serializable

@JsonClass(generateAdapter = true)
data class Document(
    val address_name: String = "",
    val category_group_code: String = "",
    val category_group_name: String = "",
    val category_name: String = "",
    val distance: String = "-1.0",
    val id: String = "",
    val phone: String = "",
    val place_name: String = "",
    val place_url: String = "",
    val road_address_name: String = "",
    val x: String = "",
    val y: String = "",
) {
    val isClickable: Boolean = id.isNotBlank()
    val addressFormat = with(road_address_name) {
        return@with when (this) {
            "" -> address_name
            else -> road_address_name
        }
    }
    val distanceFormat = with(distance.toDouble()) {
        return@with when (this) {
            -1.0 -> ""
            in 0.0..1_000.0 -> "$this m"
            else -> "${round(this / 10) / 100} km"
        }
    }
}

val DOCUMENT_NONE = Document(
    place_name = "검색결과가 없습니다.",
    road_address_name = "장소를 찾을 때 주소, 전화번호도 활용해 보세요.",
    category_name = "예) 대진기술정보(주) 또는 국채보상로 159길41"
)

@JsonClass(generateAdapter = true)
data class Meta(
    val is_end: Boolean,
    val pageable_count: Int,
    val same_name: SameName,
    val total_count: Int,
)

@JsonClass(generateAdapter = true)
data class SameName(
    val keyword: String,
    val region: List<Any>,
    val selected_region: String,
)

object DocumentDiffCallback : DiffUtil.ItemCallback<Document>() {
    override fun areItemsTheSame(oldItem: Document, newItem: Document) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Document, newItem: Document) = oldItem == newItem
}
