package com.driver.sagardriverapp.utils

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.repository.MyRideChildRepository
import com.driver.sagardriverapp.uis.HomeActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SchoolDriverLocationService : Service() {

    @Inject
    lateinit var myRideChildRepository: MyRideChildRepository
    private var bookingId: String? = null
    private var tripLocId: String? = null
    private var tripStatus: String? = null
    private var driver_location_title: String? = null
    private var driver_address: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Define the LocationCallback as a property
    private lateinit var locationCallback: LocationCallback

    private val locationRequest = LocationRequest.create().apply {
        interval = 10000  // Update every 10 seconds
        fastestInterval = 5000  // Fastest update interval (5 seconds)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Retrieve the data from the Intent
        bookingId = intent?.getStringExtra("booking_id")
        tripLocId = intent?.getStringExtra("trip_loc_id")


        startForeground(1, createNotification())
        startLocationUpdates()
        return START_NOT_STICKY
    }

    private fun startLocationUpdates() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                location?.let { updateLocationToServer(it) } // Send location to server
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun updateLocationToServer(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        // TODO: Make an API call to update the driver's location to the server
        val token = "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)
        val userId = SharedPref.readString(SharedPref.USER_ID)


        // Make sure bookingId, tripLocId, and tripStatus are available
        if (bookingId != null && tripLocId != null ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = myRideChildRepository.updateSchoolDriverLocation(
                        token,
                        /*bookingId!!,*/
                        userId,
                        latitude.toString(),
                        longitude.toString(),
                        tripLocId!!
                    )
                    // Handle the response here
                    when (response) {
                        is Response.Success -> {
                            // Access the response data
                            val responseData = response.responseData
                            val message = responseData?.message
                            val status = responseData?.status

                            // Log or handle the response as needed
                            Log.d("LocationService", "Location updated successfully: $message, Status: $status")
                            // Optionally, access additional data if needed
                            val updatedData = responseData?.data
                            updatedData?.forEach { updateDriverLocation ->
                                // Handle each updateDriverLocation object as needed
                                Log.d("LocationService", "Updated Driver Location: $updateDriverLocation")
                            }
                        }
                        is Response.Error -> {
                            // Handle the error response
                            Log.e("LocationService", "Error updating location: ${response.errorBody?.errorMessages}")
                        }

                        is Response.Loading -> {

                        }
                    }
                } catch (e: Exception) {
                    Log.e("LocationService", "Exception: ${e.message}")
                }
            }
        } else {
            Log.e("LocationService", "Missing bookingId, tripLocId")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val notificationChannelId = "location_service_channel"
        val channel = NotificationChannel(
            notificationChannelId,
            "Location Service",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notificationIntent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Tracking your location")
            .setContentText("Location updates in progress")
            .setSmallIcon(R.drawable.noti_icon)
            // .setSmallIcon(R.drawable.ic_location)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // No binding
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove location updates using the same LocationCallback instance
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}