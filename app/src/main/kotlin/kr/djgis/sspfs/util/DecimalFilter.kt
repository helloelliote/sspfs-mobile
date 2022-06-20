/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.util

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class DecimalFilter(beforeZero: Int, afterZero: Int) : InputFilter {

    private val pattern: Pattern =
        Pattern.compile("[0-9]{0," + (beforeZero - 1) + "}+((\\.[0-9]{0," + (afterZero - 1) + "})?)||(\\.)?")

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int,
    ): CharSequence? {
        return if (!pattern.matcher(dest as CharSequence).matches()) "" else null
    }
}
