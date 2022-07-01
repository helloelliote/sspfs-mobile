/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.tabs

import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Job
import kr.djgis.sspfs.R
import kr.djgis.sspfs.data.FeatureAttachment
import kr.djgis.sspfs.model.FeatureVMFactory
import kr.djgis.sspfs.model.FeatureViewModel
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentAdapter
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentAdapterListener
import kr.djgis.sspfs.util.snackbar

open class FeatureTabs : Fragment(), FeatureAttachmentAdapterListener {

    val viewModel: FeatureViewModel by activityViewModels { FeatureVMFactory }
    private val viewSelect by lazy { return@lazy R.drawable.tablelayout_border_row_select }
    private val viewDeselect by lazy { return@lazy R.drawable.tablelayout_border_row_deselect }

    var coroutineJob: Job? = null
    lateinit var featureAttachmentAdapter: FeatureAttachmentAdapter

    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fab = requireActivity().findViewById(R.id.fab_main)
    }

    fun setTableLayoutOnClickListener(table: TableLayout) {
        try {
            val vm = viewModel.value()
            val rowCount = table.childCount
            for (i: Int in 0..rowCount) {
                val row = table.getChildAt(i) as TableRow
                val columnCount = row.childCount
                if (columnCount < 3) continue
                var key = ""
                for (j: Int in 0..columnCount) {
                    when (val column = row.getChildAt(j)) {
                        is Button -> {
                            if (column.tag != null) {
                                key = column.tag.toString()
                                println("Button TAG!: $key")
                            }
                        }
                        is TextView -> {
                            if (column.isClickable) {
                                column.tag = (j - 1).toString()
                                println(column.isSelected)
                                column.setOnClickListener {
                                    it as TextView
                                    println("Text TAG!: " + column.tag)
                                    it.isSelected = it.isSelected.not()
                                    println("$key at ${column.tag}: ${it.isSelected}")
                                    if (it.isSelected) {
                                        if (vm.getByKey(key) == null) {
                                            it.setBackgroundResource(viewSelect)
                                            it.setTypeface(null, BOLD)
                                            vm.setByKey(key, column.tag)
                                        } else {
                                            it.isSelected = it.isSelected.not()
                                            snackbar(fab, "앞서 선택된 항목부터 선택 해제해 주세요").setAction("확인") {

                                            }.show()
                                        }
                                    } else {
                                        it.setBackgroundResource(viewDeselect)
                                        it.setTypeface(null, NORMAL)
                                        if (vm.getByKey(key) == column.tag) {
                                            vm.setByKey(key, null)
                                        }
                                    }
                                }
                            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineJob?.cancel()
    }
}
