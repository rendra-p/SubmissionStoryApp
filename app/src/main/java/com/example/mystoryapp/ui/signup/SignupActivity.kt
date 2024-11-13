package com.example.mystoryapp.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.R
import com.example.mystoryapp.data.Injection
import com.example.mystoryapp.databinding.ActivitySignupBinding
import com.example.mystoryapp.ui.welcome.WelcomeActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menggunakan Injection object
        val viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[SignupViewModel::class.java]

        setupView()
        setupAction()
        playAnimation()
        observeRegistrationResult()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            // Validasi input
            if (validateInput(name, email, password)) {
                viewModel.registerUser(name, email, password)
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.signupButton.isEnabled = !isLoading
            if (isLoading) {
                binding.signupButton.text = getString(R.string.loading)
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.signupButton.text = getString(R.string.signup)
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.nameEditTextLayout.error = "Name cannot be empty"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.emailEditTextLayout.error = "Email cannot be empty"
            isValid = false
        }

        if (password.isEmpty() || password.length < 8) {
            binding.passwordEditTextLayout.error = "Password must be at least 8 characters"
            isValid = false
        }

        return isValid
    }

    private fun observeRegistrationResult() {
        viewModel.registrationResult.observe(this) { result ->
            result.onSuccess { response ->
                if (response.error == false) {
                    // Registrasi berhasil
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                } else {
                    // Registrasi gagal
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }.onFailure { exception ->
                // Tangani error jaringan atau lainnya
                Toast.makeText(this, "Registration failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEdit = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, name, nameEdit, email, emailEdit, password, passwordEdit, signup)
            start()
        }
    }
}