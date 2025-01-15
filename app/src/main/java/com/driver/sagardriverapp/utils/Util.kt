package com.driver.sagardriverapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.regex.Pattern

object Util {
    private val emailRegex = Pattern.compile(
        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
    )
    fun isValidEmail(email: String): Boolean {
        var value = false
        if (!TextUtils.isEmpty(email) && emailRegex.matcher(email).matches()) {
            value = true
        }
        return value
    }

    fun Activity.launchActivity(
        className: Class<*>,
        bundle: Bundle? = null,
        view: View? = null,
        removeAllPrevActivities: Boolean = false
    ) {
        val activityLaunchIntent = Intent(this, className)
        bundle?.let { activityLaunchIntent.putExtras(it) }
        startActivity(activityLaunchIntent)

        if (removeAllPrevActivities) {
            this@launchActivity.finishAffinity()
        }
    }

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Log.d("LocaleChange", "Changing language to: $languageCode")
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            configuration.setLocales(LocaleList(locale))
        } else {
            configuration.locale = locale
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)

        /*val locale = Locale(languageCode)
        Locale.setDefault(locale)

        var config = context.resources.configuration
        config.setLocale(locale)

        if (languageCode == "ar") {
            config.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            config.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }

        context.resources.updateConfiguration(config, context.resources.displayMetrics)*/
    }

    /*@RequiresApi(Build.VERSION_CODES.O)
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
*/

    /*@RequiresApi(Build.VERSION_CODES.O)
    fun shouldEnableStartRideButton(pickupDate: String, pickupTime: String, timeUnit: String): Boolean {
        // Parse pickup date
        val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val pickupLocalDate = LocalDate.parse(pickupDate, formatterDate)

        // Parse pickup time using a 24-hour format
        val formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
        var parsedTime = LocalTime.parse(pickupTime, formatterTime)

        // Adjust for PM if the timeUnit is "PM" and hour is before 12
        if (timeUnit.equals("pm", ignoreCase = true) && parsedTime.hour < 12) {
            parsedTime = parsedTime.plusHours(12)
        }

        // Construct the pickup datetime from date and time
        val pickupDateTime = LocalDateTime.of(pickupLocalDate, parsedTime)

        // Get the current date and time
        val currentDateTime = LocalDateTime.now()

        // Check if pickup is today
        val isToday = pickupLocalDate.isEqual(LocalDate.now())

        // Check if we are within 1 hour before the pickup time
        val isWithinOneHourBefore = currentDateTime.isAfter(pickupDateTime.minusHours(1)) &&
                currentDateTime.isBefore(pickupDateTime)

        // Log the values for debugging
        Log.d("StartRideButton", "Current DateTime: $currentDateTime")
        Log.d("StartRideButton", "Pickup DateTime: $pickupDateTime")
        Log.d("StartRideButton", "Is Today: $isToday")
        Log.d("StartRideButton", "Is Within One Hour Before: $isWithinOneHourBefore")

        return isToday && isWithinOneHourBefore
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    fun shouldEnableStartRideButton(pickupDate: String, pickupTime: String, timeUnit: String): Boolean {
        // Use the device's current time zone
        val timeZone = ZoneId.systemDefault()

        // Parse pickup date
        val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val pickupLocalDate = LocalDate.parse(pickupDate, formatterDate)

        // Parse pickup time in 24-hour format
        val formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
        var parsedTime = LocalTime.parse(pickupTime, formatterTime)

        // Adjust for PM if the timeUnit is "PM" and hour is before 12
        if (timeUnit.equals("pm", ignoreCase = true) && parsedTime.hour < 12) {
            parsedTime = parsedTime.plusHours(12)
        }

        // Construct the pickup ZonedDateTime from date, time, and device time zone
        val pickupDateTime = ZonedDateTime.of(pickupLocalDate, parsedTime, timeZone)

        // Get the current date and time in the same time zone
        val currentDateTime = ZonedDateTime.now(timeZone)

        // Check if pickup is today in the device's time zone
        val isToday = pickupDateTime.toLocalDate().isEqual(currentDateTime.toLocalDate())

        // Check if we are within 1 hour before the pickup time
        val isWithinOneHourBefore = currentDateTime.isAfter(pickupDateTime.minusHours(1)) &&
                currentDateTime.isBefore(pickupDateTime)

        // Log the values for debugging
        Log.d("StartRideButton", "Current DateTime: $currentDateTime")
        Log.d("StartRideButton", "Pickup DateTime: $pickupDateTime")
        Log.d("StartRideButton", "Is Today: $isToday")
        Log.d("StartRideButton", "Is Within One Hour Before: $isWithinOneHourBefore")

        return isToday && isWithinOneHourBefore
    }


}