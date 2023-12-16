package com.iko.android.courier.ui.terms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.iko.android.courier.R
import com.iko.android.courier.ui.main.MainActivity

class TermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)
        window.statusBarColor = resources.getColor(R.color.colorStatusBar)
        window.navigationBarColor = resources.getColor(R.color.md_white_1000)

        val webView: WebView = findViewById(R.id.webView)

        webView.settings.javaScriptEnabled = true

        // Load the HTML content
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Terms and Conditions</title>
            </head>
            <body>
                <h1>Terms and Conditions</h1>
                <p>By using the Courier Application, you agree to the following terms of use:</p>
            
                <ol>
                    <li>You agree to use the application for lawful and legitimate purposes only.</li>
                    <li>You agree not to engage in any activities that violate local, national, or international laws and regulations.</li>
                    <li>You understand that the content provided by the application is for informational purposes and should not be considered as professional advice.</li>
                    <li>You are responsible for maintaining the confidentiality of your account and password used to access the application.</li>
                    <li>You agree not to attempt to disrupt, hack, or gain unauthorized access to the application's servers or data.</li>
                    <li>The application's content and services are provided "as is" without any warranties or guarantees.</li>
                    <li>We reserve the right to modify or terminate the application's services at any time.</li>
                    <li>Your use of the application may be subject to applicable privacy and data protection laws.</li>
                    <li>You consent to the collection and use of your data as described in our <a href="privacy_policy.html">Privacy Policy</a>.</li>
                </ol>
            
                <p>If you do not agree with these terms and conditions, please refrain from using the Courier Application.</p>
            </body>
            </html>
        """.trimIndent()

        webView.loadData(htmlContent, "text/html", "UTF-8")

        webView.webViewClient = WebViewClient()
    }

    fun backToHome(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}