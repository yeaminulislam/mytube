package com.amin.mytube

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color

/**
 * MyTube - WebView MainActivity
 * আমিনের প্রথম Android App - Full Functional YouTube Clone
 * 
 * Features:
 * - YouTube Video Play (ExoPlayer via WebView)
 * - File Upload, Fullscreen Video
 * - Back Button Handling
 * - PWA Support
 */

class MainActivity : AppCompatActivity() {
    
    private lateinit var webView: WebView
    
    // তোমার MyTube URL এখানে বসাও
    // Option 1: Local Dev (Same WiFi, Phone + Laptop)
    // Option 2: Vercel Production URL (Recommended)
    private val MYTUBE_URL = "https://mytube.vercel.app" 
    // Local testing: "http://192.168.1.100:5173" (তোমার Laptop IP)
    // Emulator: "http://10.0.2.2:5173"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        
        // WebView Settings - YouTube এর জন্য জরুরি
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
            userAgentString = "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36 MyTube/1.0"
        }

        // WebView Clients
        webView.webViewClient = WebViewClient() // Links open inside app
        webView.webChromeClient = WebChromeClient() // Video fullscreen, file chooser

        // Background color - YouTube dark theme
        webView.setBackgroundColor(Color.parseColor("#0f0f0f"))

        // Load MyTube
        webView.loadUrl(MYTUBE_URL)
        
        // Enable debugging (Chrome → chrome://inspect)
        WebView.setWebContentsDebuggingEnabled(true)
    }

    // Back button → WebView back, not exit app
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    // Save state on rotation
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }
}
