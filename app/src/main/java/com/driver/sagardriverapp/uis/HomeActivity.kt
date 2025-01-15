package com.driver.sagardriverapp.uis

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.base.BaseActivity
import com.driver.sagardriverapp.databinding.ActivityHomeBinding
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.uis.home.AcceptFragment
import com.driver.sagardriverapp.uis.home.ProfileFragment
import com.driver.sagardriverapp.uis.home.TripsFragment
import com.driver.sagardriverapp.uis.home.viewModel.HomeViewModel
import com.driver.sagardriverapp.utils.AppConstants
import com.driver.sagardriverapp.utils.SharedPref
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    lateinit var binding: ActivityHomeBinding
    var bundle: Bundle? = null
    var type: String = ""
    var where: String = ""
    var token: String? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private val viewModels: HomeViewModel by viewModels<HomeViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUis()
    }

    private fun initUis() = binding.apply {

       /* if (!hasLocationPermission()) {
            requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
        }*/
        if (!hasLocationWithServicePermission()) {
            requestLocationPermissionWithSer(LOCATION_PERMISSION_REQUEST_CODE)

        } else {
            /* // Permissions already granted, start the map and the service
             val mapFragment = supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
             mapFragment.getMapAsync(this@MyRideActivity)
             startLocationService()*/
        }


        bundle = intent.extras

        if (bundle != null) {

            type = bundle?.getString("type").toString()
            where = bundle?.getString("where").toString()

        }
        //if(type.equals("child"))
        if (SharedPref.readString(SharedPref.DRIVER_TYPE).equals("child")) {
            showPage("accept")
            llAccepts.visibility = View.VISIBLE
            llTrips.visibility = View.GONE
            llProfile.visibility = View.VISIBLE
        }
        //if(type.equals("normal"))
        if (SharedPref.readString(SharedPref.DRIVER_TYPE).equals("normal")) {
            // showPage("trips")
            if (where.equals("login")) {
                showPage("trips")
            } else {
                showPage("accept")
            }

            llAccepts.visibility = View.VISIBLE
            llTrips.visibility = View.VISIBLE
            llProfile.visibility = View.VISIBLE
        }
        /*  if(page.equals("3"))
          {
              showPage("settings")
          }
          else{*/

        //}

        rlHome.setOnClickListener {
            showPage("trips")
        }
        rlTrips.setOnClickListener {
            showPage("accept")
        }
        rlSettings.setOnClickListener {
            showPage("settings")
        }

        fcmTokenUpdate()
        apisObservers()
    }

    fun apisObservers() {
        viewModels.commonResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CommonResponse -> {
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
                    Log.d("TS", it.toString())
                    Log.d("TS", it.errorBody?.status.toString())
                    Log.d("TS", it.errorBody?.message.toString())
                    Log.d("TS", it.errorCode.toString())
                    if (it.errorBody?.status == 401) {
                        showAuthenticateDialog()
                    } else if (it.errorBody?.message.toString().equals(AppConstants.UNAUTH)) {
                        showAuthenticateDialog()
                    } else if (it.errorCode == 422) {
                        showToast(getString(R.string.invalid_credentials))
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        } else {
                            showToast(it.errorBody?.message.toString())
                        }

                    }
                }
            }
        })

    }

    fun fcmTokenUpdate() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token

            token = task.result
            Log.d("FCM", "Token: $token")
            var device_id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            viewModels.updateFcmToken(
                "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
                SharedPref.readString(SharedPref.EMAIL),
                "android",
                device_id,
                "4",
                "",
                token.toString()
            )
        }

    }

    fun showPage(s: String) = binding.apply {
        if (s.equals("trips")) {
            rlHomeFocus.visibility = View.VISIBLE
            rlHome.visibility = View.GONE
            rlSettingsFocus.visibility = View.GONE
            rlSettings.visibility = View.VISIBLE
            rlTripsFocus.visibility = View.GONE
            rlTrips.visibility = View.VISIBLE
            showFragment(TripsFragment())
        }
        if (s.equals("accept")) {
            rlHomeFocus.visibility = View.GONE
            rlHome.visibility = View.VISIBLE
            rlSettingsFocus.visibility = View.GONE
            rlSettings.visibility = View.VISIBLE
            rlTripsFocus.visibility = View.VISIBLE
            rlTrips.visibility = View.GONE
            showFragment(AcceptFragment())
        }
        if (s.equals("settings")) {
            rlHomeFocus.visibility = View.GONE
            rlHome.visibility = View.VISIBLE
            rlSettingsFocus.visibility = View.VISIBLE
            rlSettings.visibility = View.GONE
            rlTripsFocus.visibility = View.GONE
            rlTrips.visibility = View.VISIBLE
            showFragment(ProfileFragment())
        }

    }

    fun showFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.name
        val fragmentManager = supportFragmentManager
        val fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            // fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit()
        }
    }

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
               /* // Permissions granted, proceed with map and service
                val mapFragment = supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
                mapFragment.getMapAsync(this@MyRideActivity)*/
            } else {
                Toast.makeText(this, "Location, service or notification permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}