package com.driver.sagardriverapp.uis.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.base.BaseActivity
import com.driver.sagardriverapp.databinding.ActivityChildListBinding
import com.driver.sagardriverapp.model.ChildListResponse
import com.driver.sagardriverapp.model.ChildrensdetailItem
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.uis.home.viewModel.ChildListViewModel
import com.driver.sagardriverapp.utils.AppConstants
import com.driver.sagardriverapp.utils.SharedPref
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ChildListActivity : BaseActivity() {

    var bundle: Bundle? = null
    var ride_id: String? = null
    lateinit var adapter: ChildListAdapter
    lateinit var binding: ActivityChildListBinding
    var childList = mutableListOf<ChildrensdetailItem>()
    var current_latitude: Double = 0.0
    var current_longitute: Double = 0.0
    var addressText: String? = null
    var name: String? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModels: ChildListViewModel by viewModels<ChildListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChildListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    private fun initUis() = binding.apply {

        // Enable the location layer if permission is granted
        if (!hasLocationPermission()) {
            requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)

        }

        // Initialize the fused location client
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@ChildListActivity)

        bundle = intent.extras

        if (bundle != null) {


            ride_id = bundle?.getString("ride_id").toString()

        }
        headerChildList.headerTitle.text = getString(R.string.child_list)
        headerChildList.ivBack.setOnClickListener {
            finish()
        }

        rvChilds.layoutManager = LinearLayoutManager(this@ChildListActivity)
        adapter = ChildListAdapter(
            this@ChildListActivity,
            childList,
            object : ChildListAdapter.OnItemClickListener {
                override fun onItemClick(id: Int?) {
                    childBoardApi(id)
                }

                override fun onBoardedClick(id: Int?) {
                    childDroppedApi(id)
                }

            })
        rvChilds.adapter = adapter

        etSearchChild.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length > 2) {
                    childListApi(etSearchChild.text.toString(), false)
                }
                if (p0.toString().length == 0) {
                    childListApi("", false)
                }


            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        childListApi("", true)
        apisObservers()
    }

    fun childListApi(Search: String, b: Boolean) {
        if (b) {
            showDialog()
        }

        viewModels.childList(

            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            ride_id.toString(), Search
        )

    }

    fun childBoardApi(id: Int?) {
        showDialog()
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Format to hh:mm a
        val formattedTime = sdf.format(currentTime)
        val timeParts = formattedTime.split(" ") // Split the string by space
        val time = timeParts[0] // hh:mm part (e.g., 10:00)
        val amPm = timeParts[1] // AM or PM part


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
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                //mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))

                updateLocationInfo(currentLatLng)
            }
        }
        viewModels.childBoard(

            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            id.toString(), time, amPm, current_latitude.toString(),
            current_longitute.toString(), name.toString(), addressText.toString()
        )

    }

    fun childDroppedApi(id: Int?) {
        showDialog()
        viewModels.childDropped(

            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            id.toString()
        )

    }

    fun apisObservers() {
        viewModels.childListResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is ChildListResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
                                    binding.tvSchoolName.text =
                                        data.data?.schooldetail?.get(0)?.school?.name
                                }
                                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
                                    binding.tvSchoolName.text =
                                        data.data?.schooldetail?.get(0)?.school?.nameAr
                                }

                                binding.tvSchoolId.text =
                                    getString(R.string.school_id) + " " + data.data?.schooldetail?.get(
                                        0
                                    )?.schoolId
                                binding.tvBus.text =
                                    getString(R.string.bus) + data.data?.schooldetail?.get(0)?.vehicle?.vehicleNumber
                                childList.clear()
                                if (data.data?.childrensdetail?.size!! > 0) {
                                    binding.tvChild.visibility = View.GONE

                                    childList.addAll(data.data?.childrensdetail)
                                } else {
                                    binding.tvChild.visibility = View.VISIBLE

                                }
                                adapter.notifyDataSetChanged()
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
        viewModels.childBoardResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CommonResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                childListApi("", false)
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

    private fun updateLocationInfo(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        current_latitude = latLng.latitude
        current_longitute = latLng.longitude
        if (addresses!!.isNotEmpty()) {
            val address = addresses[0]

            addressText = address.getAddressLine(0)

            name = address.featureName

            // binding.tvLocationAddress.text = addressText

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

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
    }
}