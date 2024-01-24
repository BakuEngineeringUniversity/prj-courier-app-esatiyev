package com.iko.android.courier.ui.main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.iko.android.courier.R
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.data.model.Courier
import com.iko.android.courier.data.model.Customer
import com.iko.android.courier.databases.AppDatabase
import com.iko.android.courier.databases.entities.User
import com.iko.android.courier.ui.auth.login.LoginActivity
import com.iko.android.courier.ui.cargo.awaitingCourier.CourierCargoListActivity
import com.iko.android.courier.ui.cargo.create.CreateCargoActivity
import com.iko.android.courier.ui.cargo.delivers.DeliversCargoListFragment
import com.iko.android.courier.ui.cargo.ownCargo.OwnCargoListFragment
import com.iko.android.courier.ui.profile.ProfileFragment
import com.iko.android.courier.ui.terms.TermsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.IllegalFormatException
import java.util.Optional

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // R.layout.activity_main

        window.navigationBarColor = resources.getColor(R.color.colorNavigationBar)
        window.statusBarColor = resources.getColor(R.color.colorHomeBar)

        var selectedAction = intent.getStringExtra("selectedAction")
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Check if selectedAction is not null and navigate accordingly
        if (selectedAction != null) {
            when (selectedAction) {
                "action_cargo" -> {
                    selectedAction = null
                    val item: MenuItem = bottomNavigationView.menu.findItem(R.id.action_cargo)
                    item.isChecked = true
                    bottomNavigationClicked(item)
                }
                "action_delivers" -> {
                    selectedAction = null
                    val item: MenuItem = bottomNavigationView.menu.findItem(R.id.action_delivers)
                    item.isChecked = true
                    bottomNavigationClicked(item)
                }
                "action_profile" -> {
                    selectedAction = null
                    val item: MenuItem = bottomNavigationView.menu.findItem(R.id.action_profile)
                    item.isChecked = true
                    bottomNavigationClicked(item)
                }
                // Add more cases for other actions as needed
                else -> {
                    val item: MenuItem = bottomNavigationView.menu.findItem(R.id.action_home)
                    item.isChecked = true
                    bottomNavigationClicked(item)
                } // Default action
            }
        } else {
            // If selectedAction is null, fetch user from the SQLite database asynchronously
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val userDao = AppDatabase.getDatabase(applicationContext).userDao()
                    val user = userDao.getAllUsers().firstOrNull()

                    if (user != null) {
                        Log.d("MainActivity", "User found in the database: $user")
                        // Assign user data to UserManager variables
                        UserManager.id = user.userId
                        UserManager.firstname = user.firstname
                        UserManager.lastname = user.lastname
                        UserManager.username = user.username
                        UserManager.email = user.email
                        UserManager.password = user.password
                        UserManager.fin = user.fin
                        UserManager.serialNo = user.serialNo
                        UserManager.age = user.age
                        UserManager.gender = user.gender
                        UserManager.phone = user.phone
                        UserManager.address = user.address
                        UserManager.isCourier = user.isCourier
                        UserManager.ordersNumber = user.ordersNumber
                        UserManager.expenses = user.expenses

                        UserManager.accessToken = user.accessToken
                        UserManager.refreshToken = user.refreshToken
                        // ... (assign other variables)

                        // You may need to update other parts of your UI or logic based on the user data
                    } else {
                        // Handle the case when no user is found in the database
                        // You may want to show a message or perform other actions
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "No user found in the database", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            // Default action
            replaceFragment(HomeFragment())
        }



    bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            bottomNavigationClicked(item)
        }



    }


    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment).commit()
    }


    fun bottomNavigationClicked(item: MenuItem): Boolean {


        when (item.itemId) {
            R.id.action_home -> {
                replaceFragment(HomeFragment())
                window.statusBarColor = resources.getColor(R.color.colorHomeBar)
                return true
            }
            R.id.action_cargo -> {
                replaceFragment(OwnCargoListFragment())
                window.statusBarColor = resources.getColor(R.color.colorStatusBar)
                return true
            }
            R.id.action_delivers -> {
                replaceFragment(DeliversCargoListFragment())
                window.statusBarColor = resources.getColor(R.color.colorStatusBar)
                return true
            }
            R.id.action_profile -> {
                replaceFragment(ProfileFragment())
                window.statusBarColor = resources.getColor(R.color.colorStatusBar)
                return true
            }
        }
        return false
    }

    fun sendPackageClicked(view: View) {
        val intent = Intent(this, CreateCargoActivity::class.java)
        startActivity(intent)
    }

    fun courierTermsClicked(view: View) {
        val intent = Intent(this, TermsActivity::class.java)
        startActivity(intent)

    }

    fun becomeCourierClicked(view: View) {
        if (UserManager.isCourier == true) {
            val intent = Intent(this, CourierCargoListActivity::class.java)
            startActivity(intent)
        }
        else {
            showCustomDialogBox()
        }
    }



    fun signOutClicked(item: MenuItem) {
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

        Toast.makeText(applicationContext, "Sign Out Clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)

        if (currentFragment is HomeFragment) {
            // If the current fragment is HomeFragment, perform the default back action
            super.onBackPressed()
        } else {
            // If the current fragment is not HomeFragment, navigate to HomeFragment
            replaceFragment(HomeFragment())
        }
    }

    private fun showCustomDialogBox() {
        val overlayView = findViewById<View>(R.id.delivers_overlayView)
        overlayView?.visibility = View.VISIBLE

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.become_courier_custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnAgree = dialog.findViewById<Button>(R.id.btnAgree)
        val btnDisagree = dialog.findViewById<Button>(R.id.btnDisagree)


        btnAgree.setOnClickListener {
            val apiService = RetrofitInstance.apiService

            val newCourier = Courier(
                id = UserManager.id,
                firstname = UserManager.firstname,
                lastname = UserManager.lastname,
                username = UserManager.username,
                email = UserManager.email,
                password = UserManager.password,
                fin = UserManager.fin,
                serialNo = UserManager.serialNo,
                age = UserManager.age,
                gender = UserManager.gender,
                phone = UserManager.phone,
                address = UserManager.address
            )

            lifecycleScope.launch {
                try {
                    val courier = apiService.createCourier(newCourier)

                    Toast.makeText(applicationContext, "You are now a courier with id = ${courier.id}.",
                        Toast.LENGTH_SHORT).show()

                    dialog.dismiss()
                    overlayView?.visibility = View.GONE

                    UserManager.isCourier = true

                    val updatedCustomer = Customer(
                        isCourier = true
                    )
                    val customer = apiService.updateCustomer(UserManager.id!!, updatedCustomer)

                    val intent = Intent(this@MainActivity, CourierCargoListActivity::class.java)
                    startActivity(intent)
//                    becomeCourierClicked()
                } catch (e : IllegalFormatException) {
                    Log.e("Become Courier", "IllegalFormatException: ${e.message}")
                    Toast.makeText(applicationContext, "Occur a error, please try again!", Toast.LENGTH_SHORT).show()
                }
                catch (e: Exception) {
                    Log.e("Update Package Step", "Error while creating courier: ${e.message}")
                    Toast.makeText(applicationContext, "Occur a error, please try again!", Toast.LENGTH_SHORT).show()
                }
            }

        }

        btnDisagree.setOnClickListener {
            Toast.makeText(applicationContext, "Cancel clicked", Toast.LENGTH_SHORT).show()
            dialog.dismiss()

            overlayView?.visibility = View.GONE
        }

        dialog.show()
    }
}