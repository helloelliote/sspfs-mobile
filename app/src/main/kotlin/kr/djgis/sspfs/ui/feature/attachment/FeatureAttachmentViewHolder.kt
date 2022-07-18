/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.attachment

import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import kr.djgis.sspfs.data.FeatureAttachment

/**
 * Generic RecyclerView.ViewHolder which is able to bind layouts which expose a variable for [FeatureAttachment].
 */
class FeatureAttachmentViewHolder(
    private val binding: ViewDataBinding, private val listener: FeatureAttachmentAdapter.OnClickListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(attachment: FeatureAttachment) {
        binding.run {
            setVariable(BR.featureAttachment, attachment)
            setVariable(BR.listener, listener)
            executePendingBindings()
        }
    }
}
