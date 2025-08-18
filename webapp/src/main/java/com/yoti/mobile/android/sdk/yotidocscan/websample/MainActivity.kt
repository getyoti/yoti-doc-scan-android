package com.yoti.mobile.android.sdk.yotidocscan.websample

import android.Manifest.permission
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebChromeClient.FileChooserParams
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yoti.mobile.android.sdk.yotidocscan.websample.ui.YotiDocScanWebSampleAppTheme
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

private const val KEY_IS_VIEW_RECREATED = "MainActivity.KEY_IS_VIEW_RECREATED"
private const val FINISH_SESSION_URL = "https://www.yoti.com/"

class MainActivity : ComponentActivity() {

    private lateinit var cameraCaptureFileUri: Uri
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var isViewRecreated: Boolean = false

    private val mimeTypeMap = mapOf(
            ".pdf" to "application/pdf",
            ".png" to "image/png",
            ".jpg" to "image/jpeg"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isViewRecreated = savedInstanceState?.getBoolean(KEY_IS_VIEW_RECREATED) ?: false

        setContent {
            val navController = rememberNavController()
            var sessionUrl by remember { mutableStateOf("") }
            var showMissingPermissionsDialog by remember { mutableStateOf(false) }
            var showSessionFinishedDialog by remember { mutableStateOf(false) }

            val permissionsLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                showMissingPermissionsDialog = !(permissions.values.all { it })
            }
            LaunchedEffect(Unit) {
                permissionsLauncher.launch(arrayOf(permission.CAMERA, permission.RECORD_AUDIO))
            }

            YotiDocScanWebSampleAppTheme {
                Scaffold { innerPadding ->
                    NavHost(
                            navController = navController,
                            startDestination = AppDestinations.MAIN_SCREEN,
                            modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(route = AppDestinations.MAIN_SCREEN) {
                            MainScreen(
                                    sessionUrl = sessionUrl,
                                    showMissingPermissionsDialog = showMissingPermissionsDialog,
                                    onSessionUrlChanged = { sessionUrl = it },
                                    onStartSessionClicked = {
                                        navController.navigate(AppDestinations.WEB_SCREEN)
                                    },
                                    onMissingPermissionsConfirmed = { finish() }
                            )
                        }

                        composable(route = AppDestinations.WEB_SCREEN) {
                            WebScreen(
                                    sessionUrl = sessionUrl,
                                    showSessionFinishedDialog = showSessionFinishedDialog,
                                    onPageCommitVisible = { url ->
                                        // Detect the URL that indicates that flow is finished and close the app
                                        if (url == FINISH_SESSION_URL) {
                                            showSessionFinishedDialog = true
                                        }
                                    },
                                    onFilePathCallbackReady = { callback ->
                                        filePathCallback = callback
                                    },
                                    onShowCameraAndFilePickerChooser = { fileChooserParams ->
                                        showCameraAndFilePickerChooser(fileChooserParams)
                                    },
                                    onCloseSession = { navController.popBackStack() },
                                    onSessionFinished = { finish() }
                            )
                        }
                    }
                }
            }
        }
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_VIEW_RECREATED, true)
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
}