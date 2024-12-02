package com.example.berongsok

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.berongsok.databinding.ActivityMainBinding
import com.example.berongsok.ui.profile.ProfileViewModel
import com.example.berongsok.ui.profile.ProfileViewModelFactory
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.local.dataStore
import com.example.berongsok.ui.dashboard.DashboardFragment
import com.example.berongsok.ui.history.HistoryFragment
import com.example.berongsok.ui.login.LoginActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_dashboard, R.id.navigation_scan,  R.id.navigation_history
//            )
//        )
//        navView.setupWithNavController(navController)

        val openHistoryFragment = intent.getBooleanExtra("openHistoryFragment", false)

        if (openHistoryFragment) {
            // If the flag is true, navigate to the HistoryFragment
            navController.navigate(R.id.navigation_history)
        } else {
            // Normal navigation setup
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_dashboard, R.id.navigation_scan, R.id.navigation_history
                )
            )
            navView.setupWithNavController(navController)
        }
    }

    private fun gotoLogin () {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}