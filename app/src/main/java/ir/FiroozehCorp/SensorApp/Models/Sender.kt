package ir.FiroozehCorp.SensorApp.Models

import java.io.Serializable

class Sender : Serializable {


    var gyroData: FloatArray? = null
    var compassData: FloatArray? = null
    var location: Location? = null

    inner class Location {
        var accuracy: Float? = null
        var lalitude: Double? = null
        var longitude: Double? = null
        var time: Long? = null
        var speed: Float? = null
    }


}