package com.iko.android.courier.ui.main

import android.app.Dialog
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
import com.iko.android.courier.ui.auth.login.LoginActivity
import com.iko.android.courier.ui.cargo.awaitingCourier.CourierCargoListActivity
import com.iko.android.courier.ui.cargo.create.CreateCargoActivity
import com.iko.android.courier.ui.cargo.delivers.DeliversCargoListFragment
import com.iko.android.courier.ui.cargo.ownCargo.OwnCargoListFragment
import com.iko.android.courier.ui.profile.ProfileFragment
import com.iko.android.courier.ui.terms.TermsActivity
import kotlinx.coroutines.launch
import java.util.IllegalFormatException


class MainActivity : AppCompatActivity() {

//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // R.layout.activity_main
//
//        // Inflate the menu items
//        bottomNavigationView.inflateMenu(R.menu.main_bottom_nav)

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
            // Handle the case when selectedAction is null
            replaceFragment(HomeFragment()) // Default action
        }


        window.navigationBarColor = resources.getColor(R.color.colorNavigationBar)
        window.statusBarColor = resources.getColor(R.color.colorHomeBar)


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