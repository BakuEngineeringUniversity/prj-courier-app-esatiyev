package com.iko.android.courier.ui.splash

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.iko.android.courier.R
import com.iko.android.courier.ui.auth.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.statusBarColor = Color.parseColor("#000000")
        window.navigationBarColor = Color.parseColor("#000033")
        Handler().postDelayed({
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}

