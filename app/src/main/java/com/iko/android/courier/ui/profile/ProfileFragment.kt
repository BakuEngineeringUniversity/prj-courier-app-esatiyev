package com.iko.android.courier.ui.profile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.navigation.NavigationView
import com.iko.android.courier.R
import com.iko.android.courier.UserManager
import com.iko.android.courier.ui.profile.balance.BalanceFragment
import com.iko.android.courier.ui.profile.review.ReviewFragment
import com.iko.android.courier.ui.settings.SettingsActivity

import android.app.AlertDialog
import android.net.Uri



class ProfileFragment : Fragment() {
    private lateinit var reviews_tab: TextView
    private lateinit var balance_tab: TextView
    private lateinit var childFragmentContainer: FragmentContainerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        val fullNameTitle = view.findViewById<TextView>(R.id.profile_title)
        val phoneNumber = view.findViewById<TextView>(R.id.phone_number)
        val email = view.findViewById<TextView>(R.id.email)
        val orders = view.findViewById<TextView>(R.id.orders)
        val delivers = view.findViewById<TextView>(R.id.delivers)
        val rating = view.findViewById<TextView>(R.id.rating)

        val fullName = UserManager.firstname + " " + UserManager.lastname

        fullNameTitle.text = fullName
        phoneNumber.text = UserManager.phone
        email.text = UserManager.email
        val ordersNumber = "Orders: " + UserManager.ordersNumber
        orders.text = ordersNumber
        val deliversNumber = "Delivers: " + UserManager.deliversNumber
        delivers.text = deliversNumber
        val ratingNumber = "Rating: " + UserManager.rating
        rating.text = ratingNumber

        val drawerLayout: DrawerLayout = view.findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = view.findViewById(R.id.navigationView)

        drawerLayout.setScrimColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryTransparent));

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_sign_out -> {
                    drawerLayout.closeDrawer(Gravity.RIGHT)
                    true
                }
                R.id.menu_settings -> {
                    drawerLayout.closeDrawer(Gravity.RIGHT)
                    val intent = Intent(context, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_customer_support -> {
                    drawerLayout.closeDrawer(Gravity.RIGHT)
                    showCustomDialog() // Call the function to show the custom dialog
                    true
                }
                else -> false
            }
        }

        val settings = view.findViewById<ImageView>(R.id.settings)
        settings.setOnClickListener {
            // Open the drawer when the sign-out button is clicked
            drawerLayout.openDrawer(Gravity.RIGHT)
        }

        reviews_tab = view.findViewById(R.id.reviews_tab)
        balance_tab = view.findViewById(R.id.balance_tab)
        childFragmentContainer = view.findViewById(R.id.tabFragmentContainerView)

        reviews_tab.setOnClickListener {
            // Replace the child fragment with ChildFragment1

            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.tabFragmentContainerView, ReviewFragment())
            transaction.commit()

            reviews_tab.setTextColor(resources.getColor(R.color.colorPrimary))
            balance_tab.setTextColor(resources.getColor(R.color.colorText))

            reviews_tab.background = resources.getDrawable(R.drawable.bottom_border_no_background)
            balance_tab.background = resources.getDrawable(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)

        }

        balance_tab.setOnClickListener {
            // Replace the child fragment with ChildFragment2
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.tabFragmentContainerView, BalanceFragment())
            transaction.commit()

            reviews_tab.setTextColor(resources.getColor(R.color.colorText))
            balance_tab.setTextColor(resources.getColor(R.color.colorPrimary))

//            reviews_tab?.background?.isVisible?.equals(false)
            reviews_tab.background = resources.getDrawable(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
            balance_tab.background = resources.getDrawable(R.drawable.bottom_border_no_background)
        }

        // ilk defe hansi child fragment gostersin :
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.tabFragmentContainerView, ReviewFragment())
        transaction.commit()

        return view
    }


    private fun showCustomDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.customer_support_custom_dialog, null)
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
        val alertDialog = dialogBuilder.create()

        // Access buttons in the custom dialog
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)
        val btnCall = dialogView.findViewById<Button>(R.id.btnCall)

        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }

        btnCall.setOnClickListener {
            // Dial 911
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:911")
            startActivity(intent)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }



}