package ir.FiroozehCorp.SensorApp.Utils

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager


object SensorUtil {

    fun getGyroscopeData(activity: Activity, listener: SensorEventListener) {
        val mSensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mSensorManager.registerListener(listener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
}