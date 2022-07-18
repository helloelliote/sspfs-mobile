/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.JsonClass
import retrofit2.http.Url
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class FeatureAttachment(
    @Url var url: String?,
    var name: String?,
    val type: String,
) : Serializable

class DiffCallback : DiffUtil.ItemCallback<FeatureAttachment>() {
    override fun areItemsTheSame(oldItem: FeatureAttachment, newItem: FeatureAttachment): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: FeatureAttachment, newItem: FeatureAttachment): Boolean {
        return oldItem == newItem
    }
    // TODO: 사진 테이블에서 시점 종점 사진 제외하고 0 부터 넘버링하기
}
