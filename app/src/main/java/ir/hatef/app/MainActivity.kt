package ir.hatef.app

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : Activity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NotificationHelper.createChannel(this)
        setupWebView()
        findViewById<Button>(R.id.copyPushIdButton).apply {
            visibility = if (BuildConfig.DEBUG) View.VISIBLE else View.GONE
            setOnClickListener { copyInstallationId() }
        }
        if (savedInstanceState == null) {
            webView.loadUrl(urlFromIntent(intent))
        } else {
            webView.restoreState(savedInstanceState)
        }
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATIONS)
        }
        FirebaseMessaging.getInstance().register()
            .addOnFailureListener { error -> Log.w(TAG, "FCM registration failed", error) }
    }

    private fun setupWebView() {
        webView = findViewById(R.id.hatefWebView)
        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) safeBrowsingEnabled = true
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if (!request.isForMainFrame) return false
                val url = request.url.toString()
                if (HatefUrlPolicy.isTrustedUrl(url)) return false
                val scheme = request.url.scheme?.lowercase()
                if (scheme in setOf("https", "http", "mailto", "tel")) {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, request.url))
                    } catch (error: Exception) {
                        Log.w(TAG, "No application to handle external URL", error)
                    }
                }
                return true
            }
        }
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
    }

    private fun urlFromIntent(source: Intent?): String =
        HatefUrlPolicy.destinationForPath(source?.getStringExtra("path"))
            ?: HatefUrlPolicy.HOME_URL

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        webView.loadUrl(urlFromIntent(intent))
    }

    private fun copyInstallationId() {
        val fid = PushRegistrationStore.current(this)
        if (fid.isNullOrBlank()) {
            Toast.makeText(this, R.string.push_id_pending, Toast.LENGTH_LONG).show()
            return
        }
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Hatef FCM Installation ID", fid))
        Toast.makeText(this, R.string.push_id_copied, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Use Android predictive back handling in a future UI iteration")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        // WebView owns network and rendering resources; dispose when Activity ends.
        (webView.parent as? android.view.ViewGroup)?.removeView(webView)
        webView.destroy()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "HatefAndroid"
        private const val REQUEST_NOTIFICATIONS = 1001
    }
}
