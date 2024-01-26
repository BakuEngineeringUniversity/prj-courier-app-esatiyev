package com.iko.android.courier.ui.settings

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.lifecycleScope
import com.iko.android.courier.R
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.data.model.Customer
import com.iko.android.courier.databases.AppDatabase
import com.iko.android.courier.databases.entities.User
import com.iko.android.courier.ui.auth.login.LoginActivity
import com.iko.android.courier.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class SettingsActivity : AppCompatActivity() {
    private var newName: String? = null
    private var newSurname: String? = null
    private var newPhone: String? = null
    private var newEmail: String? = null
    private var newPassword: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    fun saveChanges(view: View) {
        val btnSaveChanges = findViewById<TextView>(R.id.btn_save_changes)
        btnSaveChanges.isClickable = false

        var nameTxt = findViewById<EditText>(R.id.input_firstname_settings).text
        var surnameTxt = findViewById<EditText>(R.id.input_surname_settings).text
        var phoneTxt = findViewById<EditText>(R.id.input_phone_settings).text
        var emailTxt = findViewById<EditText>(R.id.input_email_settings).text
        var passwordTxt = findViewById<EditText>(R.id.input_password_settings).text


        if (nameTxt.isNotBlank()) newName = nameTxt.toString()
        if (surnameTxt.isNotBlank()) newSurname = surnameTxt.toString()
        if (phoneTxt.isNotBlank()) newPhone = phoneTxt.toString()
        if (emailTxt.isNotBlank()) newEmail = emailTxt.toString()
        if (passwordTxt.isNotBlank()) newPassword = passwordTxt.toString()

        if (newPassword != null && passwordTxt.length < 6) {
            Toast.makeText(
                applicationContext,
                "Password must be at least 6 characters",
                Toast.LENGTH_SHORT
            ).show()
            btnSaveChanges.isClickable = true
            return
        } else if (newPhone != null && !phoneTxt.isDigitsOnly()) {
            Toast.makeText(applicationContext, "Phone number must be a number", Toast.LENGTH_SHORT)
                .show()
            btnSaveChanges.isClickable = true
            return
        } else if (newPhone != null && phoneTxt.length != 10) {
            Toast.makeText(applicationContext, "Phone number must be 10 digits", Toast.LENGTH_SHORT)
                .show()
            btnSaveChanges.isClickable = true
            return
        }


        val updatedCustomer = Customer(
            firstname = newName,
            lastname = newSurname,
            phone = newPhone,
            email = newEmail,
            password = newPassword
        )


        val apiService = RetrofitInstance.apiService
        lifecycleScope.launch {
            try {
                val response = apiService.updateCustomer(UserManager.id ?: 0, updatedCustomer)
                if (response.isSuccessful) {
                    updateDatabase(updatedCustomer)
                    Toast.makeText(applicationContext, "Changes saved!", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle error response
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrBlank()) {
                        errorBody
                    } else {
                        response.message() // Fallback to response message if errorBody is null or blank
                    }

                    Toast.makeText(
                        applicationContext,
                        "Error saving changes: $errorMessage",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("SettingsActivity", "Error saving changes: $errorMessage")
                }
                nameTxt = null
                surnameTxt = null
                phoneTxt = null
                emailTxt = null
                passwordTxt = null

                btnSaveChanges.isClickable = true
            } catch (e: Exception) {
                btnSaveChanges.isClickable = true
                Toast.makeText(
                    applicationContext,
                    "Error saving changes: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("SettingsActivity", "Error creating package: ${e.message}")
            }
        }


    }

    private fun updateDatabase(updatedCustomer: Customer) {

        // Update the user data in the Room database
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val userDao = AppDatabase.getDatabase(applicationContext).userDao()
                val user = userDao.getAllUsers().firstOrNull()

                if (user == null) {
                    signOut()
                } else {
                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    if (updatedCustomer.firstname != null) {
                        user.firstname = updatedCustomer.firstname
                        UserManager.firstname = updatedCustomer.firstname
                    }
                    if (updatedCustomer.lastname != null) {
                        user.lastname = updatedCustomer.lastname
                        UserManager.lastname = updatedCustomer.lastname
                    }
                    if (updatedCustomer.phone != null) {
                        user.phone = updatedCustomer.phone
                        UserManager.phone = updatedCustomer.phone
                    }
                    if (updatedCustomer.email != null) {
                        user.email = updatedCustomer.email
                        UserManager.email = updatedCustomer.email

                        editor.putString("EMAIL", updatedCustomer.email)
                    }
                    if (updatedCustomer.password != null) {
                        user.password = updatedCustomer.password
                        UserManager.password = updatedCustomer.password

                        editor.putString("PASSWORD", updatedCustomer.password)
                    }

                    editor.apply()

                    userDao.updateUser(user)

                    Log.d("Settings: UserDao", "User updated successfully $user")
                    Log.d(
                        "Settings: UserManager",
                        "User updated successfully. email: ${UserManager.email} password: ${UserManager.password} phone: ${UserManager.phone} firstname: ${UserManager.firstname} lastname: ${UserManager.lastname} isCourier: ${UserManager.id} ${UserManager.isCourier}"
                    )
                }
            }
        }


    }

    private fun signOut() {
        // Clear user session data
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Clear the database
        AppDatabase.clearDatabase(applicationContext)

        // Reset UserManager variables
        UserManager.id = null

        UserManager.firstname = null
        UserManager.lastname = null
        UserManager.username = null
        UserManager.email = null
        UserManager.password = null
        UserManager.fin = null
        UserManager.serialNo = null
        UserManager.age = null
        UserManager.gender = null
        UserManager.phone = null
        UserManager.address = null
        UserManager.packages = null
        UserManager.ordersNumber = null
        UserManager.expenses = null
        UserManager.isCourier = false
        UserManager.deliversNumber = null
        UserManager.rating = null
        UserManager.deliveriesPackages = null
        UserManager.reviews = null
        UserManager.accessToken = null
        UserManager.refreshToken = null
        // Reset UserManager variables

        Toast.makeText(applicationContext, "Sign Out", Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun backToProfile(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("selectedAction", "action_profile")
        startActivity(intent)
        finish()
    }

    fun togglePasswordSettings(view: View) {
        val image = findViewById<ImageView>(R.id.img_password_settings)

        val passwordEditText = findViewById<EditText>(R.id.input_password_settings)
        val currentTransformationMethod = passwordEditText.transformationMethod

        // Toggle between visible and password transformations
        if (currentTransformationMethod == null) {
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            image.background = ContextCompat.getDrawable(this, R.drawable.visibility_off_24)
        } else {
            passwordEditText.transformationMethod =
                null // This removes the transformation, making the text visible
            image.background = ContextCompat.getDrawable(this, R.drawable.visibility_24)
        }


        // Move the cursor to the end of the text
        passwordEditText.setSelection(passwordEditText.text.length)
    }

}