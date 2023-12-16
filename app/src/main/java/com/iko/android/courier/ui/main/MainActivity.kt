package com.iko.android.courier.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.iko.android.courier.R
import com.iko.android.courier.ui.auth.login.LoginActivity
import com.iko.android.courier.ui.cargo.courier.CourierCargoListActivity
import com.iko.android.courier.ui.cargo.create.CreateCargoActivity
import com.iko.android.courier.ui.cargo.delivers.DeliversCargoListFragment
import com.iko.android.courier.ui.cargo.list.OwnCargoListFragment
import com.iko.android.courier.ui.profile.ProfileFragment
import com.iko.android.courier.ui.terms.TermsActivity
import java.lang.Thread.sleep


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
        val intent = Intent(this, CourierCargoListActivity::class.java)
        startActivity(intent)
    }



    fun signOutClicked(item: MenuItem) {
        Toast.makeText(applicationContext, "Sign Out Clicked", Toast.LENGTH_SHORT).show()
        sleep(2000)
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
}