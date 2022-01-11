package com.example.fullscreeninsetstest

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowInsets.Type
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.fullscreeninsetstest.databinding.ActivityFullscreenBinding

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding
    private val onWindowFocusChangeListener =
        ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
            if (hasFocus) {
                setAsImmersive()
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the user interaction to manually show or hide the system UI.
        binding.enterImmersiveMode.setOnClickListener {
            setAsImmersive()
        }
        binding.enterImmersiveModeWithRestore.setOnClickListener {
            setAsImmersive()
            enableImmersiveModeRestore()
        }
        binding.exitImmersiveMode.setOnClickListener {
            exitImmersiveModeIfNeeded()
        }
    }

    private fun setAsImmersive() {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            WindowInsetsControllerCompat(this, this.decorView).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    private fun enableImmersiveModeRestore() {
        window.decorView.viewTreeObserver?.addOnWindowFocusChangeListener(onWindowFocusChangeListener)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.setOnApplyWindowInsetsListener { _, insets ->
                if (insets.isVisible(Type.statusBars())) {
                    setAsImmersive()
                }
                insets
            }
        } else {
            @Suppress("DEPRECATION") // insets.isVisible(int) is available only starting with API 30
            window.decorView.setOnSystemUiVisibilityChangeListener { newFlags ->
                if (newFlags and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    setAsImmersive()
                }
            }
        }
    }

    private fun exitImmersiveModeIfNeeded() {
        if (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON and window.attributes.flags == 0) {
            // We left immersive mode already.
            return
        }

        window.decorView.viewTreeObserver?.removeOnWindowFocusChangeListener(onWindowFocusChangeListener)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.setOnApplyWindowInsetsListener(null)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.setOnSystemUiVisibilityChangeListener(null)
        }

        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            show(WindowInsetsCompat.Type.systemBars())
        }
    }
}