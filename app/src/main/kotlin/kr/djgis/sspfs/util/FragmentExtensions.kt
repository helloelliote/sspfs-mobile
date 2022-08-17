/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.util

import android.content.Intent
import android.graphics.Bitmap
import android.speech.RecognizerIntent.*
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kr.djgis.sspfs.R
import java.util.*

@Suppress("DEPRECATION")
fun Fragment.screenSize(): Int {
    val displayMetrics = DisplayMetrics()
    requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels
    return if (height > width) height else width
}

fun Fragment.snackbar(anchorView: View? = null, @StringRes message: Int): Snackbar {
    return Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG).apply {
        this.anchorView = anchorView
    }
}

fun Fragment.snackbar(anchorView: View? = null, message: String?): Snackbar {
    return Snackbar.make(this.requireView(), message.toString(), Snackbar.LENGTH_INDEFINITE).apply {
        this.anchorView = anchorView
        this.setTextMaxLines(5)
        this.setAction("확인") {}
    }
}

fun Fragment.toggleFab(
    isEnabled: Boolean = true,
    @ColorRes colorInt: Int? = R.color.teal_A400,
    @DrawableRes drawableInt: Int? = R.drawable.ic_round_content_paste_search_30,
): FloatingActionButton {
    return requireActivity().findViewById<FloatingActionButton>(R.id.fab_main).apply {
        colorInt?.let { backgroundTintList = color(colorInt) }
        drawableInt?.let { setImageDrawable(drawable(drawableInt)) }
        this.isEnabled = isEnabled
    }
}

fun Fragment.alertDialog(
    @StringRes title: Int,
    @StringRes message: Int,
    @DrawableRes drawableInt: Int? = R.drawable.ic_round_help_outline_24,
): MaterialAlertDialogBuilder {
    return MaterialAlertDialogBuilder(this.requireContext(), R.style.MaterialAlertDialog__Center)
        .setIcon(ResourcesCompat.getDrawable(resources, drawableInt!!, null))
        .setTitle(resources.getString(title))
        .setMessage(resources.getString(message))
        .setPositiveButton(resources.getString(R.string.dialog_close)) { _, _ ->
            // Respond to positive button press
        }
}

fun Fragment.alertDialog(
    title: String?,
    message: String?,
    @DrawableRes drawableInt: Int? = R.drawable.ic_round_help_outline_24,
): MaterialAlertDialogBuilder {
    return MaterialAlertDialogBuilder(this.requireContext(), R.style.MaterialAlertDialog__Center)
        .setIcon(ResourcesCompat.getDrawable(resources, drawableInt!!, null))
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(resources.getString(R.string.dialog_close)) { _, _ ->
            // Respond to positive button press
        }
}

fun Fragment.glide(source: Any?, isThumbnail: Boolean = true): GlideRequest<Bitmap> {
    return GlideApp.with(this.requireContext()).asBitmap().load(source).thumbnail(if (isThumbnail) 0.25f else 1.0f)
        .transition(BitmapTransitionOptions.withCrossFade()).centerCrop()
}

fun getRecognizeSpeechIntent(prompt: String?): Intent {
    return Intent(ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM)
        putExtra(EXTRA_LANGUAGE, Locale.KOREAN)
        putExtra(EXTRA_PROMPT, prompt)
    }
}
