package ir.FiroozehCorp.SensorApp.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Sender : Serializable {


    @SerializedName("gyroData")
    @Expose
    var gyroData: FloatArray? = null

    @SerializedName("compassData")
    @Expose
    var compassData: FloatArray? = null

    @SerializedName("location")
    @Expose
    var location: Location? = null

    inner class Location {
        @SerializedName("accuracy")
        @Expose
        var accuracy: Float? = null

        @SerializedName("latitude")
        @Expose
        var latitude: Double? = null

        @SerializedName("longitude")
        @Expose
        var longitude: Double? = null

        @SerializedName("time")
        @Expose
        var time: Long? = null

        @SerializedName("speed")
        @Expose
        var speed: Float? = null
    }


}