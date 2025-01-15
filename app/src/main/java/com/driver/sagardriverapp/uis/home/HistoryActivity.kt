package com.driver.sagardriverapp.uis.home

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.base.BaseActivity
import com.driver.sagardriverapp.databinding.ActivityHistoryBinding
import com.driver.sagardriverapp.model.CompletedBooking
import com.driver.sagardriverapp.model.CompletedBookingResponse
import com.driver.sagardriverapp.uis.home.viewModel.HistoryViewModel
import com.driver.sagardriverapp.utils.AppConstants
import com.driver.sagardriverapp.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : BaseActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val viewModels: HistoryViewModel by viewModels<HistoryViewModel>()

    lateinit var adapter: HistoryAdapter
    var historyList= mutableListOf<CompletedBooking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    private fun initUis() = binding.apply {
        headerAccept.endTitle.visibility = View.GONE
        headerAccept.headerTitle.text = getString(R.string.history)

        headerAccept.ivBack.setOnClickListener {
            finish()
        }

        rvAcceptedTrips.layoutManager = LinearLayoutManager(this@HistoryActivity)
        adapter = HistoryAdapter(this@HistoryActivity,historyList)
        rvAcceptedTrips.adapter = adapter

        historyApi()
        apisObservers()


    }


    fun historyApi() {
        showDialog()
        viewModels.history(

            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.VEHICLE_CAT_ID),
            "4",
            SharedPref.readString(SharedPref.USER_ID)
        )

    }

    fun apisObservers() {
        viewModels.hostoryResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CompletedBookingResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                historyList.clear()
                                if (data.data?.size!! > 0)
                                {
                                    binding.tvNoHistory.visibility=View.GONE
                                    historyList.addAll(data.data)
                                }
                                else{
                                    binding.tvNoHistory.visibility=View.VISIBLE

                                }
                                adapter.notifyDataSetChanged()

                                /*if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {

                                    binding.tvPagesText.text = data.data?.descriptionEn
                                }
                                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
                                    binding.tvPagesText.text = data.data?.descriptionAr

                                }*/

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