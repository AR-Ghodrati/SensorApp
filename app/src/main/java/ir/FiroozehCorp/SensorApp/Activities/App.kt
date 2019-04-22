package ir.FiroozehCorp.SensorApp.Activities

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex

class App : Application() {

    override fun attachBaseContext(base: Context) {
        MultiDex.install(base)
        super.attachBaseContext(base)
    }
}