package com.driver.sagardriverapp.uis.myRide

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.base.BaseActivity
import com.driver.sagardriverapp.databinding.ActivityMyRideChildBinding
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.SchoolDriverTripDetailResponse
import com.driver.sagardriverapp.model.UpdateDriverLocationResponse
import com.driver.sagardriverapp.uis.HomeActivity
import com.driver.sagardriverapp.uis.home.ChildListActivity
import com.driver.sagardriverapp.uis.home.TripDetailsWithTypeAdapter
import com.driver.sagardriverapp.uis.myRide.viewModels.MyRideChildViewModel
import com.driver.sagardriverapp.utils.AppConstants
import com.driver.sagardriverapp.utils.SchoolDriverLocationService
import com.driver.sagardriverapp.utils.SharedPref
import com.driver.sagardriverapp.utils.Util.launchActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

@AndroidEntryPoint
class MyRideChildActivity : BaseActivity(), OnMapReadyCallback {
    val kuwaitBounds = LatLngBounds(
        LatLng(28.5246, 46.5527), // Southwest corner
        LatLng(30.0935, 48.4326)  // Northeast corner
    )
    var bundle: Bundle? = null
    var ride_id: String? = null
    var booking_id: String? = null
    var trip_loc_id: String? = null
    var source_lat: Double = 0.0
    var source_lng: Double = 0.0
    var des_lat: Double = 0.0
    var des_lng: Double = 0.0
    private var routePoints: List<LatLng> = emptyList()

    var startLocation: LatLng?=null
    var endLocation: LatLng?=null

    private lateinit var mMap: GoogleMap
    var school_dialor: String? = null
    var emergency_dialor: String? = null


    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentMarker: Marker? = null
    private val client = OkHttpClient()
    private var routePolyline: Polyline? = null

    private val viewModels: MyRideChildViewModel by viewModels<MyRideChildViewModel>()

    lateinit var binding: ActivityMyRideChildBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyRideChildBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    private fun initUis() = binding.apply {

        // Initialize the fused location client
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@MyRideChildActivity)

        // Initialize the map fragment
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
        mapFragment.getMapAsync(this@MyRideChildActivity)

        bundle = intent.extras

        if (bundle != null) {


            ride_id = bundle?.getString("ride_id").toString()

        }

        rvTripsTimeEtc.layoutManager = LinearLayoutManager(this@MyRideChildActivity)
        rvTripsTimeEtc.adapter = TripDetailsWithTypeAdapter(this@MyRideChildActivity)

        val bottomSheetRideForChild = BottomSheetBehavior.from(bottomSheetRideForChild)
        //bottomSheetRideForChild.peekHeight = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheetRideForChild.state = BottomSheetBehavior.STATE_EXPANDED

        bottomSheetRideForChild.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    /* BottomSheetBehavior.STATE_EXPANDED -> {
                         // When fully expanded, set a new peek height or keep it unchanged
                         bottomSheetBehavior.peekHeight = 300 // or any desired height
                     }
                     BottomSheetBehavior.STATE_COLLAPSED -> {
                         // When collapsed (slid to the bottom), set the peek height to minimum or desired value
                         bottomSheetBehavior.peekHeight = 100 // set the desired peek height when collapsed
                     }
                     BottomSheetBehavior.STATE_HIDDEN -> {
                         // Optional: if you want to hide the BottomSheet completely
                         bottomSheetBehavior.peekHeight = 0
                     }*/
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val displayMetrics = resources.displayMetrics
                val screenHeight = displayMetrics.heightPixels
                bottomSheetRideForChild.peekHeight =
                    screenHeight / 3 // Set peek height to half of the screen height
                // Optional: you can respond to slide events here if needed
            }
        })


        btViewChildList.setOnClickListener {
            val bundle = Bundle()
            bundle?.putString("ride_id", ride_id.toString())
            launchActivity(
                ChildListActivity::class.java,
                removeAllPrevActivities = false,
                bundle = bundle
            )

        }
        btEndTrip.setOnClickListener {
            endChildTripApi()

        }
        btReport.setOnClickListener {
            val bundle = Bundle()
            bundle?.putString("ride_id", ride_id.toString())
            launchActivity(
                ReportIssueActivity::class.java, removeAllPrevActivities = false,
                bundle = bundle
            )
        }

        tvCallSchool.setOnClickListener {
            if (school_dialor?.isNotEmpty() == true) {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$school_dialor")
                startActivity(dialIntent)
            }
        }
        tvEmergency.setOnClickListener {
            if (emergency_dialor?.isNotEmpty() == true) {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$emergency_dialor")
                startActivity(dialIntent)
            }
        }

        schoolDriverTripDetailApi()
        apiObserver()

    }

    fun schoolDriverTripDetailApi() {
        showDialog()
        viewModels.tripDetailsForSChoolDriverTrips(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            ride_id.toString()
        )

    }

    fun endChildTripApi() {
        showDialog()
        viewModels.endChildTrip(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            ride_id.toString()
        )

    }

    fun apiObserver() {
        viewModels.schoolDriverTripDetailResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is SchoolDriverTripDetailResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {

                                booking_id= data.data?.get(0)?.bookingId.toString()
                                trip_loc_id= data.data?.get(0)?.id.toString()
                                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {

                                    binding.tvSchoolName.text = data.data?.get(0)?.school?.name
                                    binding.tvCurrentLocation.text =
                                        data.data?.get(0)?.startLocationTitle
                                    binding.tvCurrAdd.text = data.data?.get(0)?.startLocationAddress
                                    binding.tvDestination.text = data.data?.get(0)?.endLocationTitle
                                    binding.tvDesAddress.text =
                                        data.data?.get(0)?.endLocationAddress
                                }

                                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {

                                    binding.tvSchoolName.text = data.data?.get(0)?.school?.nameAr
                                    binding.tvCurrentLocation.text =
                                        data.data?.get(0)?.startLocationTitleAr
                                    binding.tvCurrAdd.text = data.data?.get(0)?.startLocationAddressAr
                                    binding.tvDestination.text = data.data?.get(0)?.endLocationTitleAr
                                    binding.tvDesAddress.text =
                                        data.data?.get(0)?.endLocationAddressAr
                                }
                                binding.tvExpectedTime.text = data.data?.get(0)?.expectedStartTime
                                binding.tvSchoolId.text =
                                    getString(R.string.school_id) + "" + data.data?.get(0)?.schoolId

                                source_lat = data.data?.get(0)?.startLatitude!!.toDouble()
                                source_lng = data.data?.get(0)?.startLongitude!!.toDouble()
                                des_lat = data.data?.get(0)?.endLatitude!!.toDouble()
                                des_lng = data.data?.get(0)?.endLongitude!!.toDouble()
                                startLocation = LatLng(source_lat, source_lng)
                                endLocation = LatLng(des_lat, des_lng) // Los Angeles

                                school_dialor = data.data?.get(0)?.school?.user?.mobile
                                emergency_dialor = data.data?.get(0)?.school?.schoolEmergency?.contact_no

                                startBackSer(des_lat,des_lng)
                            } else {
                                it.responseData.message?.let { it1 -> showToast(it1) }
                            }
                        }
                    }

                    hideDialog()

                }

                is Response.Loading -> {
                    try {
                        showDialog()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Response.Error -> {
                    hideDialog()
                    if (it.errorBody?.status == 401) {
                        showAuthenticateDialog()
                    } else if (it.errorBody?.message.toString().equals(AppConstants.UNAUTH)) {
                        showAuthenticateDialog()
                    }else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        }else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }

                    }
                }


            }
        })
        viewModels.endChildTripResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CommonResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                stopLocationService()
                                launchActivity(
                                    HomeActivity::class.java,
                                    removeAllPrevActivities = false
                                )

                            } else {
                                it.responseData.message?.let { it1 -> showToast(it1) }
                            }
                        }
                    }

                    hideDialog()

                }

                is Response.Loading -> {
                    try {
                        showDialog()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Response.Error -> {
                    hideDialog()
                    if (it.errorBody?.status == 401) {
                        showAuthenticateDialog()
                    }else if (it.errorBody?.message.toString().equals(AppConstants.UNAUTH)) {
                        showAuthenticateDialog()
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        }else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }

                    }
                }


            }
        })
        viewModels.updateDriverLocationResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateDriverLocationResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {

                            } else {
                                it.responseData.message?.let { it1 -> showToast(it1) }
                            }
                        }
                    }

                    hideDialog()

                }

                is Response.Loading -> {
                    try {
                        showDialog()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Response.Error -> {
                    hideDialog()
                    if (it.errorBody?.status == 401) {
                        showAuthenticateDialog()
                    }else if (it.errorBody?.message.toString().equals(AppConstants.UNAUTH)) {
                        showAuthenticateDialog()
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        }else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }

                    }
                }


            }
        })

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable the location layer if permission is granted
       /* if (!hasLocationPermission()) {
            requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
            return
        }*/
        if (!hasLocationWithServicePermission()) {
            requestLocationPermissionWithSer(LOCATION_PERMISSION_REQUEST_CODE)
            return
        } else {
            /* // Permissions already granted, start the map and the service
             val mapFragment = supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
             mapFragment.getMapAsync(this@MyRideActivity)
             startLocationService()*/
        }

        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                //mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 19f))
                /*currentMarker = mMap.addMarker(
                    MarkerOptions().position(currentLatLng).title("Current Location")
                )*/

                //updateLocationInfo(currentLatLng)
            }
        }
       // mMap.setLatLngBoundsForCameraTarget(kuwaitBounds)
       // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(kuwaitBounds, 100))

        // Get the current location of the device and set the position of the map
        // handler.post(locationUpdateRunnable)


        var startLocation = LatLng(source_lat, source_lng)
        var endLocation = LatLng(des_lat, des_lng) // Los Angeles

       // fetchAndDrawRoute(startLocation, endLocation)
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 15000 // 10 seconds

    @SuppressLint("MissingPermission")
    private val locationUpdateRunnable = object : Runnable {
        override fun run() {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    //mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 19f))
                    currentMarker = mMap.addMarker(
                        MarkerOptions().position(currentLatLng).title("Current Location")
                    )
                    // updateLocationInfo(currentLatLng)


                        endLocation?.let { it2 ->
                            updateDriverLocationAndCheckRoute(currentLatLng,
                                 it2, routePoints)
                        }

                    updateDriverLocation(currentLatLng)
                    updateDriverLocationAPi(it.latitude, it.longitude) // Call your API function
                }
            }
            handler.postDelayed(this, updateInterval) // Repeat this process
        }
    }

    private fun updateDriverLocationAndCheckRoute(driverLocation: LatLng, pickupLocation: LatLng, routePoints: List<LatLng>) {
        if (isDriverOffRoute(driverLocation, routePoints, 100.0)) {  // 100 meters as the threshold
            // Driver is off the route, so remove the existing route and fetch a new one
            routePolyline?.remove()
            fetchAndDrawRoute(driverLocation, pickupLocation)
        }
    }

    private fun isDriverOffRoute(driverLocation: LatLng, routePoints: List<LatLng>, threshold: Double): Boolean {
        for (point in routePoints) {
            val distance = FloatArray(1)
            Location.distanceBetween(
                driverLocation.latitude, driverLocation.longitude,
                point.latitude, point.longitude,
                distance
            )
            if (distance[0] < threshold) {
                return false  // Driver is still on the route
            }
        }
        return true  // Driver has jumped off the route
    }



    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(locationUpdateRunnable) // Stop updates
    }

    fun updateDriverLocationAPi(latitude: Double, longitude: Double) {
        //showDialog()

        viewModels.updateSchoolDriverLocation(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            /*booking_id.toString(),*/ SharedPref.readString(SharedPref.USER_ID),
            latitude.toString(), longitude.toString(), trip_loc_id.toString()
        )

    }

    /* private fun updateLocationInfo(latLng: LatLng) {
         val geocoder = Geocoder(this, Locale.getDefault())
         val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
         current_latitude = latLng.latitude
         current_longitute = latLng.longitude
         if (addresses!!.isNotEmpty()) {
             val address = addresses[0]
             val addressText = address.getAddressLine(0)
             val name = address.featureName
             binding.tvDriverLocation.text = name
             // binding.tvLocationAddress.text = addressText

         }
     }*/

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, proceed with location access
                    // Initialize the map fragment
                    val mapFragment =
                        supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
                    mapFragment.getMapAsync(this@MyRideChildActivity)
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {
                // Handle other permissions if necessary
            }
        }
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            var isPermissionGranted = true

            for (i in permissions.indices) {
                if (permissions[i] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[i] != PackageManager.PERMISSION_GRANTED
                ) {
                    isPermissionGranted = false
                }
                if (permissions[i] == Manifest.permission.FOREGROUND_SERVICE_LOCATION &&
                    grantResults[i] != PackageManager.PERMISSION_GRANTED
                ) {
                    isPermissionGranted = false
                }
                if (permissions[i] == Manifest.permission.POST_NOTIFICATIONS &&
                    grantResults[i] != PackageManager.PERMISSION_GRANTED
                ) {
                    isPermissionGranted = false
                }
            }

            if (isPermissionGranted) {
                // Permissions granted, proceed with map and service
                val mapFragment = supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
                mapFragment.getMapAsync(this@MyRideChildActivity)
            } else {
                Toast.makeText(this, "Location, service or notification permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchAndDrawRoute(startLocation: LatLng, endLocation: LatLng) {
        //mMap.addMarker(MarkerOptions().position(startLocation).title("Current Location"))
        mMap.addMarker(MarkerOptions().position(startLocation).title("Pickup Location"))
        mMap.addMarker(MarkerOptions().position(endLocation).title("Drop Location"))
        CoroutineScope(Dispatchers.IO).launch {
            val url = getDirectionsUrl(startLocation, endLocation)
            Log.d("URL OF ", url)
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseData = response.body?.string()
                responseData?.let {
                    val routePoints = parseDirections(it)
                    withContext(Dispatchers.Main) {
                        drawRoute(routePoints)
                    }
                }
            }
        }
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${dest.latitude},${dest.longitude}"

        val key = "key=${getString(R.string.google_search_key)}"
        val parameters = "$strOrigin&$strDest&$key"
        //val parameters = "$strOrigin&$strDest&$key"
        return "https://maps.googleapis.com/maps/api/directions/json?$parameters"
    }

    private fun parseDirections(response: String): List<LatLng> {
        Log.d("DirectionsResponse", response)

        val result = mutableListOf<LatLng>()
        try {
            val jsonResponse = JSONObject(response)
            val routes = jsonResponse.getJSONArray("routes")

            if (routes.length() == 0) {
                // No routes found
                runOnUiThread {
                    Toast.makeText(this, "No routes found", Toast.LENGTH_SHORT).show()
                }
                return result
            }

            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")

            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                result.addAll(PolyUtil.decode(points))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this, "Failed to parse directions", Toast.LENGTH_SHORT).show()
            }
        }

        return result
        /*val result = mutableListOf<LatLng>()
        try {
            val jsonResponse = JSONObject(response)
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")

            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                result.addAll(PolyUtil.decode(points))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result*/
    }

    /*private fun drawRoute(routePoints: List<LatLng>) {

        val polylineOptions = PolylineOptions()
            .addAll(routePoints)
            .width(10f)
            .color(android.graphics.Color.BLUE)
            .geodesic(true)

        mMap.addPolyline(polylineOptions)
    }*/
    private fun drawRoute(routePoints: List<LatLng>) {
        routePolyline?.remove()  // Remove the existing path

        val polylineOptions = PolylineOptions()
            .addAll(routePoints)
            .width(10f)
            .color(android.graphics.Color.BLUE)
            .geodesic(true)

        routePolyline = mMap.addPolyline(polylineOptions)  // Store the reference to the new polyline
    }

    private fun updateDriverLocation(currentLatLng: LatLng) {
        if (currentMarker == null) {
            // If the marker does not exist, create it
            currentMarker =
                mMap.addMarker(MarkerOptions().position(currentLatLng).title("Driver Location"))
        } else {
            // Animate marker movement to the new position
            animateMarker(
                currentMarker!!,
                currentLatLng,
                2000
            ) // 2000 ms for the animation duration
        }
    }


    private fun animateMarker(marker: Marker, targetPosition: LatLng, duration: Int) {
        val startPosition = marker.position
        val startTime = SystemClock.uptimeMillis()
        val handler = Handler(Looper.getMainLooper())

        val interpolator = LinearInterpolator()

        val runnable = object : Runnable {
            override fun run() {
                // Calculate elapsed time
                val elapsedTime = SystemClock.uptimeMillis() - startTime
                val t = Math.min(1.0f, elapsedTime.toFloat() / duration)

                // Interpolate marker position
                val lat =
                    interpolator.getInterpolation(t) * (targetPosition.latitude - startPosition.latitude) + startPosition.latitude
                val lng =
                    interpolator.getInterpolation(t) * (targetPosition.longitude - startPosition.longitude) + startPosition.longitude

                // Update marker position
                marker.position = LatLng(lat, lng)

                if (t < 1.0) {
                    handler.postDelayed(
                        this,
                        16
                    ) // Repeat until the animation is complete (approx 60fps)
                }
            }
        }

        handler.post(runnable)
    }
    fun startBackSer(pick_latitude: Double, pick_longitute: Double)
    {
        /* val serviceIntent = Intent(this, LocationService::class.java)
         ContextCompat.startForegroundService(this, serviceIntent)
 */

        val serviceIntent = Intent(this, SchoolDriverLocationService::class.java).apply {
            putExtra("booking_id", booking_id)
            putExtra("trip_loc_id", trip_loc_id)

        }
        ContextCompat.startForegroundService(this, serviceIntent)
        // Launch Google Maps navigation to the pickup location
       /* val gmmIntentUri = Uri.parse("google.navigation:q=$pick_latitude,$pick_longitute")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)*/
    }

    // Call this method to stop the service when the trip ends
    private fun stopLocationService() {
        val serviceIntent = Intent(this, SchoolDriverLocationService::class.java)
        stopService(serviceIntent)
    }


}