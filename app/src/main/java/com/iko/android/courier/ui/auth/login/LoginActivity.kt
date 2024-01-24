package com.iko.android.courier.ui.auth.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.iko.android.courier.R
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.ui.auth.signup.RegisterActivity
import com.iko.android.courier.ui.main.MainActivity
import com.iko.android.courier.viewmodel.LoginViewModel
import com.iko.android.courier.viewmodel.factory.LoginViewModelFactory
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.JWTParser

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        window.navigationBarColor = resources.getColor(R.color.md_white_1000)

        viewModel = ViewModelProvider(this, LoginViewModelFactory(RetrofitInstance.apiService))[LoginViewModel::class.java]

        emailEditText = findViewById(R.id.input_email)
        passwordEditText = findViewById(R.id.input_password)
        loginButton = findViewById(R.id.btn_login)

        // Check if the user is already logged in
        if (isLoggedIn()) {
            // User is already logged in, navigate to the main activity
            fetchAndSetupUserInfo()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // Set up login logic only if the user is not logged in
            setupLoginLogic()
        }

    }

    private fun isLoggedIn(): Boolean {
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val sharedEmail = sharedPreferences.getString("EMAIL", "")
        val sharedPassword = sharedPreferences.getString("PASSWORD", "")
        return sharedEmail?.isNotEmpty() == true && sharedPassword?.isNotEmpty() == true
    }

    private fun setupLoginLogic() {
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
                // Set the user as logged in after UserManager.id is correctly set
                setLoggedIn()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // Login failed, show a toast
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setLoggedIn() {
        // Use SharedPreferences to set the user as logged in
        val editor = sharedPreferences.edit()
        editor.putString("EMAIL", emailEditText.text.toString())
        editor.putString("PASSWORD", passwordEditText.text.toString())
        UserManager.id = extractSubjectFromJwt(UserManager.accessToken)
        editor.putLong("ID", UserManager.id?:0L)
        editor.apply()
    }

    private fun fetchAndSetupUserInfo() {
        val id = sharedPreferences.getLong("ID", 0L)

        if (id != 0L) {
            // Call the function in your ViewModel to fetch user info based on email and password
            viewModel.fetchUserInfo(id)
        }
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

    private fun extractSubjectFromJwt(jwtToken: String?): Long? {
        try {
            // Parse the JWT token
            val jwt: JWT = JWTParser.parse(jwtToken)

            // Get the JWT claims set
            val jwtClaimsSet: JWTClaimsSet = jwt.jwtClaimsSet

            // Extract subject (sub) claim
            return jwtClaimsSet.subject.toLong()
        } catch (e: Exception) {
            // Handle exceptions, e.g., log the error
            println("Error extracting subject from JWT: ${e.message}")
        }
        return null
    }


}