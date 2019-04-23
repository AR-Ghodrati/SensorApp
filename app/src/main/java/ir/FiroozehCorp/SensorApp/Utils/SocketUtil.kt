package ir.FiroozehCorp.SensorApp.Utils

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import ir.FiroozehCorp.SensorApp.Activities.MainActivity
import ir.FiroozehCorp.SensorApp.Models.Sender
import java.io.DataOutputStream
import java.net.Socket

class SocketUtil {

    private lateinit var socket: Socket
    private var outputStream: DataOutputStream? = null
    private lateinit var activity: Activity
    private lateinit var ip: String
    private var port: Int = 0
    private var Try = 0

    fun init(activity: Activity, ip: String, port: Int) {
        try {

            this.activity = activity
            this.ip = ip
            this.port = port

            socket = Socket(ip, port)
            outputStream = DataOutputStream(socket.getOutputStream())
            Try = 0
            MainActivity.isActive = true

        } catch (e: Exception) {
            Log.e(javaClass.name, "Error 1 :$e")
            Try++
            if (Try == 10) {

                activity.runOnUiThread {
                    Toast.makeText(activity, "${javaClass.name} Error1 : $e", Toast.LENGTH_LONG).show()
                }
                init(activity, ip, port)
            }
        }

    }

    fun sendData(sender: Sender) {
        try {
            val send = "${Gson().toJson(sender)}|"
            Log.e(javaClass.name, "Send : $send")
            outputStream?.write(send.toByteArray())
            Try = 0
        } catch (e: Exception) {
            Log.e(javaClass.name, "Error 2 :$e")
            Try++
            if (Try == 10) {

                activity.runOnUiThread {
                    Toast.makeText(activity, "${javaClass.name} SendData2 : $e", Toast.LENGTH_LONG).show()
                }

            init(activity, ip, port)
            }
        }
    }

    fun terminate() {
        try {
            socket.close()
            outputStream?.close()
        } catch (e: Exception) {
        }
    }


}