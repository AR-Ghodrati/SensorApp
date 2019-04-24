package ir.FiroozehCorp.SensorApp.Activities

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.google.firebase.FirebaseApp

class App : Application() {

    override fun attachBaseContext(base: Context) {
        MultiDex.install(base)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        FirebaseApp.initializeApp(this)
        super.onCreate()
    }
}