package com.example.berongsok.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.berongsok.MainActivity
import com.example.berongsok.databinding.ActivityLoginBinding
import com.example.berongsok.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnSignIn.setOnClickListener{ gotoMain() }

        binding.register.setOnClickListener { gotoRegister() }
    }

    private fun gotoMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun gotoRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}