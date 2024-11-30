package com.example.berongsok.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.berongsok.MainActivity
import com.example.berongsok.R
import com.example.berongsok.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animasi mendekat ke tengah
        val imageMoveToCenter = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        ).apply {
            duration = 1000
        }

        val textMoveToCenter = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        ).apply {
            duration = 1000
        }

        // Animasi menjauh ke arah berlawanan
        val imageMoveAway = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        ).apply {
            duration = 1000
            startOffset = 3000 // Mulai setelah animasi mendekat selesai
        }

        val textMoveAway = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        ).apply {
            duration = 1000
            startOffset = 3000 // Mulai setelah animasi mendekat selesai
        }

        // Gabungkan animasi untuk ImageView
        val imageAnimationSet = AnimationSet(true).apply {
            addAnimation(imageMoveToCenter)
            addAnimation(imageMoveAway)
            fillAfter = true
        }

        // Gabungkan animasi untuk TextView
        val textAnimationSet = AnimationSet(true).apply {
            addAnimation(textMoveToCenter)
            addAnimation(textMoveAway)
            fillAfter = true
        }

        // Mulai animasi
        binding.ivSplash.startAnimation(imageAnimationSet)
        binding.tvSplash.startAnimation(textAnimationSet)

        // Pindah ke HomeActivity setelah semua animasi selesai
        imageAnimationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

    }
}