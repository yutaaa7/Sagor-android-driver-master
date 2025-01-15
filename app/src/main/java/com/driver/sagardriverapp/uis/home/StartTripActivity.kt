package com.driver.sagardriverapp.uis.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.base.BaseActivity
import com.driver.sagardriverapp.databinding.ActivityStartTripBinding
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.SchoolDriverTripDetailResponse
import com.driver.sagardriverapp.uis.home.viewModel.StartTripViewModel
import com.driver.sagardriverapp.uis.myRide.MyRideChildActivity
import com.driver.sagardriverapp.uis.myRide.ReportIssueActivity
import com.driver.sagardriverapp.utils.AppConstants
import com.driver.sagardriverapp.utils.SharedPref
import com.driver.sagardriverapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartTripActivity : BaseActivity() {

    var bundle: Bundle? = null
    var ride_id: String? = null
    var school_dialor: String? = null
    var emergency_dialor: String? = null

    private lateinit var binding: ActivityStartTripBinding
    private val viewModels: StartTripViewModel by viewModels<StartTripViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStartTripBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUis()
    }

    private fun initUis() = binding.apply {

        bundle = intent.extras

        if (bundle != null) {


            ride_id = bundle?.getString("ride_id").toString()

        }

        headerStartTrip.headerTitle.text = getString(R.string.start_trip)
        headerStartTrip.ivBack.setOnClickListener {
            finish()
        }

        btStartTrip.setOnClickListener {
            startChildTripApi()
        }
        btChildListFromStartTrip.setOnClickListener {
            val bundle = Bundle()
            bundle?.putString("ride_id", ride_id.toString())
            launchActivity(
                ChildListActivity::class.java, removeAllPrevActivities = false,
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
        btReport.setOnClickListener {
            val bundle = Bundle()
            bundle?.putString("ride_id", ride_id.toString())
            launchActivity(
                ReportIssueActivity::class.java, removeAllPrevActivities = false,
                bundle = bundle
            )
        }
        tvEmergency.setOnClickListener {
            if (emergency_dialor?.isNotEmpty() == true) {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$emergency_dialor")
                startActivity(dialIntent)
            }
        }
        btViewTrip.setOnClickListener {
            val bundle = Bundle()
            bundle?.putString("ride_id", ride_id.toString())
            launchActivity(
                MyRideChildActivity::class.java,
                removeAllPrevActivities = false,
                bundle = bundle
            )
        }
        schoolDriverTripDetailApi()
        apisObservers()
    }

    fun schoolDriverTripDetailApi() {
        showDialog()
        viewModels.tripDetailsForSChoolDriverTrips(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            ride_id.toString()
        )
    }

    fun startChildTripApi() {
        showDialog()
        viewModels.startChildTrips(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            ride_id.toString()
        )

    }

    fun apisObservers() {
        viewModels.schoolDriverTripDetailResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is SchoolDriverTripDetailResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                binding.tvSchoolName.text = data.data?.get(0)?.school?.name
                                binding.tvCurrentLocation.text =
                                    data.data?.get(0)?.startLocationTitle
                                binding.tvCurrAdd.text = data.data?.get(0)?.startLocationAddress
                                binding.tvDestination.text = data.data?.get(0)?.endLocationTitle
                                binding.tvDesAddress.text = data.data?.get(0)?.endLocationAddress
                                binding.tvExpectedTime.text = data.data?.get(0)?.expectedStartTime
                                binding.tvSchoolId.text =
                                    getString(R.string.school_id) + "" + data.data?.get(0)?.schoolId

                                school_dialor = data.data?.get(0)?.school?.user?.mobile
                                emergency_dialor = data.data?.get(0)?.school?.schoolEmergency?.contact_no
                                if (data.data?.get(0)?.rideStatus.equals("2")) {
                                    //binding.tvTime.setTextColor(activity.getColor(R.color.green))
                                    binding.btStartTrip.visibility= View.GONE
                                    binding.btViewTrip.visibility= View.VISIBLE
                                }
                               else{
                                    binding.btStartTrip.visibility= View.VISIBLE
                                    binding.btViewTrip.visibility= View.GONE
                                }
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
        viewModels.startChildTripResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CommonResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                val bundle = Bundle()
                                bundle?.putString("ride_id", ride_id.toString())
                                launchActivity(
                                    MyRideChildActivity::class.java,
                                    removeAllPrevActivities = false,
                                    bundle = bundle
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
    }
}