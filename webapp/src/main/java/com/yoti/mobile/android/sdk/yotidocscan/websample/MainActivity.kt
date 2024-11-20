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
import android.view.View.VISIBLE
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.FileChooserParams
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.yoti.mobile.android.sdk.yotidocscan.websample.AccelerometerListener.ShakeListener
import com.yoti.mobile.android.sdk.yotidocscan.websample.SessionBottomSheet.SessionConfigurationListener
import com.yoti.mobile.android.sdk.yotidocscan.websample.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * YDS Web SDK in Android WebView sample
 *
 * We strongly recommend to use YDS native SDK for Android clients, because using YDS web version
 * inside an Android WebView doesn't guarantee the compatibility and some issues can be raised
 * depending of the SO version & WebView component version.
 *
 * If you still prefer just to load YDS web into an Android WebView, this sample cover all the base
 * requirements needed for a good performance, which are:
 *
 *      - Add permissions to manifest: check webapp module AndroidManifest.xml file
 *
 *      - Request all the permissions before start YDS flow and DON'T start it if
 *        any of the permissions request is denied.
 *
 *      - Launch intents to pick a file or take a picture and return the results to the WebView.
 *
 *      - Configure WebView for callback management to set the results of the picture intents
 *
 *      - Use FileProvider api to setup Android Camera capture result file.
 *
 *      - Detect end of YDS flow by URL
 *
 *      - Bear in mind/manage view re-creation: The app can be put in background by the user and if the
 *        system memory is low, the process can be destroyed by the system. If this happens, the view will
 *        be re-created when the app is put in foreground again by the user. This means that we have to reload
 *        the WebView.
 *
 *      - For a better UX, we also recommend:
 *          - Only allow orientation portrait (in manifest) or manage orientation changes
 *            to avoid a reload of the flow if the user rotates the device
 *          - Use a NoActionBar theme
 *          - Manage back navigation: users can press back hardware button and exit from the flow
 */
private const val CAPTURE_REQUEST_CODE = 1112
private const val PERMISSIONS_REQUEST_CODE = 1114

private const val KEY_IS_VIEW_RECREATED = "MainActivity.KEY_IS_VIEW_RECREATED"
private const val FINISH_SESSION_URL = "https://www.yoti.com/"

class MainActivity : AppCompatActivity(), SessionConfigurationListener {

    private var sessionBottomSheet: SessionBottomSheet? = null

    private lateinit var cameraCaptureFileUri: Uri
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var isViewRecreated: Boolean = false
    private val shakeListener = AccelerometerListener(this, object: ShakeListener {
        override fun onShake() {
            showOptionsDialog()
        }
    })

    private val mimeTypeMap = mapOf(
            ".pdf" to "application/pdf",
            ".png" to "image/png",
            ".jpg" to "image/jpeg"
    )

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isViewRecreated = savedInstanceState?.getBoolean(KEY_IS_VIEW_RECREATED) ?: false

        requestPermissions()

        binding.webview.configureForYdsWeb()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_REQUEST_CODE) {
            if (!isViewRecreated && resultCode == Activity.RESULT_OK) {
                val resultUri = data?.data ?: cameraCaptureFileUri
                filePathCallback?.onReceiveValue(arrayOf(resultUri))
            } else {
                filePathCallback?.onReceiveValue(null)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        shakeListener.start()
    }

    override fun onPause() {
        shakeListener.stop()
        super.onPause()
    }

    override fun onDestroy() {
        binding.webview.destroy()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_VIEW_RECREATED, true)
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
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        this.settings.apply {
            javaScriptEnabled = true
            allowFileAccess = true
            allowUniversalAccessFromFileURLs = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            mediaPlaybackRequiresUserGesture = false
        }
        this.webViewClient = YdsWebClient()
        this.webChromeClient = YdsWebChromeClient()
    }

    private fun showCameraAndFilePickerChooser(fileChooserParams: FileChooserParams) {

        cameraCaptureFileUri = createFileUri()

        Intent.createChooser(createFilePickerIntent(fileChooserParams), fileChooserParams.title)
                .run {
                    putExtra(
                            Intent.EXTRA_INITIAL_INTENTS,
                            listOf(createCameraIntent(cameraCaptureFileUri)).toTypedArray()
                    )
                    startActivityForResult(this, CAPTURE_REQUEST_CODE)
                }
    }

    private fun createFilePickerIntent(params: FileChooserParams): Intent? {
        return params.createIntent()?.apply {
            type = "*/*"
            putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    params.acceptTypes?.map { mimeTypeMap[it] }!!.filterNotNull().toTypedArray()
            )
        }
    }

    private fun createCameraIntent(outputFile: Uri): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(MediaStore.EXTRA_OUTPUT, outputFile)
        }
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

            return if (fileChooserParams?.mode == FileChooserParams.MODE_OPEN) {
                showCameraAndFilePickerChooser(fileChooserParams)
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

    private inner class YdsWebClient : WebViewClient() {
        // Detect the URL that indicates that YDS flow is finished
        // and close the app
        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            if (url == FINISH_SESSION_URL) {
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("YDS Session")
                        .setMessage("Session finished")
                        .setPositiveButton("OK") { _, _ -> this@MainActivity.finish() }
                        .show()
            }
        }
    }

    private fun showOptionsDialog() {
        if (sessionBottomSheet != null) return

        sessionBottomSheet = SessionBottomSheet.newInstance()
        sessionBottomSheet?.show(
                supportFragmentManager,
                SessionBottomSheet.FRAGMENT_TAG
        )
    }

    override fun onSessionConfigurationSuccess(sessionUrl: String) {
        with(binding) {
            webview.visibility = VISIBLE
            webview.loadUrl(sessionUrl)
        }
    }

    override fun onSessionConfigurationDismiss() {
        sessionBottomSheet = null
    }
}
