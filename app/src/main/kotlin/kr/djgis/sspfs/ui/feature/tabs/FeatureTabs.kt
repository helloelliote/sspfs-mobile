/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.tabs

import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Job
import kr.djgis.sspfs.R
import kr.djgis.sspfs.model.FeatureVMFactory
import kr.djgis.sspfs.model.FeatureViewModel
import kr.djgis.sspfs.util.snackbar

open class FeatureTabs : Fragment(), FeatureTabsInterface {

    val viewModel: FeatureViewModel by activityViewModels { FeatureVMFactory }
    private val viewSelect by lazy { return@lazy R.drawable.tablelayout_border_row_select }
    private val viewDeselect by lazy { return@lazy R.drawable.tablelayout_border_row_deselect }

    var coroutineJob: Job? = null

    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fab = requireActivity().findViewById(R.id.fab_main)
    }

    fun setTableLayoutOnClickListener(fac_typ: String, table: TableLayout) {
        try {
            val vm = viewModel.of(fac_typ)
            val rowCount = table.childCount
            for (i: Int in 0..rowCount) {
                val row = table.getChildAt(i)
                if (row !is TableRow) continue
                val columnCount = row.childCount
                if (columnCount < 3) continue
                var key = ""
                for (j: Int in 0..columnCount) {
                    when (val column = row.getChildAt(j)) {
                        is Button -> {
                            column.tag?.let { key = it.toString() }
                        }

                        is TextView -> {
                            if (column.isClickable) {
                                vm.getByKey(key)?.let { value ->
                                    when (value) {
                                        is String -> row.setSelect(value)
                                        is MutableSet<*> -> value.forEach { row.setSelect(it.toString()) }
                                    }
                                }
                                column.setOnClickListener {
                                    it as TextView
                                    it.isSelected = it.isSelected.not()
                                    if (it.isSelected) {
                                        if (vm.getByKey(key) == null) {
                                            it.setBackgroundResource(viewSelect)
                                            it.setTypeface(null, BOLD)
                                            vm.setByKey(key, column.tag)
                                        } else {
                                            when (vm.getByKey(key)) {
                                                is String -> {
                                                    it.isSelected = it.isSelected.not()
                                                    snackbar(fab, "먼저 선택된 항목부터 선택 해제해 주세요").setAction("확인") {}.show()
                                                }

                                                is MutableSet<*> -> {
                                                    it.setBackgroundResource(viewSelect)
                                                    it.setTypeface(null, BOLD)
                                                    (vm.getByKey(key) as MutableSet<String>).add(column.tag as String)
                                                }

                                                else -> {

                                                }
                                            }
                                        }
                                    } else {
                                        it.setBackgroundResource(viewDeselect)
                                        it.setTypeface(null, NORMAL)
                                        when (vm.getByKey(key)) {
                                            is String -> {
                                                if (vm.getByKey(key) == column.tag) vm.setByKey(key, null)
                                            }

                                            is MutableSet<*> -> {
                                                (vm.getByKey(key) as MutableSet<String>).remove(column.tag)
                                                if ((vm.getByKey(key) as MutableSet<String>).size == 0) {
                                                    vm.setByKey(key, null)
                                                }
                                                println(vm.getByKey(key))
                                            }

                                            else -> {

                                            }
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

    private fun TableRow.setSelect(tag: String) {
        val selection = this.findViewWithTag<TextView>(tag)
        selection.setBackgroundResource(viewSelect)
        selection.setTypeface(null, BOLD)
        selection.isSelected = true
    }

    override var text = "조사"

    override var iconDrawable = R.drawable.ic_round_description_24

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineJob?.cancel()
    }
}
