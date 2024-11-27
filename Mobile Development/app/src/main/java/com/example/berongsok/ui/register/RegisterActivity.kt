package com.example.berongsok.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.berongsok.MainActivity
import com.example.berongsok.R
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.local.dataStore
import com.example.berongsok.databinding.ActivityLoginBinding
import com.example.berongsok.databinding.ActivityRegisterBinding
import com.example.berongsok.ui.login.LoginActivity
import com.example.berongsok.utils.Injection
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(Injection.provideUserRepository())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener { gotoLogin() }

        showLoading(false)

        binding.btnSignUp.setOnClickListener {
            val password = binding.edRegisterPassword.getPassword()
            val email = binding.edRegisterEmail.getEmail()
            val confirmPassword = binding.edConfirmPassword.text.toString()
            val tpsName = binding.edRegisterUsername.text.toString()

            if (email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() && tpsName.isNotBlank()) {
                if (password == confirmPassword) {
                    registerViewModel.register(email, tpsName, password)
                } else {
                    Toast.makeText(this, R.string.password_mismatch, Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, R.string.fields_required, Toast.LENGTH_SHORT).show()
            }
        }

        registerViewModel.loading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        registerViewModel.registerResult.observe(this) { result ->
            result.onSuccess { response ->
                if (!response.error) {
                    showRegistrationDialog(response.message)
                } else {
                    showErrorDialog(response.message)
                }
            }
            result.onFailure { throwable ->
                throwable.localizedMessage?.let {
                    showErrorDialog(it)
                    showLoading(false)
                }
            }
        }
    }

    private fun gotoMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun gotoLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun showRegistrationDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.succes_regist)
            .setMessage(message)
            .setPositiveButton(R.string.continue_login) { dialog, _ ->
                goToLogin()
            }
            .show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.failed_regist)
            .setMessage(message)
            .setPositiveButton(R.string.retry) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}