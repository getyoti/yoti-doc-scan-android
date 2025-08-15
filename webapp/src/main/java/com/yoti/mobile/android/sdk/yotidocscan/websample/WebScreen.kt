package com.yoti.mobile.android.sdk.yotidocscan.websample

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.FileChooserParams
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebScreen(
        sessionUrl: String,
        onPageCommitVisible: (String?) -> Unit,
        onFilePathCallbackReady: (ValueCallback<Array<Uri>>?) -> Unit,
        onShowCameraAndFilePickerChooser: (FileChooserParams) -> Unit,
        modifier: Modifier = Modifier
) {
    AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    configureSettings(settings)
                    configureWebViewClient(this, onPageCommitVisible)
                    configureWebChromeClient(
                            this,
                            onFilePathCallbackReady,
                            onShowCameraAndFilePickerChooser
                    )
                }
            },
            update = { webView ->
                sessionUrl.takeIf { it.isNotBlank() }?.let { webView.loadUrl(it) }
            }
    )
}

@SuppressLint("SetJavaScriptEnabled")
private fun configureSettings(settings: WebSettings) {
    with(settings) {
        javaScriptEnabled = true
        allowFileAccess = true
        allowUniversalAccessFromFileURLs = true
        domStorageEnabled = true
        javaScriptCanOpenWindowsAutomatically = true
        mediaPlaybackRequiresUserGesture = false
    }
}

private fun configureWebViewClient(webView: WebView, onPageCommitVisible: (String?) -> Unit) {
    webView.webViewClient = object : WebViewClient() {
        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            onPageCommitVisible(url)
        }
    }
}

private fun configureWebChromeClient(
        webView: WebView,
        onFilePathCallbackReady: (ValueCallback<Array<Uri>>?) -> Unit,
        onShowCameraAndFilePickerChooser: (FileChooserParams) -> Unit
) {
    webView.webChromeClient = object : WebChromeClient() {
        // Launch file picker or camera intent and set the
        // filePathCallback to set the capture results to the webview
        override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
        ): Boolean {
            onFilePathCallbackReady(filePathCallback)
            return if (fileChooserParams?.mode == FileChooserParams.MODE_OPEN) {
                onShowCameraAndFilePickerChooser(fileChooserParams)
                true
            } else {
                false
            }
        }

        // Grant permission requested
        override fun onPermissionRequest(request: PermissionRequest?) {
            request?.grant(request.resources)
        }
    }
}