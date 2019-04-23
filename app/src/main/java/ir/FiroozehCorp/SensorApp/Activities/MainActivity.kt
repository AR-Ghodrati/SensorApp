package ir.FiroozehCorp.SensorApp.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import ir.FiroozehCorp.SensorApp.Models.Sender
import ir.FiroozehCorp.SensorApp.R
import ir.FiroozehCorp.SensorApp.Utils.SensorUtil
import ir.FiroozehCorp.SensorApp.Utils.SocketUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

open class MainActivity : AppCompatActivity()
    , GoogleApiClient.ConnectionCallbacks
    , GoogleApiClient.OnConnectionFailedListener
    , com.google.android.gms.location.LocationListener {

    private var gyroEvent: SensorEvent? = null
    private var CompassEvent: SensorEvent? = null
    private var socketUtil: SocketUtil? = null
    private var Timer: Timer? = null


    companion object {
        var isActive = false
    }



    private val TAG = "MainActivity"
    private lateinit var mGoogleApiClient: GoogleApiClient
    private var mLocationManager: LocationManager? = null
    lateinit var mLocation: Location
    private var mLocationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = (2 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */

    lateinit var locationManager: LocationManager


    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient.isConnected) {
            mGoogleApiClient.disconnect()
        }
    }

    override fun onConnectionSuspended(p0: Int) {

        Log.i(TAG, "Connection Suspended")
        mGoogleApiClient.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.errorCode)
    }

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {

        mLocation = location
        updateLog()


    }

    @SuppressLint("SetTextI18n")
    override fun onConnected(p0: Bundle?) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }


        startLocationUpdates()

        val fusedLocationProviderClient:
                FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    mLocation = location
                    updateLog()
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        SensorUtil.getGyroscopeData(this, object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent?) {
                this@MainActivity.gyroEvent = event
            }

        })
        SensorUtil.getCompassData(this, object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                this@MainActivity.CompassEvent = event
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

        })


        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        checkLocation()


        start?.setOnClickListener {

            if (ip?.text.toString().isNotEmpty() && timer?.text.toString().isNotEmpty()) {


                Thread {
                    socketUtil = SocketUtil().apply {
                        init(
                            this@MainActivity
                            , ip.text.toString().split(":").first()
                            , ip.text.toString().split(":").last().toInt()
                        )
                    }

                }.start()

                Timer = Timer().apply {
                    scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            updateLog()
                        }
                    }, 0L, timer?.text.toString().toLong())
                }

            } else
                Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show()
        }


    }

    private fun checkLocation(): Boolean {
        if (!isLocationEnabled())
            showAlert()
        return isLocationEnabled()
    }

    private fun isLocationEnabled(): Boolean {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
            .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
            .setPositiveButton("Location Settings") { _, _ ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
            }
            .setNegativeButton("Cancel") { _, _ -> }
        dialog.show()
    }

    private fun startLocationUpdates() {

        // Create the location request
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL)
            .setFastestInterval(FASTEST_INTERVAL)
        // Request location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient,
            mLocationRequest, this
        )
    }

    @SuppressLint("SetTextI18n")
    private fun updateLog() {

        if (isActive) {
            runOnUiThread {

                locationLog?.text = "Accuracy : ${mLocation.accuracy}\n" +
                        "Latitude : ${mLocation.latitude}\n" +
                        "Longitude : ${mLocation.longitude}\n" +
                        "Time : ${mLocation.time}\n" +
                        "Speed : ${mLocation.speed}"


                gyroLog?.text =
                    "X : ${gyroEvent?.values?.get(0)}" +
                            " , Y : ${gyroEvent?.values?.get(1)}" +
                            " , Z : ${gyroEvent?.values?.get(2)}"

                compassData?.text =
                    "X : ${CompassEvent?.values?.get(0)}" +
                            " , Y : ${CompassEvent?.values?.get(1)}" +
                            " , Z : ${CompassEvent?.values?.get(2)}"

            }

            Thread {
                socketUtil?.sendData(Sender().apply {

                    gyroData = gyroEvent?.values
                    compassData = CompassEvent?.values

                    location = Location().apply {
                        accuracy = mLocation.accuracy
                        latitude = mLocation.latitude
                        longitude = mLocation.longitude
                        time = mLocation.time
                        speed = mLocation.speed
                    }
                })
            }.start()
        }


    }

    override fun onDestroy() {
        try {
            socketUtil?.terminate()
            Timer?.cancel()
            Timer?.purge()
        } catch (e: Exception) {
        }

        isActive = false
        super.onDestroy()
    }
}