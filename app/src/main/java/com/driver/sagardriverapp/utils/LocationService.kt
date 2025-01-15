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
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.repository.MyRideRepository
import com.driver.sagardriverapp.uis.HomeActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.driver.sagardriverapp.R
import java.net.URL
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    @Inject
    lateinit var myRideRepository: MyRideRepository
    private var bookingId: String? = null
    private var tripLocId: String? = null
    private var tripStatus: String? = null
    private var driver_location_title: String? = null
    private var driver_address: String? = null

    private var pickupLocation: LatLng? = null

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
        tripStatus = intent?.getStringExtra("trip_status")
        driver_location_title = intent?.getStringExtra("driver_location_title")
        driver_address = intent?.getStringExtra("driver_address")

        // Get pickup location
        val pickupLat = intent?.getDoubleExtra("pickup_lat", 0.0) ?: 0.0
        val pickupLng = intent?.getDoubleExtra("pickup_lng", 0.0) ?: 0.0
        pickupLocation = LatLng(pickupLat, pickupLng)

        startForeground(1, createNotification())
        startLocationUpdates()
        return START_STICKY
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
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun updateLocationToServer(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        // TODO: Make an API call to update the driver's location to the server
        val token = "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)
        val userId = SharedPref.readString(SharedPref.USER_ID)


        // Make sure bookingId, tripLocId, and tripStatus are available
        if (bookingId != null && tripLocId != null && tripStatus != null) {
            var etaToPickup: String=""

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (tripStatus.equals("ontheway")) {
                        // Get ETA to pickup location
                        etaToPickup = pickupLocation?.let {
                            fetchETA(LatLng(latitude, longitude), it)
                        }.toString()

                        SharedPref.writeString("ETA",etaToPickup)
                    }

                    val response = myRideRepository.updateDriverLocation(
                        token,
                        bookingId!!,
                        userId,
                        latitude.toString(),
                        longitude.toString(),
                        tripLocId!!,
                        etaToPickup.toString(),
                        "",
                        tripStatus!!,
                        driver_location_title.toString(),
                        driver_address.toString()
                    )
                    // Handle the response here
                    when (response) {
                        is Response.Success -> {
                            // Access the response data
                            val responseData = response.responseData
                            val message = responseData?.message
                            val status = responseData?.status

                            // Log or handle the response as needed
                            Log.d(
                                "LocationService",
                                "Location updated successfully: $message, Status: $status"
                            )
                            // Optionally, access additional data if needed
                            val updatedData = responseData?.data
                            updatedData?.forEach { updateDriverLocation ->
                                // Handle each updateDriverLocation object as needed
                                Log.d(
                                    "LocationService",
                                    "Updated Driver Location: $updateDriverLocation"
                                )
                            }
                        }

                        is Response.Error -> {
                            // Handle the error response
                            Log.e(
                                "LocationService",
                                "Error updating location: ${response.errorBody?.errorMessages}"
                            )
                        }

                        is Response.Loading -> {

                        }
                    }
                } catch (e: Exception) {
                    Log.e("LocationService", "Exception: ${e.message}")
                }
            }
        } else {
            Log.e("LocationService", "Missing bookingId, tripLocId, or tripStatus")
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
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

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

    private suspend fun fetchETA(origin: LatLng, destination: LatLng): String? {
        return try {
            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=${origin.latitude},${origin.longitude}" +
                    "&destination=${destination.latitude},${destination.longitude}" +
                    "&key=${getString(R.string.google_search_key)}"
            val response = URL(url).readText()
            val jsonResponse = JSONObject(response)

            if (jsonResponse.optString("status") == "OK") {
                val legs = jsonResponse.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                legs.getJSONObject(0).getJSONObject("duration").optString("text")
            } else null
        } catch (e: Exception) {
            Log.e("LocationService", "Error fetching ETA: ${e.message}")
            null
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Remove location updates using the same LocationCallback instance
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}