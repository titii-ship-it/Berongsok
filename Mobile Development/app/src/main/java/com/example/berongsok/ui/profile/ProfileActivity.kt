package com.example.berongsok.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.berongsok.MainActivity
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.local.dataStore
import com.example.berongsok.databinding.ActivityProfileBinding
import com.example.berongsok.ui.login.LoginActivity
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val switchTheme = binding.switchTheme

        val pref = SettingPreferences.getInstance(application.dataStore)
        val profileViewModel = ViewModelProvider(this, ProfileViewModelFactory(pref)).get(
            ProfileViewModel::class.java
        )

        profileViewModel.getThemeSettings().observe(this){ isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }

        lifecycleScope.launch {
            pref.tpsName.collect {
                binding.tvUsername.text = it
                Log.d("PREF TPS NAME", "tpsName: $it")
            }
        }

        lifecycleScope.launch {
            pref.tpsEmail.collect {
                binding.tvEmail.text = it
                Log.d("PREF TPS EMAIL", "tpsEmail: $it")
            }
        }

        switchTheme.setOnCheckedChangeListener { _:CompoundButton, isChecked: Boolean ->
            profileViewModel.saveThemeSetting(isChecked)
        }


        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                userLogout()
            }
        }


    }

    private suspend fun userLogout () {
        val pref = SettingPreferences.getInstance(application.dataStore)
        pref.clearUser()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}