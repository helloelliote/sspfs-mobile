/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.util

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.DisplayCutout
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.widget.ImageButton
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import kr.djgis.sspfs.Config.ANIMATION_FAST_MILLIS

/** Combination of all flags required to put activity into immersive mode */
const val FLAGS_FULLSCREEN =
    View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

fun View.color(@ColorRes color: Int): ColorStateList {
    return ResourcesCompat.getColorStateList(resources, color, null)!!
}

fun View.drawable(@DrawableRes drawable: Int): Drawable {
    return ResourcesCompat.getDrawable(resources, drawable, null)!!
}

/**
 * Simulate a button click, including a small delay while it is being pressed to trigger the
 * appropriate animations.
 */
fun ImageButton.simulateClick(delay: Long = ANIMATION_FAST_MILLIS) {
    performClick()
    isPressed = true
    invalidate()
    postDelayed({
        invalidate()
        isPressed = false
    }, delay)
}

/** Pad this view with the insets provided by the device cutout (i.e. notch) */
@RequiresApi(Build.VERSION_CODES.P)
fun View.padWithDisplayCutout() {
    /** Helper method that applies padding from cutout's safe insets */
    fun doPadding(cutout: DisplayCutout) =
        setPadding(cutout.safeInsetLeft, cutout.safeInsetTop, cutout.safeInsetRight, cutout.safeInsetBottom)
    // Apply padding using the display cutout designated "safe area"
    rootWindowInsets?.displayCutout?.let { doPadding(it) }
    // Set a listener for window insets since view.rootWindowInsets may not be ready yet
    setOnApplyWindowInsetsListener { _, insets ->
        insets.displayCutout?.let { doPadding(it) }
        insets
    }
}

/** Same as [AlertDialog.show] but setting immersive mode in the dialog's window */
fun AlertDialog.showImmersive() {
    // Set the dialog to not focusable
    window?.setFlags(FLAG_NOT_FOCUSABLE, FLAG_NOT_FOCUSABLE)
    // Make sure that the dialog's window is in full screen
    window?.decorView?.systemUiVisibility = FLAGS_FULLSCREEN
    // Show the dialog while still in immersive mode
    show()
    // Set the dialog to focusable again
    window?.clearFlags(FLAG_NOT_FOCUSABLE)
}
