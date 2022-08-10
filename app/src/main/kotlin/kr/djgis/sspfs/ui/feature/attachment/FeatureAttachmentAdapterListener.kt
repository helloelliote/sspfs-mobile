/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.attachment

import android.view.View
import kr.djgis.sspfs.data.FeatureAttachment

interface FeatureAttachmentAdapterListener {
    fun onClick(view: View, attachment: FeatureAttachment)
    fun onLongClick(view: View, attachment: FeatureAttachment): Boolean
    fun onStarChanged(attachment: FeatureAttachment, newValue: Boolean)
    fun onArchived(attachment: FeatureAttachment)
}
