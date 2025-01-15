package com.driver.sagardriverapp.uis.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.base.BaseActivity
import com.driver.sagardriverapp.databinding.ActivityPolicyTextScreenBinding
import com.driver.sagardriverapp.model.PagesResponse
import com.driver.sagardriverapp.uis.home.viewModel.PageViewModel
import com.driver.sagardriverapp.utils.AppConstants
import com.driver.sagardriverapp.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PolicyTextScreenActivity : BaseActivity() {
    var bundle: Bundle? = null
    var show: String? = null

    lateinit var binding: ActivityPolicyTextScreenBinding
    private val viewModels: PageViewModel by viewModels<PageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPolicyTextScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    private fun initUis() = binding.apply {

        headerTerms.ivBack.setOnClickListener{
            finish()
        }

        bundle = intent.extras

        if (bundle != null) {

            show = bundle?.getString("title")

        }
        if (show.equals("terms")) {
            headerTerms.headerTitle.text = getString(R.string.terms_condition)
            getPagesDataApi("3")
        }
        if (show.equals("refund")) {
            headerTerms.headerTitle.text = getString(R.string.refund_policy)
            getPagesDataApi("4")
        }
        if (show.equals("privacy")) {
            headerTerms.headerTitle.text =
                getString(R.string.privacy_policy)
            getPagesDataApi("2")
        }

        apisObservers()
       /* if (show.equals("terms")) headerTerms.headerTitle.text = getString(R.string.terms_condition)
        if (show.equals("refund")) headerTerms.headerTitle.text = getString(R.string.refund_policy)
        if (show.equals("privacy")) headerTerms.headerTitle.text = getString(R.string.privacy_policy)*/
    }
    fun getPagesDataApi(page: String) {
        showDialog()
        viewModels.pagesData(
            page = page,
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)
        )

    }
    fun apisObservers() {
        viewModels.pageResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is PagesResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {

                                    binding.tvPagesText.text = data.data?.descriptionEn
                                }
                                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
                                    binding.tvPagesText.text = data.data?.descriptionAr

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
    }
}