/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
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
import kr.djgis.sspfs.ui.feature.tabs.*
import kr.djgis.sspfs.util.glide
import kr.djgis.sspfs.util.observeOnce
import kr.djgis.sspfs.util.toggle

@DelicateCoroutinesApi
class FeatureFragment : Fragment(), View.OnClickListener {

    private val viewModel: FeatureViewModel by activityViewModels { FeatureVMFactory }

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentFeatureBinding? = null
    private val binding get() = _binding!!

    private val args: FeatureFragmentArgs by navArgs()

    private lateinit var adapter: FragmentPagerAdapter

    private val dpWidth: Boolean by lazy {
        val displayMetrics = resources.displayMetrics
        return@lazy (displayMetrics.widthPixels / displayMetrics.density) > 600.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
                activity?.onBackPressed()
            }
        })

        adapter = when (args.type) {
            "A" -> FragmentPagerAdapter(
                this@FeatureFragment, listOf(
                    FeatureAEdit1(),
                    FeatureAEdit2(),
                )
            )
            "B" -> {
                FragmentPagerAdapter(
                    this@FeatureFragment, when (args.pos) {
                        "l" -> listOf(
                            FeatureBEdit1L(),
                            FeatureBEdit2L(),
                        )
                        "m" -> listOf(
                            FeatureBEdit1M(),
                            FeatureBEdit2M(),
                        )
                        "u" -> listOf(
                            FeatureBEdit1U(),
                            FeatureBEdit2U(),
                        )
                        else -> listOf()
                    }
                )
            }
            "C" -> FragmentPagerAdapter(
                this@FeatureFragment, listOf(
                    FeatureCEdit1(),
                    FeatureCEdit2(),
                )
            )
            "D" -> FragmentPagerAdapter(
                this@FeatureFragment, listOf(
                    FeatureDEdit1(),
                    FeatureDEdit2(),
                )
            )
            "E" -> {
                FragmentPagerAdapter(
                    this@FeatureFragment, when (args.pos) {
                        "l" -> {
                            listOf(
                                FeatureFEdit1(),
                            )
                        }
                        "m" -> {
                            listOf(
                                FeatureFEdit1(),
                            )
                        }
                        "u" -> {
                            listOf(
                                FeatureFEdit1(),
                            )
                        }
                        else -> listOf(
                        )
                    }
                )
            }
            "F" -> {
                FragmentPagerAdapter(
                    this@FeatureFragment, when (args.pos) {
                        "l" -> {
                            listOf(
                                FeatureFEdit1(),
                            )
                        }
                        "m" -> {
                            listOf(
                                FeatureFEdit1(),
                            )
                        }
                        "u" -> {
                            listOf(
                                FeatureFEdit1(),
                            )
                        }
                        else -> listOf(
                        )
                    }
                )
            }
            else -> FragmentPagerAdapter(this@FeatureFragment, listOf())
        }
    }

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
            listOf(appbarLayout, collapsingToolbarLayout).forEach {
                it.layoutParams.height = if (dpWidth) 800 else 800
            }
        }

        binding.run {
            viewPager.isUserInputEnabled = false
            viewPager.adapter = adapter
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.apply {
                    val tabItem = (viewPager.adapter as FragmentPagerAdapter).tabs[position] as FeatureTabsInterface
                    text = tabItem.text
                    icon = ResourcesCompat.getDrawable(resources, tabItem.iconDrawable, null)!!
                }
            }.attach()

            (requireActivity() as MainActivity).setSupportActionBar(toolbar)
            glide(args.bitmap).into(backdrop)
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<FloatingActionButton>(R.id.fab_main)
            .toggle(true, R.color.light_green_A200, R.drawable.ic_round_save_30).setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.fab_main -> {
                viewModel.featurePost(args.type!!).observeOnce(this@FeatureFragment) {

                }
            }
        }
    }
}
