package com.amin.mytube

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

/**
 * MyTube - Simple WebView Wrapper
 * আমিনের প্রথম অ্যান্ড্রয়েড অ্যাপ!
 * 
 * এই কোডটি দিয়ে তুমি MyTube ওয়েবসাইটকে অ্যান্ড্রয়েড অ্যাপে রূপান্তর করতে পারবে
 */

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        
        // Enable JavaScript (React এর জন্য জরুরি)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        
        // YouTube ভিডিও প্লে করার জন্য
        webView.settings.mediaPlaybackRequiresUserGesture = false
        
        // WebView Client - লিংক অ্যাপের ভিতরেই খুলবে
        webView.webViewClient = WebViewClient()
        
        // MyTube লোড করো
        // Localhost এর জন্য: http://10.0.2.2:5173 (Emulator থেকে)
        // Production: তোমার Vercel/Netlify URL
        webView.loadUrl("https://mytube.vercel.app") 
    }

    // Back button এ আগের পেজে যাবে
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
