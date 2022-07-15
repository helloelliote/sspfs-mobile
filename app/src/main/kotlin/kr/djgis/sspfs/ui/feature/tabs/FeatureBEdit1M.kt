/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.djgis.sspfs.databinding.FragmentFeatureBEdit1MBinding

class FeatureBEdit1M : FeatureTabs() {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentFeatureBEdit1MBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeatureBEdit1MBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = super.viewModel
        }

        binding.run {
            setTableLayoutOnClickListener(fac_typ = "B", table = table1)
        }

        viewModel.featureB.observe(viewLifecycleOwner) {

        }
    }

    override var text = "중점부 A"

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
