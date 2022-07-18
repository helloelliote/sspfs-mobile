/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.attachment

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FeatureAttachmentDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(10, 0, 10, 20)
    }
}
