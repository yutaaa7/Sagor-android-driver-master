package com.driver.sagardriverapp.uis.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.databinding.FragmentAcceptBinding
import com.driver.sagardriverapp.model.AcceptBookingsList
import com.driver.sagardriverapp.model.AcceptBookingsListResponse
import com.driver.sagardriverapp.model.SchoolDriverTripList
import com.driver.sagardriverapp.model.SchoolDriverTripListResponse
import com.driver.sagardriverapp.uis.home.viewModel.AcceptedTripViewModel
import com.driver.sagardriverapp.uis.login.DriverLoginActivity
import com.driver.sagardriverapp.uis.myRide.AcceptedTripsAdapter
import com.driver.sagardriverapp.utils.AppConstants
import com.driver.sagardriverapp.utils.CommonDialog
import com.driver.sagardriverapp.utils.SharedPref
import com.driver.sagardriverapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AcceptFragment : Fragment() {

    lateinit var adapter: AcceptedTripsAdapter
    lateinit var childTripListAdapter: ChildAllTripsAdapter
    private lateinit var binding: FragmentAcceptBinding
    var acceptedTripList = mutableListOf<AcceptBookingsList>()
    var childTripList = mutableListOf<SchoolDriverTripList>()
    var start_date: String? = null
    private lateinit var dialog: Dialog
    private val viewModels: AcceptedTripViewModel by viewModels<AcceptedTripViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAcceptBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUis()
    }

    private fun initUis() = binding.apply {
        dialog = Dialog(requireActivity())
        headerAccept.ivBack.visibility=View.GONE
        Log.d("Driver type ", SharedPref.readString(SharedPref.DRIVER_TYPE))
        val dates = getNextSevenDays()
        start_date = dates[0]
        if (SharedPref.readString(SharedPref.DRIVER_TYPE).equals("child")) {
            llChildDriverLayout.visibility = View.VISIBLE
            llNormalDriverLayout.visibility = View.GONE
            headerAccept.endTitle.visibility = View.GONE
            headerAccept.headerTitle.text = getString(R.string.driver_schedule)
            headerAccept.ivBack.visibility = View.GONE
            schoolDriverTripListApi()
        } else {
            tripListApi()
            llChildDriverLayout.visibility = View.GONE
            llNormalDriverLayout.visibility = View.VISIBLE
            headerAccept.endTitle.visibility = View.VISIBLE
            headerAccept.headerTitle.text = getString(R.string.my_trips)
        }

        rvAcceptedTrips.layoutManager = LinearLayoutManager(activity)
        adapter = AcceptedTripsAdapter(requireActivity(), acceptedTripList)
        rvAcceptedTrips.adapter = adapter


        rvDatesForChild.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvDatesForChild.adapter =
            DatesAdapter(requireActivity(), dates, object : DatesAdapter.OnItemClickListener {
                override fun onItemClick(selected_date: String) {
                    start_date = selected_date
                    schoolDriverTripListApi()
                }

            })



        rvChildTrips.layoutManager = LinearLayoutManager(activity)
        childTripListAdapter = ChildAllTripsAdapter(requireActivity(),childTripList)
        rvChildTrips.adapter = childTripListAdapter


        headerAccept.endTitle.setOnClickListener {
            requireActivity().launchActivity(
                HistoryActivity::class.java,
                removeAllPrevActivities = false
            )
        }


        apiObserver()
    }

    fun tripListApi() {
        showDialog()
        viewModels.acceptedTripsList(

            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.VEHICLE_CAT_ID),
            "7",
            SharedPref.readString(SharedPref.USER_ID)
        )

    }

    fun schoolDriverTripListApi() {
        showDialog()
        viewModels.schoolTripListApi(

            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.USER_ID), SharedPref.readString(SharedPref.SCHOOL_ID),
            start_date.toString(), SharedPref.readString(SharedPref.BUS_ID)
        )

    }

    fun apiObserver() {
        viewModels.tripListResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is AcceptBookingsListResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                acceptedTripList.clear()
                                if (data.data?.size!! > 0) {
                                    binding.tvNoTrip.visibility = View.GONE

                                    acceptedTripList.addAll(data.data)
                                } else {
                                    binding.tvNoTrip.visibility = View.VISIBLE

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
        viewModels.schoolDriverTripListResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is SchoolDriverTripListResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                childTripList.clear()
                                if (data.data?.size!! > 0)
                                {
                                    binding.tvNoChildTrip.visibility=View.GONE

                                    childTripList.addAll(data.data)
                                }
                                else{
                                    binding.tvNoChildTrip.visibility=View.VISIBLE

                                }
                                childTripListAdapter.notifyDataSetChanged()
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
    fun getFirstErrorMessage(errors: String): String {
        return if (errors.contains(",")) {
            // If there is a comma, get the first part before the comma
            errors.substringBefore(",")
        } else {
            // If no comma, return the whole error string
            errors
        }
    }

    fun getNextSevenDays(): List<String> {
        val datesList = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in 0..30) {
            datesList.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return datesList
    }
    private fun showToast(message: String?) {
        val activity: FragmentActivity? = activity;
        if(activity != null && isAdded()) {
            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDialog() {
        try {
            dialog.setContentView(R.layout.dialog_progress)
            dialog.window?.setBackgroundDrawable(
                ColorDrawable(
                    Color.TRANSPARENT
                )
            )
            dialog.setCancelable(false)
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * this function is use for hiding progress dialog
     */
    private fun hideDialog() {
        try {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun showAuthenticateDialog() {
        val dialog = CommonDialog.createAuthDialog(
            context = requireActivity(),
            positiveButtonCallback = {
                requireActivity().launchActivity(
                    DriverLoginActivity::class.java,
                    removeAllPrevActivities = true
                )
                SharedPref.writeString(
                    SharedPref.ACCESS_TOKEN,
                    ""
                )

                SharedPref.writeString(
                    SharedPref.USER_ID,
                    ""
                )
                SharedPref.writeBoolean(SharedPref.IS_LOGIN, false)

            },

            )


        // Show the dialog
        dialog.show()
    }


}