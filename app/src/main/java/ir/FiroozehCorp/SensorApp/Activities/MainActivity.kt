package ir.FiroozehCorp.SensorApp.Activities

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.gson.Gson
import ir.FiroozehCorp.SensorApp.R
import ir.FiroozehCorp.SensorApp.Utils.SensorUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), SensorEventListener {


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        log?.text = Gson().toJson(event)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start?.setOnClickListener {
            if (ip?.text.toString().isNotEmpty() && timer?.text.toString().isNotEmpty()) {
                Timer().scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        SensorUtil.getGyroscopeData(
                            this@MainActivity
                            , this@MainActivity
                        )
                    }
                }, 0L, timer?.text.toString().toLong())
            } else
                Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show()
        }
    }
}
