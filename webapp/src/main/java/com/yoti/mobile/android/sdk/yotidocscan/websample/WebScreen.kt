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
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebScreen(
        sessionUrl: String,
        showSessionFinishedDialog: Boolean,
        onPageCommitVisible: (String?) -> Unit,
        onFilePathCallbackReady: (ValueCallback<Array<Uri>>?) -> Unit,
        onShowCameraAndFilePickerChooser: (FileChooserParams) -> Unit,
        onCloseSession: () -> Unit,
        onSessionFinished: () -> Unit,
        modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
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

        var showCloseSessionDialog by remember { mutableStateOf(false) }
        BackHandler { showCloseSessionDialog = true }
        if (showCloseSessionDialog) {
            CloseSessionDialog(
                    onConfirm = {
                        showCloseSessionDialog = false
                        onCloseSession()
                    },
                    onDismiss = { showCloseSessionDialog = false })
        }

        if (showSessionFinishedDialog) {
            SessionFinishedDialog(onConfirm = { onSessionFinished() })
        }
    }
}

@Composable
private fun CloseSessionDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
            title = { Text(stringResource(id = R.string.close_session_dialog_title)) },
            text = { Text(stringResource(id = R.string.close_session_dialog_text)) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(stringResource(id = R.string.close_session_dialog_confirm_button))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(id = R.string.close_session_dialog_dismiss_button))
                }
            },
            onDismissRequest = onDismiss
    )
}

@Composable
private fun SessionFinishedDialog(onConfirm: () -> Unit) {
    AlertDialog(
            title = { Text(stringResource(id = R.string.session_finished_dialog_title)) },
            text = { Text(stringResource(id = R.string.session_finished_dialog_text)) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(stringResource(id = R.string.session_finished_dialog_confirm_button))
                }
            },
            onDismissRequest = {}
    )
}

@Suppress("DEPRECATION")
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