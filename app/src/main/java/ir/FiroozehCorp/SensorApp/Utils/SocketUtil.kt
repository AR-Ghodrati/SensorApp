package ir.FiroozehCorp.SensorApp.Utils

import android.app.Activity
import android.widget.Toast
import com.google.gson.Gson
import ir.FiroozehCorp.SensorApp.Models.Sender
import java.io.DataOutputStream
import java.net.Socket

class SocketUtil {

    private lateinit var socket: Socket
    private lateinit var outputStream: DataOutputStream
    private lateinit var activity: Activity
    private lateinit var ip: String
    private var port: Int = 0

    fun init(activity: Activity, ip: String, port: Int) {
        try {

            this.activity = activity
            this.ip = ip
            this.port = port

            socket = Socket(ip, port)
            outputStream = DataOutputStream(socket.getOutputStream())
        } catch (e: Exception) {
            Toast.makeText(activity, "${javaClass.name} Error : $e", Toast.LENGTH_LONG).show()
            init(activity, ip, port)
        }

    }

    fun sendData(sender: Sender) {
        try {
            outputStream.write("${Gson().toJson(sender)}|".toByteArray())
        } catch (e: Exception) {
            Toast.makeText(activity, "${javaClass.name} SendData : $e", Toast.LENGTH_LONG).show()
            init(activity, ip, port)
        }
    }


}