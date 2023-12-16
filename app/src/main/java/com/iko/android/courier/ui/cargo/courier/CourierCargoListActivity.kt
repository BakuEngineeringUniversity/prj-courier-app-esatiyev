package com.iko.android.courier.ui.cargo.courier

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.iko.android.courier.R
import com.iko.android.courier.ui.main.MainActivity

class CourierCargoListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courier_cargo_list)
        window.navigationBarColor = resources.getColor(R.color.colorNavigationBar)
        window.statusBarColor = resources.getColor(R.color.colorStatusBar)


    }

    fun backToHome(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}