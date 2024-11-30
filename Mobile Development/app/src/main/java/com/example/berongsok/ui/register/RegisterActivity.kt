package com.example.berongsok.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.berongsok.ui.component.OTPEditText
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

            Log.d("Akun TPS", "email: $email")
            Log.d("Akun TPS", "password: $password")
            Log.d("Akun TPS", "tpsName: $tpsName")

            if (email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() && tpsName.isNotBlank()) {
                if (password == confirmPassword) {
                    registerViewModel.register(tpsName, email, password)
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
                val email = binding.edRegisterEmail.getEmail()
                if (!response.error) {
                    val intent = Intent(this, VerifyOTPActivity::class.java)
                    intent.putExtra(VerifyOTPActivity.EXTRA_EMAIL, email)
                    startActivity(intent)
                    showToast(response.message)
                } else {
                    showErrorDialog(response.message)
                }
            }
            result.onFailure { throwable ->
                throwable.localizedMessage?.let {
                    val email = binding.edRegisterEmail.getEmail()
                    if (it == this.getString(R.string.check_your_otp_code)) {
                        showToast(it)
                        intent.putExtra(VerifyOTPActivity.EXTRA_EMAIL, email)
                        startActivity(intent)
                    } else {
                        showErrorDialog(it)
                    }

                    showLoading(false)
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun gotoLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
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