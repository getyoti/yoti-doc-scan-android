package com.yoti.mobile.android.sdk.yotidocscan.websample

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val CAMERA_REQUEST_CODE = 1112
private const val FILE_PICKER_REQUEST_CODE = 1113
private const val PERMISSIONS_REQUEST_CODE = 1114

private const val TAG = "YdsWebSample"
private const val SESSION_URL = "<Your YDS Session URL here>"

/**
 * We strongly recommend to use YDS native SDK for Android clients, but if you still
 * prefer just to load YDS web into an Android webview, this sample could be useful
 * to know what is the configuration needed. Basically, it is doing the following:
 *
 *  1. Add permissions to manifest: check webapp module AndroidManifest.xml file
 *
 *  2. Request all the permissions before start YDS flow and DON'T start it if
 *     any of the permissions request is denied.
 *
 *  4. Only allow orientation portrait (in manifest) or manage orientation changes
 *     to avoid a reload of the flow if the user rotates the device.
 *
 *  5. Configure webview for callback management.
 *
 *  6. Manage camera capture results (image rotation).
 *
 *  7. To show the webview, we recommend to use a NoActionBar theme.
 *
 */
class MainActivity : AppCompatActivity() {

    private lateinit var cameraCaptureFileUri: Uri
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()

        webview.configureForYdsWeb()
        webview.loadUrl(SESSION_URL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FILE_PICKER_REQUEST_CODE -> {
                    data?.data?.let {
                        Log.d(TAG, "Receive file result $it")
                        filePathCallback?.onReceiveValue(arrayOf(it))
                    }
                }
                CAMERA_REQUEST_CODE -> {
                    // TODO: Manage image rotation before set the image result to the webview.
                    //  By default the camera is in landscape mode, so every image received
                    //  has to be rotate to display it in portrait
                    Log.d(TAG, "Receive camera result $cameraCaptureFileUri")
                    filePathCallback?.onReceiveValue(arrayOf(cameraCaptureFileUri))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            grantResults.firstOrNull { it != PackageManager.PERMISSION_GRANTED }?.also {
                AlertDialog.Builder(this)
                        .setTitle("Permissions needed")
                        .setMessage("All permissions are needed to continue with the YDS session")
                        .setPositiveButton("OK") { _, _ -> super.finish() }
                        .show()
            }
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setTitle("Close YDS Session")
                .setMessage("Are you sure you want to finish YDS session?")
                .setPositiveButton("Yes") { _, _ -> super.onBackPressed() }
                .setNegativeButton("No", null)
                .show()
    }

    private fun requestPermissions() {
        val permissions = listOf(
                permission.CAMERA,
                permission.RECORD_AUDIO,
                permission.READ_EXTERNAL_STORAGE,
                permission.WRITE_EXTERNAL_STORAGE
        )

        val permissionsRequest = permissions.mapNotNull { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permission
            } else null
        }.toTypedArray()

        if (permissionsRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsRequest,
                    PERMISSIONS_REQUEST_CODE
            )
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun WebView.configureForYdsWeb() {
        this.settings.apply {
            javaScriptEnabled = true
            allowFileAccess = true
            allowUniversalAccessFromFileURLs = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            mediaPlaybackRequiresUserGesture = false
            setAppCacheEnabled(true)
        }
        this.webViewClient = YdsWebClient()
        this.webChromeClient = YdsWebChromeClient()
    }

    private fun launchCamera() {
        Log.d(TAG, "Launch Camera intent")
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraCaptureFileUri = createFileUri()
        cameraIntent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                cameraCaptureFileUri
        )

        startActivityForResult(Intent.createChooser(cameraIntent, "Select a picture"), CAMERA_REQUEST_CODE)
    }

    private fun launchImagePicker() {
        Log.d(TAG, "Launch file picker intent")
        val filePickerIntent = Intent()
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(
                Intent.createChooser(filePickerIntent, "Select a file"),
                FILE_PICKER_REQUEST_CODE
        )
    }

    private fun createFileUri() : Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(Date())
        val imageFileName = "YDSCapture_${timeStamp}"
        val storageDir = this.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
        )
        val file = File.createTempFile(imageFileName, ".jpg", storageDir)

        return Uri.fromFile(file)
    }

    private inner class YdsWebChromeClient: WebChromeClient() {
        // Launch file picker or camera intent and set the
        // filePathCallback to set the capture results to the webview
        override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
        ): Boolean {
            this@MainActivity.filePathCallback = filePathCallback

            if (fileChooserParams?.isCaptureEnabled == true) launchCamera()
            else launchImagePicker()

            return true
        }

        // Grant permission requested
        override fun onPermissionRequest(request: PermissionRequest?) {
            request?.grant(request.resources)
        }
    }

    private inner class YdsWebClient : WebViewClient() {
        // Detect the URL that indicates that YDS flow is finished
        // and we can close the webview
        override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?
        ): Boolean {
            if (request?.url?.toString() == "https://www.yoti.com/") {
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("YDS Session")
                        .setMessage("Session finished OK")
                        .setPositiveButton("OK") { _, _ -> this@MainActivity.finish() }
                        .show()

                return true
            }

            return super.shouldOverrideUrlLoading(view, request)
        }
    }
}
