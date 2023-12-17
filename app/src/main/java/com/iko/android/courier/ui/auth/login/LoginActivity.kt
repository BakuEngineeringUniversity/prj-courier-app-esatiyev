package com.iko.android.courier.ui.auth.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.iko.android.courier.R
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.ui.auth.signup.RegisterActivity
import com.iko.android.courier.ui.main.MainActivity
import com.iko.android.courier.viewmodel.LoginViewModel
import com.iko.android.courier.viewmodel.factory.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        window.navigationBarColor = resources.getColor(R.color.md_white_1000)

        viewModel = ViewModelProvider(this, LoginViewModelFactory(RetrofitInstance.apiService))[LoginViewModel::class.java]

        emailEditText = findViewById(R.id.input_email)
        passwordEditText = findViewById(R.id.input_password)
        loginButton = findViewById(R.id.btn_login)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginUser(email, password)
            } else {
                // Handle empty email or password
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loginResult.observe(this, Observer { success ->
            if (success) {
                // Login successful, navigate to the main activity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // Login failed, show a toast
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        })

    }


    fun onRegisterOptionClick(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun togglePassword(view: View) {
        val image = findViewById<ImageView>(R.id.img_password)

        val passwordEditText = findViewById<EditText>(R.id.input_password)
        val currentTransformationMethod = passwordEditText.transformationMethod

        // Toggle between visible and password transformations
        if (currentTransformationMethod == null) {
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            image.background = ContextCompat.getDrawable(this, R.drawable.visibility_off_24)
        } else {
            passwordEditText.transformationMethod = null // This removes the transformation, making the text visible
            image.background = ContextCompat.getDrawable(this, R.drawable.visibility_24)
        }


        // Move the cursor to the end of the text
        passwordEditText.setSelection(passwordEditText.text.length)
    }


}