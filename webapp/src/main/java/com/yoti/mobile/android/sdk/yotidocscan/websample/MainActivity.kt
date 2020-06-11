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
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * YDS Web SDK in Android webview sample
 *
 * We strongly recommend to use YDS native SDK for Android clients, because using YDS web version
 * inside an Android WebView doesn't guarantee the compatibility and some issues can be raised
 * depending of the SO version & WebView component version.
 *
 * If you still prefer just to load YDS web into an Android webview, this sample cover all the base
 * requirements needed for a good performance, which are:
 *
 *      - Add permissions to manifest: check webapp module AndroidManifest.xml file
 *
 *      - Request all the permissions before start YDS flow and DON'T start it if
 *        any of the permissions request is denied.
 *
 *      - Launch intents to pick a file or take a picture and return the results to the webview.
 *
 *      - Configure webview for callback management to set the results of the picture intents
 *
 *      - Use FileProvider api to setup Android Camera capture result file.
 *
 *      - Detect end of YDS flow by URL
 *
 *      - For a better UX, we also recommend:
 *          - Only allow orientation portrait (in manifest) or manage orientation changes
 *            to avoid a reload of the flow if the user rotates the device
 *          - Use a NoActionBar theme
 *          - Manage back navigation: users can press back hardware button and exit from the flow
 */
private const val CAMERA_REQUEST_CODE = 1112
private const val FILE_PICKER_REQUEST_CODE = 1113
private const val PERMISSIONS_REQUEST_CODE = 1114

private const val TAG = "YdsWebSample"
private const val SESSION_URL = "<YourYdsURLSessionHere>"

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
                    Log.d(TAG, "Receive camera result $cameraCaptureFileUri")
                    filePathCallback?.onReceiveValue(arrayOf(cameraCaptureFileUri))
                }
            }
        }
    }

    override fun onDestroy() {
        webview.destroy()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            grantResults.firstOrNull { it != PackageManager.PERMISSION_GRANTED }?.let {
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
        cameraCaptureFileUri = createFileUri()

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(MediaStore.EXTRA_OUTPUT, cameraCaptureFileUri)

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

    /**
     * Create a file accessible from other applications by FileProvider api
     */
    private fun createFileUri(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(Date())
        val imageFileName = "YDSCapture_${timeStamp}"
        val storageDir = getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
        )

        return FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider",
                File.createTempFile(imageFileName, ".jpg", storageDir)
        )
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
        // and close the app
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
            request?.url?.toString()?.let { Log.d(TAG, it) }

            return super.shouldOverrideUrlLoading(view, request)
        }
    }
}
