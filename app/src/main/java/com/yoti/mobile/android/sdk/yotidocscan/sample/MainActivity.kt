package com.yoti.mobile.android.sdk.yotidocscan.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yoti.mobile.android.yotisdkcore.YOTI_SDK_REQUEST_CODE
import com.yoti.mobile.android.yotisdkcore.YotiSdk
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var yotiSdk: YotiSdk

    private var sessionId: String = ""
    private var sessionToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edSessionId.setText(sessionId)
        edTokenId.setText(sessionToken)

        yotiSdk = YotiSdk(this)

        buttonScanDocument.setOnClickListener {
            val success = yotiSdk
                .setSessionId(edSessionId.text.toString())
                .setClientSessionToken(edTokenId.text.toString())
                .start(this) // Custom request code .start(this, 8888)
            if (!success) {
                showSessionStatus()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (YOTI_SDK_REQUEST_CODE == requestCode) {
            showSessionStatus()
        }
    }

    private fun showSessionStatus() {
        val code = yotiSdk.sessionStatusCode
        val description = yotiSdk.sessionStatusDescription
        sessionStatusText.text = getString(R.string.session_status_text, code, description)
    }
}
