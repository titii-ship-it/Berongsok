package com.example.berongsok

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.berongsok.databinding.ActivityMainBinding
import com.example.berongsok.ui.profile.ProfileViewModel
import com.example.berongsok.ui.profile.ProfileViewModelFactory
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.local.dataStore
import com.example.berongsok.ui.login.LoginActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(SettingPreferences.getInstance(this.dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        viewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val pref = SettingPreferences.getInstance(application.dataStore)

        lifecycleScope.launch {
            pref.isLoggedIn.collect {
                if (it) {

                } else {
                    gotoLogin()
                    return@collect
                }
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_scan,  R.id.navigation_history
            )
        )
        navView.setupWithNavController(navController)
    }

    private fun gotoLogin () {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}