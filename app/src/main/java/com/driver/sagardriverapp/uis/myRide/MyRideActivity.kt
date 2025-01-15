package com.driver.sagardriverapp.uis.myRide

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.base.BaseActivity
import com.driver.sagardriverapp.databinding.ActivityMyRideBinding
import com.driver.sagardriverapp.model.AcceptRidelResponse
import com.driver.sagardriverapp.model.StartTripResponse
import com.driver.sagardriverapp.model.TripDetailResponse
import com.driver.sagardriverapp.model.UpdateDriverLocationResponse
import com.driver.sagardriverapp.uis.HomeActivity
import com.driver.sagardriverapp.uis.home.TripDetailsWithTypeAdapter
import com.driver.sagardriverapp.uis.myRide.viewModels.MyRideViewModel
import com.driver.sagardriverapp.utils.AppConstants
import com.driver.sagardriverapp.utils.LocationService
import com.driver.sagardriverapp.utils.SharedPref
import com.driver.sagardriverapp.utils.Util
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
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class MyRideActivity : BaseActivity(), OnMapReadyCallback {
    val kuwaitBounds = LatLngBounds(
        LatLng(28.5246, 46.5527), // Southwest corner
        LatLng(30.0935, 48.4326)  // Northeast corner
    )
    lateinit var binding: ActivityMyRideBinding
    var bundle: Bundle? = null
    var where: String? = null
    var driverAddress: String? = null
    var driverLocationTitle: String? = null
    var trip_status: String? = null
    var trip_status_from_api: String? = null
    var for_first_time = true
    private var routePoints: List<LatLng> = emptyList()
    var driverLocation: LatLng? = null  // San Francisco
    var pickupLocation: LatLng? = null  // San Francisco
    var dropLocation: LatLng? = null  // San Francisco

    var booking_id: String? = null
    var contact_user: String? = null
    var country_code: String? = null
    var trip_loc_id: String? = null
    var trip_mode_from_api: String? = null
    var distance_covered: String? = null
    var distance_covered_unit: String? = null
    private var routePolyline: Polyline? = null
    var click = 0
    var current_latitude: Double = 0.0
    var pick_latitude: Double = 0.0
    var drop_latitude: Double = 0.0
    var current_longitute: Double = 0.0
    var pick_longitute: Double = 0.0
    var drop_longitute: Double = 0.0
    private lateinit var mMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentMarker: Marker? = null
    private val client = OkHttpClient()
    private lateinit var bottomSheetStart_Trip: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetForRide_Completed: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheet_Ride: BottomSheetBehavior<LinearLayout>

    private val viewModels: MyRideViewModel by viewModels<MyRideViewModel>()
    private val simulatedRoutePoints = listOf(
        LatLng(30.747926, 76.647310), // Starting point (Guru Homes Kharar)
        LatLng(30.748500, 76.648000), // Intermediate point 1
        LatLng(30.749000, 76.649500), // Intermediate point 2
        LatLng(30.750000, 76.650500), // Intermediate point 3
        LatLng(30.751000, 76.651000), // Intermediate point 4
        LatLng(30.752000, 76.652000), // Intermediate point 5
        LatLng(30.753000, 76.653000), // Intermediate point 6
        LatLng(30.754000, 76.654000), // Intermediate point 7
        LatLng(30.755000, 76.655000), // Intermediate point 8
        LatLng(30.755500, 76.655500), // Near the destination
        LatLng(30.756000, 76.656000), // Final point before destination
        LatLng(30.756500, 76.657000), // Approaching destination
        LatLng(30.757000, 76.657500), // Destination (Shaibjada Ajit Singh Nagar)
    )

    private var currentSimulatedIndex = 0
    private var isTesting = true // Set to true for testing, false for real updates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyRideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUis() = binding.apply {

        // Initialize the fused location client
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@MyRideActivity)

        // Initialize the map fragment
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
        mapFragment.getMapAsync(this@MyRideActivity)
        bundle = intent.extras

        if (bundle != null) {
            where = bundle?.getString("where")
            booking_id = bundle?.getString("booking_id")
        }

        bottomSheetStart_Trip = BottomSheetBehavior.from(bottomSheetStartTrip)
        bottomSheetForRide_Completed = BottomSheetBehavior.from(bottomSheetForRideCompleted)
        bottomSheet_Ride = BottomSheetBehavior.from(bottomSheetRide)


        // Debug logs to check the value of 'where'
        Log.d("BottomSheet", "Current state: $where")
        // Listen for state changes
        bottomSheet_Ride.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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
                bottomSheet_Ride.peekHeight =
                    screenHeight / 3 // Set peek height to half of the screen height
                // Optional: you can respond to slide events here if needed
            }
        })
        bottomSheetStart_Trip.addBottomSheetCallback(object :
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
                bottomSheetStart_Trip.peekHeight =
                    screenHeight / 3 // Set peek height to half of the screen height
                // Optional: you can respond to slide events here if needed
            }
        })
        bottomSheetForRide_Completed.addBottomSheetCallback(object :
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
                bottomSheetForRide_Completed.peekHeight =
                    screenHeight / 3 // Set peek height to half of the screen height
                // Optional: you can respond to slide events here if needed
            }
        })

        if (where.equals("accept_trip")) {
            // Ensure the view is properly initialized
            // if (::llCustomerDetail.isInitialized && ::btStartTrip.isInitialized && ::btAcceptRide.isInitialized) {
            llCustomerDetail.visibility = View.VISIBLE
            btStartTrip.visibility = View.VISIBLE
            btAcceptRide.visibility = View.GONE
            Log.d("BottomSheet", "Setting views for accept_trip")
            /*} else {
                Log.e("BottomSheet", "Views not initialized")
            }*/
        } else if (where.equals("view_trip")) {
            //  if (::llCustomerDetail.isInitialized && ::btStartTrip.isInitialized && ::btAcceptRide.isInitialized) {
            llCustomerDetail.visibility = View.GONE
            btStartTrip.visibility = View.GONE
            btAcceptRide.visibility = View.VISIBLE
            Log.d("BottomSheet", "Setting views for view_trip")
            /* } else {
                 Log.e("BottomSheet", "Views not initialized")
             }*/
        } else {
            Log.e("BottomSheet", "Unknown state: $where")
        }



        rvTripDetails.layoutManager = LinearLayoutManager(this@MyRideActivity)
        rvTripDetails.adapter = TripDetailsWithTypeAdapter(this@MyRideActivity)

        btStartTrip.setOnClickListener {
            trip_status = "ontheway"
            // bottomSheetRide.peekHeight = 0
            bottomSheet_Ride.setHideable(true)
            bottomSheetStart_Trip.setHideable(false)
            bottomSheetForRide_Completed.setHideable(true)

            bottomSheet_Ride.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetStart_Trip.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetForRide_Completed.state = BottomSheetBehavior.STATE_HIDDEN
            startTripApi()

        }

        btGo.setOnClickListener {

            trip_status = "ongoing"
            binding.btEnd.visibility = View.VISIBLE
            binding.btGo.visibility = View.GONE
            tripGoApi()

        }
        btEnd.setOnClickListener {
            trip_status = "completed"
            endTripApi()
            bottomSheet_Ride.setHideable(true)
            bottomSheetStart_Trip.setHideable(true)
            bottomSheetForRide_Completed.setHideable(false)

            bottomSheet_Ride.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetStart_Trip.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetForRide_Completed.state = BottomSheetBehavior.STATE_EXPANDED
        }

        btAcceptRide.setOnClickListener {
            acceptTripApi()
            /*launchActivity(
                HomeActivity::class.java, removeAllPrevActivities = false,
                bundle = bundle
            )*/
        }
        btViewTripsOnCompleted.setOnClickListener {
            launchActivity(
                HomeActivity::class.java, removeAllPrevActivities = false,
                bundle = bundle
            )
        }

        ivCall.setOnClickListener {
            if (contact_user?.isNotEmpty() == true) {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$contact_user")
                startActivity(dialIntent)
            }
        }
        ivcallForStart.setOnClickListener {
            if (contact_user?.isNotEmpty() == true) {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$contact_user")
                startActivity(dialIntent)
            }
        }
        ivWhatsapp.setOnClickListener {
            openWhatsAppChat(country_code + contact_user)
        }
        ivWhatsappForStart.setOnClickListener {
            openWhatsAppChat(country_code + contact_user)
        }
        tripDetailsApi()
        apisObservers()
    }

    fun openWhatsAppChat(toNumber: String) {
        // Remove '+' sign from the phone number if present
        val formattedNumber = toNumber.replace("+", "")

        // WhatsApp API URL with the formatted phone number
        val url = "https://api.whatsapp.com/send?phone=$formattedNumber"

        // Check if WhatsApp is installed
        val isWhatsAppInstalled: Boolean = try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        if (isWhatsAppInstalled) {
            // WhatsApp is installed, so launch it
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) })
        } else {
            // WhatsApp is not installed, show a toast
            Toast.makeText(this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT)
                .show()
        }
    }


    /*fun openWhatsAppChat(toNumber: String) {
        val url = "https://api.whatsapp.com/send?phone=$toNumber"
        try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) })
        } catch (e: PackageManager.NameNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))}}
*/
    fun tripDetailsApi() {
        showDialog()
        viewModels.tripDetail(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            booking_id.toString()
        )

    }

    fun tripGoApi() {
        showDialog()
        val serviceIntent = Intent(this, LocationService::class.java).apply {
            putExtra("booking_id", booking_id)
            putExtra("trip_loc_id", trip_loc_id)
            putExtra("trip_status", trip_status)
            putExtra("driver_location_title", driverLocationTitle)
            putExtra("driver_address", driverAddress)
            putExtra("pickup_lat", drop_latitude)
            putExtra("pickup_lng", drop_longitute)
        }
        startService(serviceIntent)
        viewModels.tripGo(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            booking_id.toString(),
            SharedPref.readString(SharedPref.USER_ID), trip_loc_id.toString()
        )

    }

    fun acceptTripApi() {
        showDialog()
        viewModels.acceptTrip(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            booking_id.toString(), SharedPref.readString(SharedPref.USER_ID)
        )

    }

    fun updateDriverLocationAPi(latitude: Double, longitude: Double) {
        //showDialog()

        if (driverLocation != null && pickupLocation != null) {
            if (isDriverAtPickup(driverLocation!!, pickupLocation!!)) {
                binding.ivPickLocation.setImageResource(R.drawable.cus_loc_icon_blue)
                binding.btGo.setBackgroundResource(R.drawable.solid_green_10)
                // Driver has arrived at pickup location, update status
            }
        }

        viewModels.updateDriverLocation(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            booking_id.toString(), SharedPref.readString(SharedPref.USER_ID),
            latitude.toString(), longitude.toString(), trip_loc_id.toString(),
            "", "", trip_status.toString(), driverLocationTitle.toString(), driverAddress.toString()
        )

    }

    fun startTripApi() {
        showDialog()
        viewModels.startTrip(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            booking_id.toString(), SharedPref.readString(SharedPref.USER_ID), trip_loc_id.toString()
        )

    }

    fun endTripApi() {
        showDialog()
        val serviceIntent = Intent(this, LocationService::class.java).apply {
            putExtra("booking_id", booking_id)
            putExtra("trip_loc_id", trip_loc_id)
            putExtra("trip_status", trip_status)
            putExtra("driver_location_title", driverLocationTitle)
            putExtra("driver_address", driverAddress)
            putExtra("pickup_lat", drop_latitude)
            putExtra("pickup_lng", drop_longitute)
        }
        startService(serviceIntent)
        viewModels.endTrip(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            booking_id.toString(), SharedPref.readString(SharedPref.USER_ID),
            trip_loc_id.toString(), distance_covered.toString(), distance_covered_unit.toString()
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun apisObservers() {
        viewModels.tripDetailResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is TripDetailResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                binding.tvcustomerName.text = data.data?.get(0)?.user?.firstName
                                binding.tvBookingId.text =
                                    getString(R.string.booking_id) + "" + data.data?.get(0)?.bookingId
                                if (data.data?.get(0)?.discountPrice == null) {
                                    binding.tvPrice.text =
                                        data.data?.get(0)?.priceUnit + " " + data.data?.get(0)?.totalPrice
                                }
                                else {
                                    binding.tvPrice.text =
                                        data.data?.get(0)?.priceUnit + " " + data.data?.get(0)?.price
                                }
                                if (data.data?.get(0)?.tripMode.equals("0")) {
                                    binding.tvTripMode.text = getString(R.string.one_way)
                                }
                                if (data.data?.get(0)?.tripMode.equals("1")) {
                                    binding.tvTripMode.text = getString(R.string.round_trip)
                                }
                                country_code = data.data?.get(0)?.user?.countryCode
                                contact_user = data.data?.get(0)?.user?.mobile
                                trip_mode_from_api = data.data?.get(0)?.tripMode

                                if (trip_mode_from_api.equals("0")) {
                                    binding.tvKm.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.distance + "" + data.data?.get(
                                            0
                                        )?.triplocations?.get(0)?.distanceUnit
                                    binding.tvDropLoc.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.dropLocationTitle
                                    binding.tvPickLocation.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.pickupLocationTitle

                                    trip_loc_id =
                                        data.data?.get(0)?.triplocations?.get(0)?.id.toString()
                                    distance_covered =
                                        data.data?.get(0)?.triplocations?.get(0)?.distance
                                    distance_covered_unit =
                                        data.data?.get(0)?.triplocations?.get(0)?.distanceUnit

                                    binding.tripStatus.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.tripStatus

                                    binding.tvCurrentAddress.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.pickupLocation
                                    binding.tvDropAddress.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.dropLocation
                                    binding.tvCurrentLocation.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.pickupLocationTitle
                                    binding.tvDestination.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.dropLocationTitle
                                    binding.tvPickUpTime.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.startTime + " " + data.data?.get(
                                            0
                                        )?.triplocations?.get(0)?.startTimeUnit
                                    binding.tvPickUpdate.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.startDate


                                    pick_latitude =
                                        data.data?.get(0)?.triplocations?.get(0)?.pickupLatitude!!.toDouble()
                                    drop_latitude =
                                        data.data?.get(0)?.triplocations?.get(0)?.dropLatitude!!.toDouble()
                                    pick_longitute =
                                        data.data?.get(0)?.triplocations?.get(0)?.pickupLongitude!!.toDouble()
                                    drop_longitute =
                                        data.data?.get(0)?.triplocations?.get(0)?.dropLongitude!!.toDouble()
                                    pickupLocation =
                                        LatLng(pick_latitude, pick_longitute) // Los Angeles
                                    dropLocation =
                                        LatLng(drop_latitude, drop_longitute) // Las Vegas

                                    var shouldEnableStart = Util.shouldEnableStartRideButton(
                                        pickupDate = data.data?.get(0)?.triplocations?.get(0)?.startDate.toString(),
                                        pickupTime = data.data?.get(0)?.triplocations?.get(0)?.startTime.toString(),
                                        timeUnit = data.data?.get(0)?.triplocations?.get(0)?.startTimeUnit.toString()
                                    )
                                     /*var shouldEnableStart=Util.shouldEnableStartRideButton(
                                         pickupDate = "2024-11-14",
                                         pickupTime = "04:30:00",
                                         timeUnit = "pm",
                                     )*/
                                   if (data.data?.get(0)?.triplocations?.get(0)?.enable_button == true) {
                                        binding.btStartTrip.setBackgroundResource(R.drawable.solid_green_10)
                                        binding.btStartTrip.isEnabled = true
                                        binding.llCustomerDetail.visibility = View.VISIBLE
                                    } else {
                                        binding.btStartTrip.setBackgroundResource(R.drawable.solid_gray)
                                        binding.btStartTrip.isEnabled = false
                                        binding.llCustomerDetail.visibility = View.GONE


                                    }/*if (shouldEnableStart) {
                                        binding.btStartTrip.setBackgroundResource(R.drawable.solid_green_10)
                                        binding.btStartTrip.isEnabled = true
                                        binding.llCustomerDetail.visibility = View.VISIBLE
                                    } else {
                                        binding.btStartTrip.setBackgroundResource(R.drawable.solid_gray)
                                        binding.btStartTrip.isEnabled = false
                                        binding.llCustomerDetail.visibility = View.GONE


                                    }*/
                                    trip_status_from_api =
                                        data.data?.get(0)?.triplocations?.get(0)?.tripStatus

                                    fetchAndDrawRoute(
                                        pickupLocation!!,
                                        dropLocation!!
                                    )
                                }

                                if (trip_mode_from_api.equals("1")) {
                                    if (data.data?.get(0)?.triplocations?.get(0)?.tripStatus.equals(
                                            "completed"
                                        )
                                    ) {
                                        var shouldEnableStart = Util.shouldEnableStartRideButton(
                                            pickupDate = data.data?.get(0)?.triplocations?.get(1)?.startDate.toString(),
                                            pickupTime = data.data?.get(0)?.triplocations?.get(1)?.startTime.toString(),
                                            timeUnit = data.data?.get(0)?.triplocations?.get(1)?.startTimeUnit.toString()
                                        )
                                        /*var shouldEnableStart=Util.shouldEnableStartRideButton(
                                            pickupDate = "2024-10-29",
                                            pickupTime = "03:30:00",
                                            timeUnit = "pm",
                                        )*/
                                        binding.tvKm.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.distance + "" + data.data?.get(
                                                0
                                            )?.triplocations?.get(0)?.distanceUnit
                                        binding.tvDropLoc.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.dropLocationTitle
                                        binding.tvPickLocation.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.pickupLocationTitle

                                        if (data.data?.get(0)?.triplocations?.get(1)?.enable_button == true) {
                                            binding.btStartTrip.setBackgroundResource(R.drawable.solid_green_10)
                                            binding.btStartTrip.isEnabled = true
                                            binding.llCustomerDetail.visibility = View.VISIBLE

                                        } else {
                                            binding.btStartTrip.setBackgroundResource(R.drawable.solid_gray)
                                            binding.btStartTrip.isEnabled = false
                                            binding.llCustomerDetail.visibility = View.GONE
                                        }/* if (shouldEnableStart) {
                                            binding.btStartTrip.setBackgroundResource(R.drawable.solid_green_10)
                                            binding.btStartTrip.isEnabled = true
                                            binding.llCustomerDetail.visibility = View.VISIBLE

                                        } else {
                                            binding.btStartTrip.setBackgroundResource(R.drawable.solid_gray)
                                            binding.btStartTrip.isEnabled = false
                                            binding.llCustomerDetail.visibility = View.GONE
                                        }*/

                                        trip_loc_id =
                                            data.data?.get(0)?.triplocations?.get(1)?.id.toString()
                                        distance_covered =
                                            data.data?.get(0)?.triplocations?.get(1)?.distance
                                        distance_covered_unit =
                                            data.data?.get(0)?.triplocations?.get(1)?.distanceUnit

                                        binding.tripStatus.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.tripStatus

                                        binding.tvCurrentAddress.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.pickupLocation
                                        binding.tvDropAddress.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.dropLocation
                                        binding.tvCurrentLocation.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.pickupLocationTitle
                                        binding.tvDestination.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.dropLocationTitle
                                        binding.tvPickUpTime.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.startTime + " " + data.data?.get(
                                                0
                                            )?.triplocations?.get(1)?.startTimeUnit
                                        binding.tvPickUpdate.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.startDate

                                        trip_status_from_api =
                                            data.data?.get(0)?.triplocations?.get(1)?.tripStatus

                                        pick_latitude =
                                            data.data?.get(0)?.triplocations?.get(1)?.pickupLatitude!!.toDouble()
                                        drop_latitude =
                                            data.data?.get(0)?.triplocations?.get(1)?.dropLatitude!!.toDouble()
                                        pick_longitute =
                                            data.data?.get(0)?.triplocations?.get(1)?.pickupLongitude!!.toDouble()
                                        drop_longitute =
                                            data.data?.get(0)?.triplocations?.get(1)?.dropLongitude!!.toDouble()
                                        pickupLocation =
                                            LatLng(pick_latitude, pick_longitute) // Los Angeles
                                        dropLocation =
                                            LatLng(drop_latitude, drop_longitute) // Las Vegas
                                        fetchAndDrawRoute(
                                            pickupLocation!!,
                                            dropLocation!!
                                        )
                                    } else {
                                        binding.tvKm.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.distance + "" + data.data?.get(
                                                0
                                            )?.triplocations?.get(0)?.distanceUnit
                                        binding.tvDropLoc.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.dropLocationTitle
                                        binding.tvPickLocation.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.pickupLocationTitle
                                        var shouldEnableStart = Util.shouldEnableStartRideButton(
                                            pickupDate = data.data?.get(0)?.triplocations?.get(0)?.startDate.toString(),
                                            pickupTime = data.data?.get(0)?.triplocations?.get(0)?.startTime.toString(),
                                            timeUnit = data.data?.get(0)?.triplocations?.get(0)?.startTimeUnit.toString()
                                        )
                                        /*var shouldEnableStart=Util.shouldEnableStartRideButton(
                                            pickupDate = "2024-10-29",
                                            pickupTime = "03:30:00",
                                            timeUnit = "pm",
                                        )*/
                                        if (data.data?.get(0)?.triplocations?.get(0)?.enable_button == true) {
                                            binding.btStartTrip.setBackgroundResource(R.drawable.solid_green_10)
                                            binding.btStartTrip.isEnabled = true
                                            binding.llCustomerDetail.visibility = View.VISIBLE

                                        } else {
                                            binding.btStartTrip.setBackgroundResource(R.drawable.solid_gray)
                                            binding.btStartTrip.isEnabled = false
                                            binding.llCustomerDetail.visibility = View.GONE
                                        }/* if (shouldEnableStart) {
                                            binding.btStartTrip.setBackgroundResource(R.drawable.solid_green_10)
                                            binding.btStartTrip.isEnabled = true
                                            binding.llCustomerDetail.visibility = View.VISIBLE

                                        } else {
                                            binding.btStartTrip.setBackgroundResource(R.drawable.solid_gray)
                                            binding.btStartTrip.isEnabled = false
                                            binding.llCustomerDetail.visibility = View.GONE
                                        }*/
                                        trip_loc_id =
                                            data.data?.get(0)?.triplocations?.get(0)?.id.toString()
                                        distance_covered =
                                            data.data?.get(0)?.triplocations?.get(0)?.distance
                                        distance_covered_unit =
                                            data.data?.get(0)?.triplocations?.get(0)?.distanceUnit

                                        binding.tripStatus.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.tripStatus

                                        binding.tvCurrentAddress.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.pickupLocation
                                        binding.tvDropAddress.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.dropLocation
                                        binding.tvCurrentLocation.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.pickupLocationTitle
                                        binding.tvDestination.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.dropLocationTitle
                                        binding.tvPickUpTime.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.startTime + " " + data.data?.get(
                                                0
                                            )?.triplocations?.get(0)?.startTimeUnit
                                        binding.tvPickUpdate.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.startDate


                                        trip_status_from_api =
                                            data.data?.get(0)?.triplocations?.get(0)?.tripStatus

                                        pick_latitude =
                                            data.data?.get(0)?.triplocations?.get(0)?.pickupLatitude!!.toDouble()
                                        drop_latitude =
                                            data.data?.get(0)?.triplocations?.get(0)?.dropLatitude!!.toDouble()
                                        pick_longitute =
                                            data.data?.get(0)?.triplocations?.get(0)?.pickupLongitude!!.toDouble()
                                        drop_longitute =
                                            data.data?.get(0)?.triplocations?.get(0)?.dropLongitude!!.toDouble()
                                        pickupLocation =
                                            LatLng(pick_latitude, pick_longitute) // Los Angeles
                                        dropLocation =
                                            LatLng(drop_latitude, drop_longitute) // Las Vegas
                                        fetchAndDrawRoute(
                                            pickupLocation!!,
                                            dropLocation!!
                                        )
                                    }

                                }


                                managescreenbottoms()
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
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        } else if (it.errorBody?.error != null) {
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
                                //showToast(data.message)
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
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        } else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }

                    }
                }
            }
        })
        viewModels.startTripResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is StartTripResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                binding.tvStartTripBookingId.text =
                                    getString(R.string.booking_id) + "" +
                                            data.data?.get(0)?.bookingId
                                binding.tvUserName.text = data.data?.get(0)?.user?.firstName



                                Log.d("LOcations", pickupLocation.toString())
                                Log.d("LOcations", dropLocation.toString())
                                //trip_status_from_api=data.data?.get(0)?.triplocations?.get(0)?.tripStatus
                                trip_mode_from_api = data.data?.get(0)?.tripMode
                                if (trip_mode_from_api.equals("0")) {
                                    binding.tvKm.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.distance + "" + data.data?.get(
                                            0
                                        )?.triplocations?.get(0)?.distanceUnit
                                    binding.tvDropLoc.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.dropLocationTitle
                                    pick_latitude =
                                        data.data?.get(0)?.triplocations?.get(0)?.pickupLatitude!!.toDouble()
                                    drop_latitude =
                                        data.data?.get(0)?.triplocations?.get(0)?.dropLatitude!!.toDouble()
                                    pick_longitute =
                                        data.data?.get(0)?.triplocations?.get(0)?.pickupLongitude!!.toDouble()
                                    drop_longitute =
                                        data.data?.get(0)?.triplocations?.get(0)?.dropLongitude!!.toDouble()
                                    binding.tvPickLocation.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.pickupLocationTitle

                                    // Replace these with actual coordinates
                                    driverLocation = LatLng(current_latitude, current_longitute)
                                    pickupLocation =
                                        LatLng(pick_latitude, pick_longitute) // Los Angeles
                                    dropLocation =
                                        LatLng(drop_latitude, drop_longitute) // Las Vegas

                                    trip_status_from_api =
                                        data.data?.get(0)?.triplocations?.get(0)?.tripStatus
                                }

                                if (trip_mode_from_api.equals("1")) {
                                    if (data.data?.get(0)?.triplocations?.get(0)?.tripStatus.equals(
                                            "completed"
                                        )
                                    ) {
                                        binding.tvKm.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.distance + "" + data.data?.get(
                                                0
                                            )?.triplocations?.get(1)?.distanceUnit
                                        binding.tvDropLoc.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.dropLocationTitle
                                        pick_latitude =
                                            data.data?.get(0)?.triplocations?.get(1)?.pickupLatitude!!.toDouble()
                                        drop_latitude =
                                            data.data?.get(0)?.triplocations?.get(1)?.dropLatitude!!.toDouble()
                                        pick_longitute =
                                            data.data?.get(0)?.triplocations?.get(1)?.pickupLongitude!!.toDouble()
                                        drop_longitute =
                                            data.data?.get(0)?.triplocations?.get(1)?.dropLongitude!!.toDouble()
                                        binding.tvPickLocation.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.pickupLocationTitle

                                        // Replace these with actual coordinates
                                        driverLocation = LatLng(current_latitude, current_longitute)
                                        pickupLocation =
                                            LatLng(pick_latitude, pick_longitute) // Los Angeles
                                        dropLocation =
                                            LatLng(drop_latitude, drop_longitute) // Las Vegas

                                        trip_status_from_api =
                                            data.data?.get(0)?.triplocations?.get(1)?.tripStatus
                                    } else {
                                        binding.tvKm.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.distance + "" + data.data?.get(
                                                0
                                            )?.triplocations?.get(0)?.distanceUnit
                                        binding.tvDropLoc.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.dropLocationTitle
                                        pick_latitude =
                                            data.data?.get(0)?.triplocations?.get(0)?.pickupLatitude!!.toDouble()
                                        drop_latitude =
                                            data.data?.get(0)?.triplocations?.get(0)?.dropLatitude!!.toDouble()
                                        pick_longitute =
                                            data.data?.get(0)?.triplocations?.get(0)?.pickupLongitude!!.toDouble()
                                        drop_longitute =
                                            data.data?.get(0)?.triplocations?.get(0)?.dropLongitude!!.toDouble()
                                        binding.tvPickLocation.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.pickupLocationTitle

                                        // Replace these with actual coordinates
                                        driverLocation = LatLng(current_latitude, current_longitute)
                                        pickupLocation =
                                            LatLng(pick_latitude, pick_longitute) // Los Angeles
                                        dropLocation =
                                            LatLng(drop_latitude, drop_longitute) // Las Vegas

                                        trip_status_from_api =
                                            data.data?.get(0)?.triplocations?.get(0)?.tripStatus
                                    }

                                }
                                startBackSer(pick_latitude, pick_longitute)

                                //getTimeMet()

                                /* fetchAndDrawRoute(
                                     driverLocation!!,
                                     pickupLocation!!,
                                     dropLocation!!
                                 )*/

                                // Start location updates and tracking
                                ///  handler.post(locationUpdateRunnable)  // Start the recurring location updates and route checking
                                /* binding.bottomSheetRide.setHideable(true)
                                 binding.bottomSheetStartTrip.setHideable(false)
                                 binding.bottomSheetForRideCompleted.setHideable(true)

                                 binding.bottomSheetRide.state = BottomSheetBehavior.STATE_HIDDEN
                                 binding.bottomSheetStartTrip.state = BottomSheetBehavior.STATE_EXPANDED
                                 binding.bottomSheetForRideCompleted.state = BottomSheetBehavior.STATE_HIDDEN*/
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
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        } else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }

                    }
                }
            }
        })
        viewModels.endTripResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is StartTripResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                handler.removeCallbacks(locationUpdateRunnable) // Stop updates

                                binding.tvDistanceCovered.text =
                                    distance_covered + "" + distance_covered_unit
                                val currentTime =
                                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                                        Date()
                                    )
                                binding.tvArrivingTime.text = currentTime
                                binding.tvNoOfStops.text = "1 Stop"
                                // trip_status_from_api=data.data?.get(0)?.triplocations?.get(0)?.tripStatus
                                trip_mode_from_api = data.data?.get(0)?.tripMode
                                if (trip_mode_from_api.equals("0")) {
                                    trip_status_from_api =
                                        data.data?.get(0)?.triplocations?.get(0)?.tripStatus
                                }

                                if (trip_mode_from_api.equals("1")) {
                                    if (data.data?.get(0)?.triplocations?.get(0)?.tripStatus.equals(
                                            "completed"
                                        )
                                    ) {
                                        trip_status_from_api =
                                            data.data?.get(0)?.triplocations?.get(1)?.tripStatus
                                    } else {
                                        trip_status_from_api =
                                            data.data?.get(0)?.triplocations?.get(0)?.tripStatus
                                    }

                                }
                                stopLocationService()
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
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        } else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }

                    }
                }
            }
        })
        viewModels.goTripResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is StartTripResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                trip_mode_from_api = data.data?.get(0)?.tripMode
                                if (trip_mode_from_api.equals("0")) {
                                    trip_status_from_api =
                                        data.data?.get(0)?.triplocations?.get(0)?.tripStatus
                                }

                                if (trip_mode_from_api.equals("1")) {
                                    if (data.data?.get(0)?.triplocations?.get(0)?.tripStatus.equals(
                                            "completed"
                                        )
                                    ) {
                                        trip_status_from_api =
                                            data.data?.get(0)?.triplocations?.get(1)?.tripStatus
                                    } else {
                                        trip_status_from_api =
                                            data.data?.get(0)?.triplocations?.get(0)?.tripStatus
                                    }

                                }
                                //trip_status_from_api=data.data?.get(0)?.triplocations?.get(0)?.tripStatus

                                startNavForDrop(drop_latitude, drop_longitute)
                                /* if (click == 0) {
                                     binding.btGo.setBackgroundResource(R.drawable.solid_green_10)
                                     click = 1
                                 } else {*/

                                // }
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
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        } else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }

                    }
                }
            }
        })
        viewModels.acceptTripResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is AcceptRidelResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                launchActivity(
                                    HomeActivity::class.java, removeAllPrevActivities = false
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
                    } else if (it.errorBody?.message.toString().equals(AppConstants.UNAUTH)) {
                        showAuthenticateDialog()
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        } else if (it.errorBody?.error != null) {
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
        /*if (!hasLocationPermission()) {
            requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
            return
        }*/
        // Enable the location layer if permission is granted
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
       // mMap.setLatLngBoundsForCameraTarget(kuwaitBounds)
       // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(kuwaitBounds, 100))
        // Get the current location of the device and set the position of the map

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                //mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 19f))
                currentMarker = mMap.addMarker(
                    MarkerOptions().position(currentLatLng).title("Current Location")
                )
                getDriverLocationInfo(currentLatLng)
                //updateLocationInfo(currentLatLng)
            }
        }

        // Start location updates
        // handler.post(locationUpdateRunnable) // Start the recurring task


    }

    private fun updateLocationInfo(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        current_latitude = latLng.latitude
        current_longitute = latLng.longitude
        driverLocation = LatLng(current_latitude, current_longitute)

        Log.d("LOcations", driverLocation.toString())

        if (addresses!!.isNotEmpty()) {
            val address = addresses[0]
            val addressText = address.getAddressLine(0)
            val name = address.featureName
            if (for_first_time) {
                binding.tvDriverLocation.text = name
                for_first_time = false
            }
            // binding.tvLocationAddress.text = addressText

        }
    }

    private fun getDriverLocationInfo(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        current_latitude = latLng.latitude
        current_longitute = latLng.longitude
        driverLocation = LatLng(current_latitude, current_longitute)

        Log.d("LOcations", driverLocation.toString())

        if (addresses!!.isNotEmpty()) {
            val address = addresses[0]
            driverAddress = address.getAddressLine(0)
            driverLocationTitle = address.featureName

            // binding.tvLocationAddress.text = addressText

        }
    }

    /* override fun onRequestPermissionsResult(
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
                     mapFragment.getMapAsync(this@MyRideActivity)
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

    // Handle permission results
    /* override fun onRequestPermissionsResult(
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
             }

             if (isPermissionGranted) {
                 // Permissions granted, proceed with map and service
                 val mapFragment = supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
                 mapFragment.getMapAsync(this@MyRideActivity)

             } else {
                 Toast.makeText(this, "Location permissions denied", Toast.LENGTH_SHORT).show()
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
                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
                mapFragment.getMapAsync(this@MyRideActivity)
            } else {
                Toast.makeText(
                    this,
                    "Location, service or notification permissions denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun fetchAndDrawRoute(
        pickupLocation: LatLng,
        dropLocation: LatLng
    ) {
        mMap.addMarker(MarkerOptions().position(pickupLocation).title("Pickup Location"))
        mMap.addMarker(MarkerOptions().position(dropLocation).title("Drop Location"))
        CoroutineScope(Dispatchers.IO).launch {
            val url = getDirectionsUrl(pickupLocation, dropLocation)
            Log.d("URL OF ", url)
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                Log.d("URL OF ", url)

                val responseData = response.body?.string()
                responseData?.let {
                    routePoints = parseDirections(it)
                    withContext(Dispatchers.Main) {
                        mMap.clear()
                        mMap.addMarker(
                            MarkerOptions().position(pickupLocation).title("Pickup Location")
                        )
                        mMap.addMarker(
                            MarkerOptions().position(dropLocation).title("Drop Location")
                        )
                        Log.d("RoutePoints", "Size: ${routePoints.size}")
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

            // Check for status
            val status = jsonResponse.optString("status")
            if (status != "OK") {
                Log.e("ParseError", "Status: $status")
                runOnUiThread {
                    Toast.makeText(this, "API error: $status", Toast.LENGTH_SHORT).show()
                }
                return result
            }

            // Check routes array
            val routes = jsonResponse.optJSONArray("routes")
            if (routes == null || routes.length() == 0) {
                Log.e("ParseError", "No routes found")
                runOnUiThread {
                    Toast.makeText(this, "No routes found", Toast.LENGTH_SHORT).show()
                }
                return result
            }
            Log.d("ParseDebug", "Routes found: ${routes.length()}")

            // Access legs array in the first route
            val legs = routes.getJSONObject(0).optJSONArray("legs")
            if (legs == null || legs.length() == 0) {
                Log.e("ParseError", "No legs found in route")
                return result
            }
            Log.d("ParseDebug", "Legs found: ${legs.length()}")

            // Access steps array in the first leg
            val steps = legs.getJSONObject(0).optJSONArray("steps")
            /*val durationObject = legs.getJSONObject(0).getJSONObject("duration")
            val durationText = durationObject.getString("text") // e.g., "15 mins
            binding.tvTimeTaken.text=durationText*/
            if (steps == null || steps.length() == 0) {
                Log.e("ParseError", "No steps found in leg")
                return result
            }
            Log.d("ParseDebug", "Steps found: ${steps.length()}")

            // Loop through steps to decode polyline points
            for (i in 0 until steps.length()) {
                val step = steps.getJSONObject(i)
                val polyline = step.optJSONObject("polyline")
                val points = polyline?.optString("points")
                if (points != null) {
                    result.addAll(PolyUtil.decode(points))
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e("ParseError", "JSON parsing failed: ${e.message}")
            runOnUiThread {
                Toast.makeText(this, "Failed to parse directions", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ParseError", "Unexpected error: ${e.message}")
            runOnUiThread {
                Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show()
            }
        }

        Log.d("ParsedPoints", "Total points: ${result.size}")
        return result
    }


    /*private fun parseDirections(response: String): List<LatLng> {
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
            val durationObject = legs.getJSONObject(0).getJSONObject("duration")
            val durationText = durationObject.getString("text") // e.g., "15 mins
            binding.tvTimeTaken.text = durationText

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
        *//*val result = mutableListOf<LatLng>()
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
        return result*//*
    }
*/
    /* private fun drawRoute(routePoints: List<LatLng>) {
         val polylineOptions = PolylineOptions()
             .addAll(routePoints)
             .width(10f)
             .color(android.graphics.Color.BLUE)
             .geodesic(true)

         mMap.addPolyline(polylineOptions)
     }*/

    private fun drawRoute(routePoints: List<LatLng>) {
        routePolyline?.remove()
        // mMap.clear()// Remove the existing path

        val polylineOptions = PolylineOptions()
            .addAll(routePoints)
            .width(10f)
            .color(android.graphics.Color.BLUE)
            .geodesic(true)
        // showToast("Inside Draw polyline, routePoints size: ${routePoints.size}")
        routePolyline =
            mMap.addPolyline(polylineOptions)  // Store the reference to the new polyline

        // Focus map on the polyline route
        if (routePoints.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.Builder()
            for (point in routePoints) {
                boundsBuilder.include(point)
            }
            val bounds = boundsBuilder.build()

            // Add some padding around the bounds to ensure the route is fully visible
            val padding = 100  // in pixels
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 15000 // 10 seconds

    @SuppressLint("MissingPermission")
    private val locationUpdateRunnable = object : Runnable {
        override fun run() {


            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    updateLocationInfo(currentLatLng)
                    // Check if the driver has jumped the route
                    pickupLocation?.let { it1 ->
                        dropLocation?.let { it2 ->
                            updateDriverLocationAndCheckRoute(
                                currentLatLng,
                                it1, it2, routePoints
                            )
                        }
                    }
                    updateDriverLocation(currentLatLng)
                    updateDriverLocationAPi(it.latitude, it.longitude) // Call your API function
                }
            }
            handler.postDelayed(this, updateInterval) // Repeat this process
        }
    }

    private fun updateDriverLocationAndCheckRoute(
        driverLocation: LatLng,
        pickupLocation: LatLng,
        dropLocation: LatLng,
        routePoints: List<LatLng>
    ) {
        if (isDriverOffRoute(driverLocation, routePoints, 100.0)) {  // 100 meters as the threshold
            // Driver is off the route, so remove the existing route and fetch a new one
            routePolyline?.remove()
            // fetchAndDrawRoute(driverLocation, pickupLocation, dropLocation)
        }
    }

    private fun isDriverOffRoute(
        driverLocation: LatLng,
        routePoints: List<LatLng>,
        threshold: Double
    ): Boolean {
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

    override fun onDestroy() {
        super.onDestroy()


        handler.removeCallbacks(locationUpdateRunnable) // Stop updates
    }

    override fun onResume() {
        super.onResume()
        bottomSheet_Ride.setHideable(true)
        bottomSheetStart_Trip.setHideable(true)
        bottomSheetForRide_Completed.setHideable(true)
        //bottomSheet_Ride.peekHeight = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet_Ride.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetStart_Trip.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetForRide_Completed.state = BottomSheetBehavior.STATE_HIDDEN
        managescreenbottoms()
    }

    private fun isDriverAtPickup(currentLatLng: LatLng, pickupLatLng: LatLng): Boolean {
        val distance = FloatArray(1)
        Location.distanceBetween(
            currentLatLng.latitude, currentLatLng.longitude,
            pickupLatLng.latitude, pickupLatLng.longitude,
            distance
        )
        return distance[0] < 50 // Example threshold: 50 meters
    }

    private fun getDirectionsUrlToGetTime(origin: LatLng, dest: LatLng): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${dest.latitude},${dest.longitude}"
        val key = "key=${getString(R.string.google_search_key)}"
        return "https://maps.googleapis.com/maps/api/directions/json?$strOrigin&$strDest&key=$key"
    }

    /*fun getTimeMet()
    {
        val directionsUrl = getDirectionsUrlToGetTime(driverLocation!!, pickupLocation!!)
        val request = Request.Builder().url(directionsUrl).build()

        //val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseData = response.body?.string()
            responseData?.let {
                val durationInSeconds  = parseDurationFromResponse(it)
                if (durationInSeconds != -1) {
                    // Handle the duration, for example, update the UI or save it to a variable
                    runOnUiThread {
                        val time_taken_one_rout="${durationInSeconds / 60} minutes"
                        //binding.tvEstimatedTimeToPickup.text = "Estimated time: ${durationInSeconds / 60} minutes"
                    }
                }
            }
        }



    }*/
    private fun parseDurationFromResponse(response: String): Int {
        try {
            val jsonResponse = JSONObject(response)
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val durationObject = legs.getJSONObject(0).getJSONObject("duration")
            return durationObject.getInt("value") // Duration in seconds
        } catch (e: Exception) {
            e.printStackTrace()
            return -1 // Return -1 in case of failure
        }
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

    fun startNavForDrop(drop_latitude: Double, drop_longitute: Double) {
        // Launch Google Maps navigation to the pickup location
        val gmmIntentUri = Uri.parse("google.navigation:q=$drop_latitude,$drop_longitute")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    fun startBackSer(pick_latitude: Double, pick_longitute: Double) {
        /* val serviceIntent = Intent(this, LocationService::class.java)
         ContextCompat.startForegroundService(this, serviceIntent)
 */

        val serviceIntent = Intent(this, LocationService::class.java).apply {
            putExtra("booking_id", booking_id)
            putExtra("trip_loc_id", trip_loc_id)
            putExtra("trip_status", trip_status)
            putExtra("driver_location_title", driverLocationTitle)
            putExtra("driver_address", driverAddress)
            putExtra("pickup_lat", pick_latitude)
            putExtra("pickup_lng", pick_longitute)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
        // Launch Google Maps navigation to the pickup location
        val gmmIntentUri = Uri.parse("google.navigation:q=$pick_latitude,$pick_longitute")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    // Call this method to stop the service when the trip ends
    private fun stopLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        stopService(serviceIntent)
    }

    private fun managescreenbottoms() {
        if (trip_status_from_api.equals("ongoing") || trip_status_from_api.equals("ontheway")) {

            if (trip_status_from_api.equals("ongoing")) {
                binding.btEnd.visibility = View.VISIBLE
                binding.btGo.visibility = View.GONE
            }
            if (trip_status_from_api.equals("ontheway")) {
                binding.btEnd.visibility = View.GONE
                binding.btGo.visibility = View.VISIBLE
            }
            bottomSheet_Ride.setHideable(true)
            bottomSheetStart_Trip.setHideable(false)
            bottomSheetForRide_Completed.setHideable(true)
            // bottomSheet_Ride.peekHeight = ViewGroup.LayoutParams.MATCH_PARENT
            bottomSheet_Ride.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetStart_Trip.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetForRide_Completed.state = BottomSheetBehavior.STATE_HIDDEN
        } else if (trip_status_from_api.equals("completed")) {
            bottomSheet_Ride.setHideable(true)
            bottomSheetStart_Trip.setHideable(true)
            bottomSheetForRide_Completed.setHideable(false)
            // bottomSheet_Ride.peekHeight = ViewGroup.LayoutParams.MATCH_PARENT
            bottomSheet_Ride.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetStart_Trip.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetForRide_Completed.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheet_Ride.setHideable(false)
            bottomSheetStart_Trip.setHideable(true)
            bottomSheetForRide_Completed.setHideable(true)
            bottomSheet_Ride.peekHeight = ViewGroup.LayoutParams.MATCH_PARENT
            bottomSheet_Ride.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetStart_Trip.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetForRide_Completed.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }


}