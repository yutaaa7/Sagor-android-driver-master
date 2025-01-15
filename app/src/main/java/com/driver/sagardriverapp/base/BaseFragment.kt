package com.driver.sagardriverapp.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.uis.login.DriverLoginActivity
import com.driver.sagardriverapp.utils.CommonDialog
import com.driver.sagardriverapp.utils.SharedPref
import com.driver.sagardriverapp.utils.Util.launchActivity


abstract class BaseFragment : Fragment() {
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        dialog = Dialog(requireActivity())
    }
    protected fun showToast(message: String?) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }


    /**
     * this function is use for showing progress dialog
     */
    fun showDialog() {
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

    fun getFirstErrorMessage(errors: String): String {
        return if (errors.contains(",")) {
            // If there is a comma, get the first part before the comma
            errors.substringBefore(",")
        } else {
            // If no comma, return the whole error string
            errors
        }
    }

    /**
     * this function is use for hiding progress dialog
     */
    fun hideDialog() {
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