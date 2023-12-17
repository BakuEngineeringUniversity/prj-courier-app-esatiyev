package com.iko.android.courier.ui.auth.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.iko.android.courier.R
import com.iko.android.courier.ui.auth.login.LoginActivity
import com.iko.android.courier.viewmodel.RegistrationViewModel
import com.iko.android.courier.api.Result
import androidx.lifecycle.ViewModelProvider
import com.iko.android.courier.api.RetrofitInstance.apiService
import com.iko.android.courier.viewmodel.factory.RegistrationViewModelFactory
import com.iko.android.courier.data.model.Customer
import java.util.UUID
import kotlin.random.Random

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: RegistrationViewModel

    var firstname : String? = null
    var lastname : String? = null
    var email : String? = null
    var password : String? = null
    var confirmPassword : String? = null
    var phone : String? = null
    var age : String? = null
    var gender : String? = null
    private lateinit var selectedGender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        window.navigationBarColor = resources.getColor(R.color.md_white_1000)

        val spinner: Spinner = findViewById(R.id.spinnerGender)
        val genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_options, android.R.layout.simple_spinner_item)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = genderAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                selectedGender = parentView?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing here
            }
        }


        val registrationViewModelFactory = RegistrationViewModelFactory(apiService)
        viewModel = ViewModelProvider(this, registrationViewModelFactory).get(RegistrationViewModel::class.java)
    }


    fun SignupClick(view: View) {
        firstname = findViewById<EditText>(R.id.input_firstname).text.toString()
        lastname = findViewById<EditText>(R.id.input_lastname).text.toString()
        email = findViewById<EditText>(R.id.input_email_rgstr).text.toString()
        password = findViewById<EditText>(R.id.input_password_rgstr).text.toString()
        confirmPassword = findViewById<EditText>(R.id.input_confirm_password).text.toString()
        phone = findViewById<EditText>(R.id.input_phone).text.toString()
        age = findViewById<EditText>(R.id.input_age).text.toString()

        gender = selectedGender

        if (firstname.isNullOrBlank() || lastname.isNullOrBlank() || email.isNullOrBlank()
            || password.isNullOrBlank() || confirmPassword.isNullOrBlank()
            || phone.isNullOrBlank() || age.isNullOrBlank() || gender.isNullOrBlank()) {

            Toast.makeText(applicationContext, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        else if (password != confirmPassword) {
            Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        } else if (password!!.length < 6) {
            Toast.makeText(applicationContext, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        } else if (!phone!!.isDigitsOnly()) {
            Toast.makeText(applicationContext, "Phone number must be a number", Toast.LENGTH_SHORT).show()
            return
        } else if (phone!!.length != 10) {
            Toast.makeText(applicationContext, "Phone number must be 10 digits", Toast.LENGTH_SHORT)
                .show()
            return
        } else if (!age!!.isDigitsOnly()) {
            Toast.makeText(applicationContext, "Age must be a number", Toast.LENGTH_SHORT).show()
            return
        } else if (age!!.toInt() < 18) {
            Toast.makeText(applicationContext, "Age must be at least 18", Toast.LENGTH_SHORT).show()
            return
        }

        val customer = Customer(
            firstname = firstname,
            lastname = lastname,
            username = UUID.randomUUID().toString().substring(1, 5), // Add other customer details here
            email = email,
            password = password,
            fin = Random.nextInt(1000).toString(),
            serialNo = Random.nextInt(1000).toString(),
            age = age?.toIntOrNull(),
            gender = gender,
            phone = phone,
            address = "123 Main St",
        )

        viewModel.registerCustomer(customer)

        // Observe the registrationResult LiveData for the result
        viewModel.registrationResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    // Registration successful, handle the response
                    val registeredCustomer = result.data
                    Toast.makeText(
                        applicationContext,
                        "Registration successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Result.Error -> {
                    // Registration failed, handle the error
                    val error = result.exception.message
                    Toast.makeText(
                        applicationContext,
                        "Registration failed1: $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun onLoginOptionClick(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun togglePassword(view: View) {
        val image = findViewById<ImageView>(R.id.img_password_rgstr)

        val passwordEditText = findViewById<EditText>(R.id.input_password_rgstr)
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
    fun toggleConfirmPassword(view: View) {
        val image = findViewById<ImageView>(R.id.img_confirm_password)

        val passwordEditText = findViewById<EditText>(R.id.input_confirm_password)
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
