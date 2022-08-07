/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import kr.djgis.sspfs.R
import kr.djgis.sspfs.databinding.ActivityMainBinding
import java.io.File

const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"
private const val IMMERSIVE_FLAG_TIMEOUT = 500L

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.bottomAppBar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener(this@MainActivity)

//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?,
    ) {
        when (destination.id) {
            R.id.naverMapFragment -> {
                binding.apply {
                    bottomAppBar.replaceMenu(R.menu.bottomappbar_menu_fragment_map)
                    bottomAppBar.visibility = View.VISIBLE
                    bottomAppBar.performShow(true)
                    fabMain.show()
                }
                showSystemUI()
            }

            R.id.featureFragment -> {
                binding.apply {
                    bottomAppBar.replaceMenu(R.menu.bottomappbar_menu_fragment_feature)
                    bottomAppBar.visibility = View.VISIBLE
                    bottomAppBar.performShow(true)
                    fabMain.show()
                }
                showSystemUI()
            }

            R.id.placeSearchFragment -> {
                hideApplicationUI()
                showSystemUI()
            }

            else -> {}
        }
    }

    /** When key down event is triggered, relay it via local broadcast so fragments can handle it */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val intent = Intent(KEY_EVENT_ACTION).apply { putExtra(KEY_EVENT_EXTRA, keyCode) }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                true
            }

            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun hideApplicationUI() {
        binding.apply {
            if (bottomAppBar.visibility == View.VISIBLE) bottomAppBar.visibility = View.INVISIBLE
            bottomAppBar.performHide(true)
            fabMain.hide()
        }
    }

    private fun hideSystemUI() {
        binding.navHostFragment.postDelayed({
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, binding.navHostFragment).let {
                it.hide(systemBars())
                it.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }, IMMERSIVE_FLAG_TIMEOUT)
    }

    private fun showSystemUI() {
        binding.navHostFragment.postDelayed({
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(window, binding.navHostFragment).show(systemBars())
        }, IMMERSIVE_FLAG_TIMEOUT)
    }

    override fun onPause() {
        try {
            super.onPause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding.fabMain.isEnabled = true
    }

    companion object {

        /** Use external media if it is available, our app's file directory otherwise */
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists()) mediaDir else appContext.filesDir
        }
    }
}
