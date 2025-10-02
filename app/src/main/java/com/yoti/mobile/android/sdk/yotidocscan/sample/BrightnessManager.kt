package com.yoti.mobile.android.sdk.yotidocscan.sample

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL

class BrightnessManager : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity.javaClass.name == "com.yoti.mobile.android.facecapture.view.FaceCaptureActivity") {
            val window = activity.window
            val layoutParams = window.attributes
            layoutParams.screenBrightness = BRIGHTNESS_OVERRIDE_FULL
            window.attributes = layoutParams
        }
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}