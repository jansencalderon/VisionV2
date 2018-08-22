package com.tip.itso.z.vision

import android.content.Intent
import android.os.IBinder
import android.support.v4.content.ContextCompat


import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.berico.coords.Coordinates
import com.google.android.gms.location.*
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.mapboxsdk.geometry.LatLng
import gov.nasa.worldwind.geom.coords.MGRSCoord
import org.jetbrains.anko.notificationManager
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.LongFunction
import kotlin.collections.ArrayList
import kotlin.coroutines.experimental.coroutineContext
import kotlin.math.roundToInt

class LocationTrackingService : Service() {

    var locationManager: LocationManager? = null
    var isSmsLocationSendingRunning: Boolean = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    val smsLocationSendingDelay: Long = 20000
    override fun onCreate() {
        super.onCreate()
        requestLocationUpdates()
        buildNotification()

        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (isSmsLocationSendingRunning) {
                    if (DataManager.isSessionInProgress == true && DataManager.isSMSLocationSendingOn == true) {
                        runSmsLocationUpdates()
                    }

                    handler.postDelayed(this, smsLocationSendingDelay)
                } else {
                    if (DataManager.isSessionInProgress == true && DataManager.isSMSLocationSendingOn == true) {
                        runSmsLocationUpdates()
                    }
                    handler.postDelayed(this, 1000)
                }
            }
        }

        handler.postDelayed(runnable, 0)
    }

    var locationList: ArrayList<String>? = ArrayList()
    //var counter:Int = 0;
    fun runSmsLocationUpdates() {
        Log.d("mysms", "SMS SENDING")
        if (DataManager.isSMSLocationSendingOn == true) {
            if (MLocation.isLocationInitiated === true) {
                isSmsLocationSendingRunning = true
                val currentTimestamp = Timestamp(System.currentTimeMillis())
                val tsLong = System.currentTimeMillis() / 1000
                val ts = tsLong.toString()


                var locDataToSend: String = "loc-" +
                        Coordinates.mgrsFromLatLon(MLocation.currentlatLng.latitude, MLocation.currentlatLng.longitude) +
                        "-" + MLocation.accuracy.roundToInt() + "-" + ts + "-" + DataManager.currentSessionNumber.toString();
                Log.d("mysms", "real accuracy: " + MLocation.accuracy)
                if (!DataManager.lastSentLocation.equals(locDataToSend)) {
                    locationList!!.add(locDataToSend);
                    DataManager.lastSentLocation = locDataToSend

                    if (locationList!!.size >= 4) {
                        var combinedLocationList: String = locationList!!.joinToString(",")
                        Log.d("mysms", "message: " + combinedLocationList + " length: " + combinedLocationList.length)
                        sendSmsLocationUpdates(combinedLocationList)
                        locationList!!.clear()
                    }
                }
            }
        } else {
            isSmsLocationSendingRunning = false
        }
    }

    fun sendSmsLocationUpdates(messageToSend: String) {
        //POSTPAID
        //SmsManager.getDefault().sendTextMessage("+639177089902", null,messageToSend, null, null)
        //PREPAID
        try {
            SmsManager.getDefault().sendTextMessage(DataManager.destinationNumber, null, messageToSend, null, null)
        } catch (e: Exception) {
            Toast.makeText(this, "Ooops, an error ocurred", Toast.LENGTH_SHORT).show()
        }


    }

    fun buildNotification() {
        val builder: NotificationCompat.Builder

        // Create an Intent for the activity you want to start
        var intent = Intent(this, MainActivity::class.java)
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        var stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(intent)
// Get the PendingIntent containing the entire back stack
        val resultPendingIntent: PendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val CHANNEL_ID = "EXAMPLE_CHANNEL_ID"

            val notificationChannel = NotificationChannel(CHANNEL_ID,
                    getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            notificationManager.createNotificationChannel(notificationChannel)
            builder = NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Location Services Running")
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.logo_solo)
                    //.setContentIntent(resultPendingIntent)
                    .setChannelId(CHANNEL_ID)
        } else {
            builder = NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Location Services Running")
                    .setOngoing(true)
                   // .setContentIntent(resultPendingIntent)
                    .setSmallIcon(R.drawable.logo_solo)
        }

        startForeground(1, builder.build())
    }

    protected var stopReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            unregisterReceiver(this)
            stopSelf()
        }
    }

    fun requestLocationUpdates() {
        if (locationManager == null) {
            locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, INTERVAL, DISTANCE, locationListeners[1])
        } catch (e: SecurityException) {
            Log.e("LOCATION", "Fail to request location update", e)
        } catch (e: IllegalArgumentException) {
            Log.e("LOCATION", "Network provider does not exist", e)
        }

        try {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL, DISTANCE, locationListeners[0])
        } catch (e: SecurityException) {
            Log.e("LOCATION", "Fail to request location update", e)
        } catch (e: IllegalArgumentException) {
            Log.e("LOCATION", "GPS provider does not exist", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null)
            for (locationListener in locationListeners) { // <- fix
                try {
                    locationManager?.removeUpdates(locationListener)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to remove location listeners")
                }
            }
    }

    companion object {
        val TAG = "LocationTrackingService"

        val INTERVAL = 1000.toLong() // In milliseconds
        val DISTANCE = 5.toFloat() // In meters

        val locationListeners = arrayOf(
                LTRLocationListener(LocationManager.GPS_PROVIDER),
                LTRLocationListener(LocationManager.NETWORK_PROVIDER)
        )

        class LTRLocationListener(provider: String) : android.location.LocationListener {
            val lastLocation = Location(provider)

            override fun onLocationChanged(location: Location?) {
                // LOCATION CHANGED
                lastLocation.set(location)
                //MLocation.latitude = lastLocation.latitude
                //MLocation.longitude = lastLocation.longitude
                MLocation.currentlatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                MLocation.accuracy = location!!.accuracy
                MLocation.accurateLat = location.latitude
                MLocation.accurateLng = location.longitude
                MLocation.isLocationInitiated = true

                if (DataManager.isSessionInProgress == true && DataManager.isSMSLocationSendingOn == false) {
                    sendNetLocationUpdates(lastLocation!!.accuracy.roundToInt().toString(),
                            lastLocation.latitude.toString(), lastLocation.longitude.toString(), Coordinates.mgrsFromLatLon(lastLocation.latitude, lastLocation.longitude).toString())
                }

            }

            override fun onProviderDisabled(provider: String?) {

            }

            override fun onProviderEnabled(provider: String?) {

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }

            fun sendNetLocationUpdates(accuracy: String, latitude: String, longtitude: String, mgrs: String) {
                Log.d("mysms", "NET SENDING")
                if (DataManager.isSMSLocationSendingOn == false) {
                    if (DataManager.lastSentLocation != mgrs) {
                        addBaseLocation(accuracy, latitude, longtitude, mgrs)
                        addHistoryLocation(accuracy, latitude, longtitude, mgrs)
                        DataManager.lastSentLocation = mgrs
                    }

                }
            }

            fun addBaseLocation(accuracy: String, latitude: String, longtitude: String, mgrs: String) {
                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("listener-data/" + DataManager.destinationUID + "/locations/" + DataManager.phoneNumber)


                val currentTimestamp = Timestamp(System.currentTimeMillis())
                val millis = System.currentTimeMillis()
                val tsLong = millis / 1000
                val ts = tsLong.toString()

                myRef.child("mgrs").setValue(mgrs) //1
                myRef.child("accuracy").setValue(accuracy) //2
                myRef.child("sent").setValue(ts) //3
                myRef.child("latitude").setValue(latitude) //4
                myRef.child("longitude").setValue(longtitude) //5
                myRef.child("received").setValue(ts)
            }

            fun addHistoryLocation(accuracy: String, latitude: String, longtitude: String, mgrs: String) {
                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("listener-data/" + DataManager.destinationUID + "/location history/" + DataManager.phoneNumber)

                val currentTimestamp = Timestamp(System.currentTimeMillis())
                val millis = System.currentTimeMillis()
                val tsLong = millis / 1000
                val ts = tsLong.toString()

                val stamp = Timestamp(millis)
                val date = java.util.Date(stamp.time)

                var cal: Calendar = Calendar.getInstance()
                cal.time = date

                Log.d("HIS", date.toString())
                val year = cal.get(Calendar.YEAR)
                val month = cal.get(Calendar.MONTH) + 1
                val day = cal.get(Calendar.DAY_OF_MONTH)

                var monthString = ""
                if (month < 10) {
                    monthString = "0" + month
                } else {
                    monthString = month.toString()
                }

                var dayString = ""
                if (day < 10) {
                    dayString = "0" + day
                } else {
                    dayString = day.toString()
                }

                var dateString = year.toString() + monthString + dayString

                var keyString: String = myRef.child(dateString).push().key!!

                myRef.child(dateString).child(DataManager.sessionID).child(keyString).child("mgrs").setValue(mgrs) //1
                myRef.child(dateString).child(DataManager.sessionID).child(keyString).child("accuracy").setValue(accuracy) //2
                myRef.child(dateString).child(DataManager.sessionID).child(keyString).child("sent").setValue(ts) //3
                myRef.child(dateString).child(DataManager.sessionID).child(keyString).child("latitude").setValue(latitude) //4
                myRef.child(dateString).child(DataManager.sessionID).child(keyString).child("longitude").setValue(longtitude) //5
                myRef.child(dateString).child(DataManager.sessionID).child(keyString).child("received").setValue(ts)
            }
        }
    }
}
