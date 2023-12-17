package com.iko.android.courier.ui.cargo.state

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.iko.android.courier.R
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.ui.main.MainActivity
import kotlinx.coroutines.launch

class CargoManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cargo_management)
        window.statusBarColor = resources.getColor(R.color.colorStatusBar)
        window.navigationBarColor = resources.getColor(R.color.colorNavigationBar)

        // Check if the activity was started with specific package information.
        val packageId: String? = intent.getStringExtra("packageId")

        if (packageId != null) {
            // The activity was started with package information, load the CargoStateFragment.
            val cargoStateFragment = CargoStateFragment()

            // Pass the packageId to the fragment, if needed.
            val bundle = Bundle()
            bundle.putString("packageId", packageId)
            cargoStateFragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, cargoStateFragment)
                .commit()
        } else {
            // The activity was not started with package information. Perform your default behavior here.
        }
    }

    fun backToOwnCargo(view: View) {
        // If the back stack is empty, go back to the main activity.
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("selectedAction", "action_cargo")
        startActivity(intent)
        finish()
    }

}