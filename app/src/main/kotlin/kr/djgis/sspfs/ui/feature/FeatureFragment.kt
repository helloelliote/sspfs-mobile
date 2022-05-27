/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.DelicateCoroutinesApi
import kr.djgis.sspfs.R
import kr.djgis.sspfs.databinding.FragmentFeatureBinding
import kr.djgis.sspfs.model.FeatureVMFactory
import kr.djgis.sspfs.model.FeatureViewModel
import kr.djgis.sspfs.ui.MainActivity
import kr.djgis.sspfs.ui.feature.FragmentPagerAdapter.FeatureFragmentTabs
import kr.djgis.sspfs.util.glide
import kr.djgis.sspfs.util.toggle

@DelicateCoroutinesApi
class FeatureFragment : Fragment(), View.OnClickListener {

    private val viewModel: FeatureViewModel by activityViewModels { FeatureVMFactory }

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentFeatureBinding? = null
    private val binding get() = _binding!!

    private val args: FeatureFragmentArgs by navArgs()

    private val dpWidth: Boolean by lazy {
        val displayMetrics = resources.displayMetrics
        return@lazy (displayMetrics.widthPixels / displayMetrics.density) > 600.0
    }

    private val featureFragmentTabs = FeatureFragmentTabs.values()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeatureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@FeatureFragment.viewModel
            listOf(appbarLayout, collapsingToolbarLayout).forEach {
                it.layoutParams.height = if (dpWidth) 800 else 800
            }
        }

        binding.run {
            viewPager.isUserInputEnabled = false
            viewPager.adapter = FragmentPagerAdapter(this@FeatureFragment)
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.apply {
                    with(featureFragmentTabs) {
                        text = this[position].text
                        icon = getDrawable(resources, this[position].icon, null)
                    }
                }
            }.attach()

            (requireActivity() as MainActivity).setSupportActionBar(toolbar)
            glide(args.bitmap).into(backdrop)
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<FloatingActionButton>(R.id.fab_main)
            .toggle(true, R.color.red_A200, R.drawable.ic_round_question_mark_30).setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(p0: View?) {
    }
}
