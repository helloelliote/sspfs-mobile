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
    val type: String,
) : Serializable {
    val name by lazy {
        return@lazy when (type) {
            "b" -> "시점"
            "m" -> "중점"
            "e" -> "종점"
            "c" -> "근경"
            "f" -> "원경"
            "s" -> "측면"
            else -> "기타"
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<FeatureAttachment>() {
    override fun areItemsTheSame(oldItem: FeatureAttachment, newItem: FeatureAttachment): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: FeatureAttachment, newItem: FeatureAttachment): Boolean {
        return oldItem == newItem
    }
    // TODO: 사진 테이블에서 시점 종점 사진 제외하고 0 부터 넘버링하기
}
