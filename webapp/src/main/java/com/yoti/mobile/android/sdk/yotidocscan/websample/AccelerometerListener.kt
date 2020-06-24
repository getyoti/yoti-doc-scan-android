package com.yoti.mobile.android.sdk.yotidocscan.websample

import android.content.Context
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import android.util.Log
import kotlin.math.sqrt

private const val TAG = "AccelerometerListener"

class AccelerometerListener(
        private val context: Context,
        private val shakeListener: ShakeListener?
) : SensorEventListener {

    interface ShakeListener {
        fun onShake()
    }

    private var sensorManager: SensorManager? = null
    private var shake = 0f
    private var currentShake = 0f
    private var lastShake = 0f

    fun start() {
        sensorManager = (context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager)
        sensorManager?.getDefaultSensor(TYPE_ACCELEROMETER)?.let { accelerometerSensor ->
            Log.d(TAG, "Start: register SensorManager listener")
            sensorManager?.registerListener(this, accelerometerSensor, SENSOR_DELAY_NORMAL)
        }?:run { Log.d(TAG, "Accelerometer sensor is not available") }
    }

    fun stop() {
        Log.d(TAG, "Stop: unregister SensorManager listener")
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.run {
            val x = values[0]
            val y = values[1]
            val z = values[2]
            lastShake = currentShake
            currentShake = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentShake - lastShake
            shake = shake * 0.9f + delta
            if (shake > 12) {
                Log.d(TAG, "Shake detected")
                shakeListener?.onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nothing to do here
    }
}