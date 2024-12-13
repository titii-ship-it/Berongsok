package com.example.berongsok.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.berongsok.MainActivity
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.local.dataStore
import com.example.berongsok.databinding.ActivitySplashScreenBinding
import com.example.berongsok.ui.login.LoginActivity
import com.example.berongsok.ui.profile.ProfileViewModel
import com.example.berongsok.ui.profile.ProfileViewModelFactory
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(SettingPreferences.getInstance(this.dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val pref = SettingPreferences.getInstance(application.dataStore)


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
            startOffset = 2000 // Mulai setelah animasi mendekat selesai
        }

        val textMoveAway = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        ).apply {
            duration = 1000
            startOffset = 2000 // Mulai setelah animasi mendekat selesai
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
                lifecycleScope.launch {
                    pref.isLoggedIn.collect {
                        if (it) {
                            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                            finish()
                        } else {
                            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                            finish()
                        }
                    }
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        viewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }


    }
}