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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.databinding.FragmentTripsBinding
import com.driver.sagardriverapp.model.AcceptRidelResponse
import com.driver.sagardriverapp.model.TripList
import com.driver.sagardriverapp.model.TripListResponse
import com.driver.sagardriverapp.uis.HomeActivity
import com.driver.sagardriverapp.uis.home.viewModel.TripListViewModel
import com.driver.sagardriverapp.uis.login.DriverLoginActivity
import com.driver.sagardriverapp.utils.AppConstants
import com.driver.sagardriverapp.utils.CommonDialog
import com.driver.sagardriverapp.utils.SharedPref
import com.driver.sagardriverapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class TripsFragment : Fragment() {
    private lateinit var dialog: Dialog
    private lateinit var binding: FragmentTripsBinding
    var start_date: String? = null
    lateinit var tripsAdapter: TripsAdapter
    var tripList = mutableListOf<TripList>()
    private val viewModels: TripListViewModel by viewModels<TripListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTripsBinding.inflate(layoutInflater)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUis()
    }

    private fun initUis() = binding.apply {
        dialog = Dialog(requireActivity())
        binding.tripsToolbar.ivBack.visibility=View.GONE

        binding.rvDates.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        val dates = getNextSevenDays()
        start_date = dates[0]
        binding.rvDates.adapter =
            DatesAdapter(requireActivity(), dates, object : DatesAdapter.OnItemClickListener {
                override fun onItemClick(selected_date: String) {
                    start_date = selected_date
                    tripListApi()
                }

            })

        binding.rvTrips.layoutManager =
            LinearLayoutManager(requireActivity())
        tripsAdapter =
            TripsAdapter(requireActivity(), tripList, object : TripsAdapter.OnItemClickListener {
                override fun onItemClick(id: Int?) {
                    acceptTripApi(id)
                }

            })
        binding.rvTrips.adapter = tripsAdapter


        tripListApi()
        apisObservers()
        Log.d("TOKEN", SharedPref.readString(SharedPref.ACCESS_TOKEN))
        Log.d("TOKEN", SharedPref.readString(SharedPref.USER_ID))
    }

    fun tripListApi() {
        showDialog()
        viewModels.tripList(

            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.VEHICLE_CAT_ID), "6", start_date.toString()
        )

    }

    fun acceptTripApi(id: Int?) {
        showDialog()
        viewModels.acceptTrip(

            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            id.toString(), SharedPref.readString(SharedPref.USER_ID)
        )

    }

    fun apisObservers() {
        viewModels.tripListResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is TripListResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                tripList.clear()
                                if (data.data?.size!! > 0) {
                                    binding.tvNoTrip.visibility = View.GONE
                                    tripList.addAll(data.data)

                                } else {
                                    binding.tvNoTrip.visibility = View.VISIBLE

                                }
                                tripsAdapter.notifyDataSetChanged()
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
                    Log.d("CIDE ",it.errorBody?.status.toString())
                    Log.d("CIDE ",it.errorBody.toString())
                    Log.d("CIDE ",it.errorBody?.message.toString())
                    if (it.errorBody?.status == 401) {
                        showAuthenticateDialog()
                    } else if (it.errorBody?.message.toString().equals(AppConstants.UNAUTH,ignoreCase = true)) {
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
        viewModels.acceptTripResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is AcceptRidelResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                requireActivity().launchActivity(
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
    private fun showAuthenticateDialog() {
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
    private fun getFirstErrorMessage(errors: String): String {
        return if (errors.contains(",")) {
            // If there is a comma, get the first part before the comma
            errors.substringBefore(",")
        } else {
            // If no comma, return the whole error string
            errors
        }
    }

}