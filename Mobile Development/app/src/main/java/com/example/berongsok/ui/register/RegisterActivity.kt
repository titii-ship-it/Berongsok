package com.example.berongsok.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.berongsok.MainActivity
import com.example.berongsok.R
import com.example.berongsok.databinding.ActivityLoginBinding
import com.example.berongsok.databinding.ActivityRegisterBinding
import com.example.berongsok.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener { gotoLogin() }
        binding.btnSignUp.setOnClickListener { gotoMain() }
    }

    private fun gotoMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun gotoLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}