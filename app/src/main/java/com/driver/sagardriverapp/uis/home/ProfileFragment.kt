package com.driver.sagardriverapp.uis.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.databinding.FragmentProfileBinding
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.GetLanguageStatusResponse
import com.driver.sagardriverapp.uis.HomeActivity
import com.driver.sagardriverapp.uis.home.viewModel.ProfileViewModel
import com.driver.sagardriverapp.uis.login.DriverLoginActivity
import com.driver.sagardriverapp.utils.AppConstants
import com.driver.sagardriverapp.utils.CommonDialog
import com.driver.sagardriverapp.utils.SharedPref
import com.driver.sagardriverapp.utils.Util
import com.driver.sagardriverapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val viewModels: ProfileViewModel by viewModels<ProfileViewModel>()
    private lateinit var dialog: Dialog
    private lateinit var binding: FragmentProfileBinding
    var language_state = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUis()
    }

    private fun initUis() = binding.apply {
        dialog = Dialog(requireActivity())
        headerProfile.headerTitle.text = getString(R.string.account_setting)
        headerProfile.ivBack.visibility = View.GONE


        tvTermsCondition.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "terms")
            requireActivity().launchActivity(
                PolicyTextScreenActivity::class.java,
                removeAllPrevActivities = false,
                bundle = bundle
            )

        }
        tvRefundPolicy.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "refund")
            requireActivity().launchActivity(
                PolicyTextScreenActivity::class.java,
                removeAllPrevActivities = false,
                bundle = bundle
            )

        }
        tvPrivacyPolicy.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "privacy")
            requireActivity().launchActivity(
                PolicyTextScreenActivity::class.java,
                removeAllPrevActivities = false,
                bundle = bundle
            )

        }
        tvContactUs.setOnClickListener {

            requireActivity().launchActivity(
                ContactUsActivity::class.java,
                removeAllPrevActivities = false
            )

        }

        tvLanguage.setOnClickListener {

            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.language_dialog, null)

            // Create the dialog
            val dialog = Dialog(requireContext(), R.style.DialogBackgroundStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(view)

            // Set the button texts
            val positiveButton = view.findViewById<TextView>(R.id.tvPostiveText)
            val negativeButton = view.findViewById<TextView>(R.id.tvNegetiveText)

            var lang_id = SharedPref.readString(SharedPref.LANGUAGE_ID)
            Log.d("LANGUAGR", lang_id)

            if (lang_id.equals("0")) {
                Log.d("LANGUAGR", lang_id)

                negativeButton.setBackgroundResource(R.drawable.solid_theme_blue)
                negativeButton.setTextColor(requireActivity().getColor(R.color.white))
                positiveButton.setBackgroundResource(R.drawable.stroke_theme_blue)
                positiveButton.setTextColor(requireActivity().getColor(R.color.theme_blue))

            }
            if (lang_id.equals("1")) {
                Log.d("LANGUAGR", lang_id)

                negativeButton.setBackgroundResource(R.drawable.stroke_theme_blue)
                negativeButton.setTextColor(requireActivity().getColor(R.color.theme_blue))
                positiveButton.setBackgroundResource(R.drawable.solid_theme_blue)
                positiveButton.setTextColor(requireActivity().getColor(R.color.white))
            }

            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            positiveButton.setOnClickListener {
                language_state = "1"
                negativeButton.setBackgroundResource(R.drawable.stroke_theme_blue)
                negativeButton.setTextColor(requireActivity().getColor(R.color.theme_blue))
                positiveButton.setBackgroundResource(R.drawable.solid_theme_blue)
                positiveButton.setTextColor(requireActivity().getColor(R.color.white))
                languageUpdateApi("1")
                dialog.dismiss()

            }
            negativeButton.setOnClickListener {
                language_state = "0"
                negativeButton.setBackgroundResource(R.drawable.solid_theme_blue)
                negativeButton.setTextColor(requireActivity().getColor(R.color.white))
                positiveButton.setBackgroundResource(R.drawable.stroke_theme_blue)
                positiveButton.setTextColor(requireActivity().getColor(R.color.theme_blue))

                languageUpdateApi("0")
                dialog.dismiss()

            }
            dialog.show()


            /*showDialog(getString(R.string.confirmation),
                getString(R.string.are_yo_sure_you_want_to_log_out),
                getString(R.string.logout),
                getString(R.string.cancel))*/
        }

        tvLogout.setOnClickListener {
            showDialog(
                getString(R.string.confirmation),
                getString(R.string.are_yo_sure_you_want_to_log_out),
                getString(R.string.logout),
                getString(R.string.cancel)
            )
        }
        getLanguageStatusApi()
        apiObserver()
    }

    fun languageUpdateApi(lang_id: String) {
        showDialog()
        var device_id =
            Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)

        viewModels.languageUpdate(
            SharedPref.readString(SharedPref.USER_ID),
            device_id,
            lang_id,
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)

        )

    }

    fun getLanguageStatusApi() {
        showDialog()
        var device_id =
            Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)

        viewModels.getLanguageStatus(
            SharedPref.readString(SharedPref.USER_ID),
            device_id,
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)

        )
    }

    fun showDialog(
        title: String,
        subtitle: String,
        positiveButtonText: String,
        negativeButtonText: String
    ) {
        // Create the dialog
        val dialog = CommonDialog.createDialog(
            context = requireContext(),
            title = title,
            subtitle = subtitle,
            positiveButtonText = positiveButtonText,
            negativeButtonText = negativeButtonText,
            positiveButtonCallback = {
                logoutApi()
            },
            negativeButtonCallback = {

            }
        )


        // Show the dialog
        dialog.show()
    }

    fun logoutApi() {
        showDialog()
        viewModels.logout(

            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)
        )

    }

    fun apiObserver() {
        viewModels.logoutResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CommonResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
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
        viewModels.getLanguageStatusResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is GetLanguageStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                SharedPref.writeString(
                                    SharedPref.LANGUAGE_ID,
                                    data.data?.langId.toString()
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

        viewModels.languageUpdateResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CommonResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                Log.d("languageUpdateResponse", language_state)
                                SharedPref.writeString(SharedPref.LANGUAGE_ID, language_state)

                                if (language_state.equals("0"))
                                {
                                    Util.setLocale(requireActivity(), "en")

                                }
                                if (language_state.equals("1"))
                                {
                                    Util.setLocale(requireActivity(), "ar")

                                }
                                val bundle = Bundle()
                                bundle.putString("where", "login")
                                requireActivity().launchActivity(
                                    HomeActivity::class.java,
                                    removeAllPrevActivities = true,
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