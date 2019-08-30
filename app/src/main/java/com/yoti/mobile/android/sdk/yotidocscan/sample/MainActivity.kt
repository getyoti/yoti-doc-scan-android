package com.yoti.mobile.android.sdk.yotidocscan.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yoti.mobile.android.yotidocs.YOTI_DOCS_REQUEST_CODE
import com.yoti.mobile.android.yotidocs.YotiDocScan
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var yotiDocScan: YotiDocScan

    private var sessionId: String = ""
    private var sessionToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edSessionId.setText(sessionId)
        edTokenId.setText(sessionToken)

        yotiDocScan = YotiDocScan(this)

        buttonScanDocument.setOnClickListener {
            val success = yotiDocScan
                .setSessionId(edSessionId.text.toString())
                .setClientSessionToken(edTokenId.text.toString())
                .start(this)
            if (!success) {
                showSessionStatus()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (YOTI_DOCS_REQUEST_CODE == requestCode) {
            showSessionStatus()
        }
    }

    private fun showSessionStatus() {
        val code = yotiDocScan.sessionStatusCode
        val description = yotiDocScan.sessionStatusDescription
        sessionStatusText.text = getString(R.string.session_status_text, code, description)
    }
}
