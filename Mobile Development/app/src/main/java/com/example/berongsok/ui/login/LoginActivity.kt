package com.example.berongsok.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.berongsok.MainActivity
import com.example.berongsok.databinding.ActivityLoginBinding
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.local.dataStore
import com.example.berongsok.ui.register.RegisterActivity
import com.example.berongsok.utils.Injection
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(Injection.provideUserRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        showLoading(false)

        binding.btnSignIn.setOnClickListener {
            val password = binding.edLoginPassword.getPassword()
            val email = binding.edLoginEmail.getEmail()

            if (email.isNotBlank() && password.isNotBlank()) {
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.loading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        val pref = SettingPreferences.getInstance(application.dataStore)

        loginViewModel.loginResult.observe(this) { result ->
            result.onSuccess { response ->
                if (!response.error) {
                    lifecycleScope.launch {
                        Log.d("Login Response", response.loginResult.toString())
                        pref.saveUser(response.loginResult, true)
                    }
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    gotoMain()
                } else {
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
            result.onFailure { throwable ->
                Toast.makeText(this, throwable.localizedMessage ?: "An error occurred", Toast.LENGTH_SHORT).show()
            }
        }

        binding.register.setOnClickListener { gotoRegister() }

        binding.forgotPassword.setOnClickListener { gotoForgotPassword() }
    }

    private fun gotoMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun gotoRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun gotoForgotPassword() {
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}