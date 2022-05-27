/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature

import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kr.djgis.sspfs.R

@DelicateCoroutinesApi
class FragmentPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments: List<Fragment> = FeatureFragmentTabs.values().map { it.fragment }

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    /**
     * 소규모 공공시설 정보 탭 목록
     *
     * 필요한 Fragment 를 생성 후 추가해준다.
     * (주의) ENUM 의 순서가 탭의 순서를 결정함
     * @see FeatureFragmentEdit: 소규모 공공시설 대장
     * @see FeatureFragmentAssess: 소규모 공공시설 위험도 평가
     */
    enum class FeatureFragmentTabs(
        val fragment: Fragment,
        val text: String,
        @DrawableRes val icon: Int,
    ) {
        EDIT(FeatureFragmentEdit(), "시설물 대장", R.drawable.ic_round_description_24),
        ASSESS(FeatureFragmentAssess(), "위험도 평가", R.drawable.ic_round_warning_24),
        ;
    }
}
