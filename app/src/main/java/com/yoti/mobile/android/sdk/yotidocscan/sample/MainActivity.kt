package com.yoti.mobile.android.sdk.yotidocscan.sample

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yoti.mobile.android.sdk.yotidocscan.sample.ui.theme.YotiDocScanSampleAppTheme
import com.yoti.mobile.android.yotisdkcore.YotiSdk
import com.yoti.mobile.android.yotisdkcore.YotiSdkContract

class MainActivity : ComponentActivity() {

    private lateinit var yotiSdk: YotiSdk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.light(
                        scrim = TRANSPARENT,
                        darkScrim = TRANSPARENT
                ),
                navigationBarStyle = SystemBarStyle.light(
                        scrim = TRANSPARENT,
                        darkScrim = TRANSPARENT
                )
        )

        yotiSdk = YotiSdk(this@MainActivity)

        setContent {
            YotiDocScanSampleAppTheme {
                var sessionId by remember { mutableStateOf("") }
                var sessionToken by remember { mutableStateOf("") }
                var sessionStatus by remember { mutableStateOf("") }

                fun showSessionStatus() {
                    val code = yotiSdk.sessionStatusCode
                    val description = yotiSdk.sessionStatusDescription
                    sessionStatus = getString(R.string.session_status_text, code, description)
                }

                val launcher = rememberLauncherForActivityResult(contract = YotiSdkContract()) {
                    showSessionStatus()
                }

                Scaffold { innerPadding ->
                    MainScreen(
                            sessionId = sessionId,
                            onSessionIdChanged = { sessionId = it },
                            sessionToken = sessionToken,
                            onSessionTokenChanged = { sessionToken = it },
                            sessionStatus = sessionStatus,
                            onScanDocumentClicked = {
                                val success = yotiSdk
                                        .setSessionId(sessionId)
                                        .setClientSessionToken(sessionToken)
                                        .start(launcher)
                                if (!success) {
                                    showSessionStatus()
                                }
                            },
                            modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
