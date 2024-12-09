package com.example.berongsok

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.berongsok.databinding.ActivityMainBinding
import com.example.berongsok.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Set up navigation with BottomNavigationView
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_scan, R.id.navigation_history
            )
        )
        navView.setupWithNavController(navController)

        // Navigate to HistoryFragment if the flag is true
        val openHistoryFragment = intent.getBooleanExtra("openHistoryFragment", false)
        if (openHistoryFragment) {
            // Use `setSelectedItemId` to sync with BottomNavigationView
            navView.selectedItemId = R.id.navigation_history
        }
    }

    private fun gotoLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
