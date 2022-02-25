package com.yoti.mobile.android.sdk.yotidocscan.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yoti.mobile.android.sdk.yotidocscan.sample.databinding.ActivityMainBinding
import com.yoti.mobile.android.yotisdkcore.YOTI_SDK_REQUEST_CODE
import com.yoti.mobile.android.yotisdkcore.YotiSdk

class MainActivity : AppCompatActivity() {

    private lateinit var yotiSdk: YotiSdk

    private lateinit var binding: ActivityMainBinding

    private var sessionId: String = ""
    private var sessionToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            edSessionId.setText(sessionId)
            edTokenId.setText(sessionToken)

            yotiSdk = YotiSdk(this@MainActivity)

            buttonScanDocument.setOnClickListener {
                val success = yotiSdk
                        .setSessionId(edSessionId.text.toString())
                        .setClientSessionToken(edTokenId.text.toString())
                        .start(this@MainActivity) // Custom request code .start(this, 8888)
                if (!success) {
                    showSessionStatus()
                }
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
        binding.sessionStatusText.text = getString(R.string.session_status_text, code, description)
    }
}
