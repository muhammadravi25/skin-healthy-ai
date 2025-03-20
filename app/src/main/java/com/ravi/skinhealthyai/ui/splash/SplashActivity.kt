package com.ravi.skinhealthyai.ui.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ravi.skinhealthyai.ui.MainActivity
import com.ravi.skinhealthyai.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMain()
        }, 3000L)
        setupView()
        setupAnimation()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupView() {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        supportActionBar?.hide()
    }

    private fun setupAnimation() {
        val firstAnimation = ObjectAnimator.ofFloat(binding.splashScreenImage, View.ALPHA, 1f).setDuration(1500)
        val endAnimation = ObjectAnimator.ofFloat(binding.splashScreenImage, View.ALPHA, 0f).setDuration(1500)
        AnimatorSet().apply {
            playSequentially(firstAnimation, endAnimation)
            start()
        }
    }
}