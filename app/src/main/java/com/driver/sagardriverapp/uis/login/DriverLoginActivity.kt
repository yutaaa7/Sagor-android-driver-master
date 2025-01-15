package com.driver.sagardriverapp.uis.login

import android.app.Dialog
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.base.BaseActivity
import com.driver.sagardriverapp.databinding.ActivityDriverLoginBinding
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.LoginResponse
import com.driver.sagardriverapp.uis.HomeActivity
import com.driver.sagardriverapp.utils.AppConstants
import com.driver.sagardriverapp.utils.SharedPref
import com.driver.sagardriverapp.utils.Util
import com.driver.sagardriverapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverLoginActivity : BaseActivity() {

    lateinit var binding: ActivityDriverLoginBinding
    var type = "normal"
    var language_state = ""
    private val viewModels: LoginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDriverLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    private fun initUis() = binding.apply {
        tvUserName.setOnClickListener {
            tvUserName.visibility = View.GONE
            etUserName.visibility = View.VISIBLE
            etUserName.requestFocus()
            userNameLable.visibility = View.VISIBLE
        }
        tvPassword.setOnClickListener {
            tvPassword.visibility = View.GONE
            textInputLayoutPassword.visibility = View.VISIBLE
            etPassword.requestFocus()
            passwordLable.visibility = View.VISIBLE
        }
        btLogin.setOnClickListener {
            validations()

        }
        btChild.setOnClickListener {
            SharedPref.writeString(
                SharedPref.DRIVER_TYPE,
                type
            )
        }
        btNormal.setOnClickListener {

            SharedPref.writeString(
                SharedPref.DRIVER_TYPE,
                type
            )
        }
        apisObservers()
    }

    fun loginApi() {
        showDialog()
        var device_id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        viewModels.login(
            binding.etUserName.text.toString(), binding.etPassword.text.toString(),
            "android", device_id, "4", ""
        )

    }

    fun validations() {
        if (binding.etUserName.text.toString().isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_user_name))
        } else if (!Util.isValidEmail(binding.etUserName.text.toString())) {
            showToast(getString(R.string.please_enter_valid_email_address))
        } else if (binding.etPassword.text.toString().isNullOrEmpty()) {

            showToast(getString(R.string.please_enter_password))
        } /*else if (binding.etPassword.text.toString().length < 8) {
            showToast(getString(R.string.the_password_must_be_at_least_8_characters))
        }
        else if (binding.etPassword.text.toString().length > 11) {
            showToast(getString(R.string.phone_number_must_be_no_more_than_11_digits))
        }*/
        else {
            loginApi()
        }
    }

    fun apisObservers() {
        viewModels.loginResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is LoginResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {

                                SharedPref.writeString(
                                    SharedPref.ACCESS_TOKEN,
                                    data.data?.token.toString()
                                )

                                SharedPref.writeString(
                                    SharedPref.USER_ID,
                                    data.data?.id.toString()
                                )
                                SharedPref.writeString(
                                    SharedPref.EMAIL,
                                    data.data?.email.toString()
                                )
                                SharedPref.writeBoolean(SharedPref.IS_LOGIN, true)

                                // type 4 means normal and type 6 means child
                                if (data.data?.type == 4) {
                                    type = "normal"
                                }
                                if (data.data?.type == 6) {
                                    type = "child"
                                }

                                SharedPref.writeString(
                                    SharedPref.DRIVER_TYPE,
                                    type
                                )
                                SharedPref.writeString(
                                    SharedPref.VEHICLE_CAT_ID,
                                    data.data?.vehicleCatId.toString()
                                )
                                SharedPref.writeString(
                                    SharedPref.SCHOOL_ID,
                                    data.data?.school_id.toString()
                                )
                                SharedPref.writeString(
                                    SharedPref.BUS_ID,
                                    data.data?.bus_id.toString()
                                )
                                Log.d("Driver type ", SharedPref.readString(SharedPref.DRIVER_TYPE))
                                showLanguageDialog()

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
                    Log.d("TS",it.toString())
                    Log.d("TS",it.errorBody?.status.toString())
                    Log.d("TS",it.errorBody?.message.toString())
                    Log.d("TS",it.errorCode.toString())
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
        viewModels.languageUpdateResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CommonResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                //Log.d("languageUpdateResponse", language_state)
                                SharedPref.writeString(SharedPref.LANGUAGE_ID, language_state)
                                if (language_state.equals("0"))
                                {
                                    Util.setLocale(this, "en")

                                }
                                if (language_state.equals("1"))
                                {
                                    Util.setLocale(this, "ar")

                                }
                                val bundle = Bundle()
                                bundle.putString("type", type)
                                bundle.putString("where", "login")
                                launchActivity(
                                    HomeActivity::class.java, removeAllPrevActivities = false,
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
                    if (it.errorBody?.status == 422) {
                        showToast("The mobile has already been taken.")
                    } else if (it.errorBody?.status == 401) {
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

    fun showLanguageDialog() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.language_dialog, null)

        // Create the dialog
        val dialog = Dialog(this, R.style.DialogBackgroundStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)

        // Set the button texts
        val positiveButton = view.findViewById<TextView>(R.id.tvPostiveText)
        val negativeButton = view.findViewById<TextView>(R.id.tvNegetiveText)



        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        positiveButton.setOnClickListener {
            language_state = "1"
            /*
             negativeButton.setBackgroundResource(R.drawable.stroke_theme_blue)
             negativeButton.setTextColor(requireActivity().getColor(R.color.theme_blue))
             positiveButton.setBackgroundResource(R.drawable.solid_theme_blue)
             positiveButton.setTextColor(requireActivity().getColor(R.color.white))*/
            languageUpdateApi("1")
            dialog.dismiss()

        }
        negativeButton.setOnClickListener {
            language_state = "0"
            /*
             negativeButton.setBackgroundResource(R.drawable.solid_theme_blue)
             negativeButton.setTextColor(requireActivity().getColor(R.color.white))
             positiveButton.setBackgroundResource(R.drawable.stroke_theme_blue)
             positiveButton.setTextColor(requireActivity().getColor(R.color.theme_blue))
 */
            languageUpdateApi("0")
            dialog.dismiss()

        }
        dialog.show()
    }

    fun languageUpdateApi(lang_id: String) {
        showDialog()
        var device_id =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        viewModels.languageUpdate(
            SharedPref.readString(SharedPref.USER_ID),
            device_id,
            lang_id,
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)

        )

    }

}