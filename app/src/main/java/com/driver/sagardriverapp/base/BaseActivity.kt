package com.driver.sagardriverapp.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.uis.login.DriverLoginActivity
import com.driver.sagardriverapp.utils.CommonDialog
import com.driver.sagardriverapp.utils.SharedPref
import com.driver.sagardriverapp.utils.Util.launchActivity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var dialog: Dialog

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        dialog = Dialog(this)
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

    /**
     * this function is use for showing toast message
     */
    protected fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * this function is use for printing log
     */
    protected fun showLog(key: String, message: String) {
        Log.d(key, message)
    }

    /**
     * this function is use for  keyboard hide
     */
    @SuppressLint("SuspiciousIndentation")
    protected fun keyBoardHide(activity: Activity) {
        try {
            val focusedView: View = activity.getCurrentFocus()!!
            (activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                focusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

     fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

     fun requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE: Int) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
    // Request location and foreground service permissions
    /*fun requestLocationPermissionWithSer(LOCATION_PERMISSION_REQUEST_CODE: Int) {
        val permissionsToRequest = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        ActivityCompat.requestPermissions(
            this,
            permissionsToRequest.toTypedArray(),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
    fun hasLocationWithServicePermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasForegroundServiceLocationPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED
            } else {
                true // No need to check for FOREGROUND_SERVICE_LOCATION on older Android versions
            }

        return hasFineLocationPermission && hasForegroundServiceLocationPermission
    }*/

    // Request location, foreground service, and notification permissions
    fun requestLocationPermissionWithSer(LOCATION_PERMISSION_REQUEST_CODE: Int) {
        val permissionsToRequest = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS) // Add notification permission
        }

        ActivityCompat.requestPermissions(
            this,
            permissionsToRequest.toTypedArray(),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    fun hasLocationWithServicePermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasForegroundServiceLocationPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED
            } else {
                true // No need to check for FOREGROUND_SERVICE_LOCATION on older Android versions
            }

        val hasNotificationPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                true // No need to check for POST_NOTIFICATIONS on older Android versions
            }

        return hasFineLocationPermission && hasForegroundServiceLocationPermission && hasNotificationPermission
    }

    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371.0 // Radius of the earth in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c // Distance in kilometers
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun shouldEnableStartRideButton(pickupDate: String, pickupTime: String, timeUnit: String): Boolean {
        // Parse pickup date
        val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val pickupLocalDate = LocalDate.parse(pickupDate, formatterDate)

        // Parse pickup time and adjust for AM/PM
        val formatterTime = DateTimeFormatter.ofPattern("hh:mm:ss", Locale.getDefault())
        val parsedTime = LocalTime.parse(pickupTime, formatterTime)
        val adjustedTime = if (timeUnit.equals("pm", ignoreCase = true) && parsedTime.hour < 12) {
            parsedTime.plusHours(12)
        } else {
            parsedTime
        }
        val pickupDateTime = LocalDateTime.of(pickupLocalDate, adjustedTime)

        // Get the current date and time
        val currentDateTime = LocalDateTime.now()

        // Check if pickup is today
        val isToday = pickupLocalDate.isEqual(LocalDate.now())

        // Check if we are within 1 hour before the pickup time
        val isWithinOneHourBefore = currentDateTime.isAfter(pickupDateTime.minusHours(1)) &&
                currentDateTime.isBefore(pickupDateTime)

        return isToday && isWithinOneHourBefore
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

    /*// Usage
    val isStartRideEnabled = shouldEnableStartRideButton(
        pickupDate = "2024-10-24",
        pickupTime = "15:00:00",
        timeUnit = "pm"
    )*/


   /* fun showAuthenticateDialog() {
        val dialog = CommonDialog.createDialog(
            context = this,
            title = getString(R.string.alert),
            subtitle = getString(R.string.your_toekn_has_been_expired_please_login_again_to_continue_your_services),
            positiveButtonText = getString(R.string.ok),
            negativeButtonText = getString(R.string.cancel),
            positiveButtonCallback = {
                launchActivity(
                    LanguageSelectActivity::class.java,
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
            negativeButtonCallback = {

            }
        )


        // Show the dialog
        dialog.show()
    }*/

    fun showAuthenticateDialog() {
        val dialog = CommonDialog.createAuthDialog(
            context = this,
            positiveButtonCallback = {
                launchActivity(
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