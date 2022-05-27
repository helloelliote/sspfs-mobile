/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.attachment

import android.view.View
import kr.djgis.sspfs.data.FeatureAttachment

interface FeatureAttachmentAdapterListener {
    fun onClicked(view: View, attachment: FeatureAttachment)
    fun onLongPressed(attachment: FeatureAttachment): Boolean
    fun onStarChanged(attachment: FeatureAttachment, newValue: Boolean)
    fun onArchived(attachment: FeatureAttachment)
}
