package com.yoti.mobile.android.sdk.yotidocscan.sample

import android.app.Application

class YotiDocScanSampleApplication : Application() {

    private val brightnessManager = BrightnessManager()

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(brightnessManager)

//         after the Yoti SDK is done executing, unregister it:
//        unregisterActivityLifecycleCallbacks(brightnessManager)
    }
}