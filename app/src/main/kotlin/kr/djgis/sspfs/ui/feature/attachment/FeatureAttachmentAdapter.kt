/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.attachment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kr.djgis.sspfs.data.FeatureAttachment
import kr.djgis.sspfs.databinding.FeatureAttachmentItemBinding

/**
 * Generic RecyclerView.Adapter to display [FeatureAttachment]s.
 */
class FeatureAttachmentAdapter(
    private val listener: FeatureAttachmentAdapterListener,
) : ListAdapter<FeatureAttachment, FeatureAttachmentViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<FeatureAttachment>() {
        override fun areItemsTheSame(oldItem: FeatureAttachment, newItem: FeatureAttachment): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FeatureAttachment, newItem: FeatureAttachment): Boolean {
            return oldItem.url == newItem.url
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureAttachmentViewHolder {
        return FeatureAttachmentViewHolder(
            FeatureAttachmentItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            listener,
        )
    }

    override fun onBindViewHolder(holder: FeatureAttachmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
