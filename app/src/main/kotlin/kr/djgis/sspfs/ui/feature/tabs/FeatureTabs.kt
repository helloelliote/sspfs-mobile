/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.tabs

import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.Job
import kr.djgis.sspfs.R
import kr.djgis.sspfs.data.FeatureAttachment
import kr.djgis.sspfs.model.FeatureVMFactory
import kr.djgis.sspfs.model.FeatureViewModel
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentAdapter
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentAdapterListener

open class FeatureTabs : Fragment(), FeatureAttachmentAdapterListener {

    val viewModel: FeatureViewModel by activityViewModels { FeatureVMFactory }
    private val viewSelect by lazy { return@lazy R.drawable.tablelayout_border_row_select }
    private val viewDeselect by lazy { return@lazy R.drawable.tablelayout_border_row_deselect }

    var coroutineJob: Job? = null
    lateinit var featureAttachmentAdapter: FeatureAttachmentAdapter

    fun setTableLayoutOnClickListener(table: TableLayout) {
        try {
            val rowCount = table.childCount
            for (i: Int in 0..rowCount) {
                val row = table.getChildAt(i) as TableRow
                val columnCount = row.childCount
                val selectableColumns = mutableListOf<TextView>()
                for (j: Int in 1..columnCount) {
                    val column = row.getChildAt(j)
                    if (column is TextView && column.isClickable) {
                        // TODO: id 에 변수명 부여
                        column.setOnClickListener {
                            (it as TextView).run {
                                isSelected = !isSelected
                                setBackgroundResource(if (isSelected) viewSelect else viewDeselect)
                                setTypeface(null, if (isSelected) BOLD else NORMAL)
                            }
                        }.also {
                            selectableColumns.add(column)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClicked(view: View, attachment: FeatureAttachment) {
    }

    override fun onLongPressed(attachment: FeatureAttachment): Boolean {
        return false
    }

    override fun onStarChanged(attachment: FeatureAttachment, newValue: Boolean) {
    }

    override fun onArchived(attachment: FeatureAttachment) {
    }

    fun test() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineJob?.cancel()
    }
}
