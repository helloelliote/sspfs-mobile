/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.tabs

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.Job
import kr.djgis.sspfs.data.FeatureAttachment
import kr.djgis.sspfs.model.FeatureVMFactory
import kr.djgis.sspfs.model.FeatureViewModel
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentAdapter
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentAdapterListener

open class FeatureTabs : Fragment(), FeatureAttachmentAdapterListener {

    val viewModel: FeatureViewModel by activityViewModels { FeatureVMFactory }
    lateinit var featureAttachmentAdapter: FeatureAttachmentAdapter
    var coroutineJob: Job? = null

    override fun onClicked(view: View, attachment: FeatureAttachment) {
    }

    override fun onLongPressed(attachment: FeatureAttachment): Boolean {
        return false
    }

    override fun onStarChanged(attachment: FeatureAttachment, newValue: Boolean) {
    }

    override fun onArchived(attachment: FeatureAttachment) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineJob?.cancel()
    }
}
