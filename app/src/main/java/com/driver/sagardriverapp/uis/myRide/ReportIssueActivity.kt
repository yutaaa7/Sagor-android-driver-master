package com.driver.sagardriverapp.uis.myRide

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.base.BaseActivity
import com.driver.sagardriverapp.databinding.ActivityReportIssueBinding
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.ReportIssue
import com.driver.sagardriverapp.model.ReportIssueResponse
import com.driver.sagardriverapp.uis.myRide.viewModels.ReportAnIssueViewModel
import com.driver.sagardriverapp.utils.AppConstants
import com.driver.sagardriverapp.utils.SharedPref

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportIssueActivity : BaseActivity() {
    lateinit var binding: ActivityReportIssueBinding

    lateinit var reportAnIssueReasonsAdapter: ReportAnIssueReasonsAdapter
    var reportIssueReasonsList = mutableListOf<ReportIssue>()
    var booking_id: String? = null
    var reason_id: String? = null
    var bundle: Bundle? = null

    private val viewModels: ReportAnIssueViewModel by viewModels<ReportAnIssueViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReportIssueBinding.inflate(layoutInflater)
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
            booking_id = bundle?.getString("ride_id")
        }

        headerReport.headerTitle.text = getString(R.string.report_an_issue)
        headerReport.ivBack.setOnClickListener {
            finish()
        }
        btRepostIssueSubmit.setOnClickListener {
            reportAnIssueAPi()
        }
        rvReportIssueReasons.layoutManager = LinearLayoutManager(this@ReportIssueActivity)
        reportAnIssueReasonsAdapter =
            ReportAnIssueReasonsAdapter(this@ReportIssueActivity, reportIssueReasonsList,
                object : ReportAnIssueReasonsAdapter.onItemClickListenre {
                    override fun onClick(id: Int?) {
                        reason_id = id.toString()
                    }

                })
        rvReportIssueReasons.adapter = reportAnIssueReasonsAdapter
        reportAnIssueReasonsAPi()
        apisObservers()
    }

    fun reportAnIssueReasonsAPi() {
        showDialog()
        viewModels.reportAnIssueReasonsApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)
        )

    }

    fun reportAnIssueAPi() {
        if (reason_id.isNullOrEmpty()) {
            showToast(getString(R.string.please_select_atleast_one_reason))
        } else {
            showDialog()
            viewModels.reportAnIssue(
                "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
                SharedPref.readString(SharedPref.USER_ID),
                booking_id.toString(),
                reason_id.toString(),
                binding.etDescription.text.toString()
            )
        }

    }

    fun apisObservers() {
        viewModels.reportAnIssueReasonsResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is ReportIssueResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                reportIssueReasonsList.clear()
                                data.data?.let { it1 -> reportIssueReasonsList.addAll(it1) }
                                reportAnIssueReasonsAdapter.notifyDataSetChanged()
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

                        }else {
                            showToast(it.errorBody?.message.toString())
                        }

                    }
                }


            }
        })
        viewModels.reportAnIssueResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CommonResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                finish()
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