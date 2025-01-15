package com.driver.sagardriverapp.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class Data(

    @field:SerializedName("gender")
    val gender: Any? = null,

    @field:SerializedName("mobile")
    val mobile: String? = null,

    @field:SerializedName("school_id")
    val school_id: String? = null,

    @field:SerializedName("bus_id")
    val bus_id: String? = null,

    @field:SerializedName("notification_status")
    val notificationStatus: Int? = null,

    @field:SerializedName("last_name")
    val lastName: Any? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("lang_id")
    val langId: Int? = null,

    @field:SerializedName("device_type")
    val deviceType: String? = null,

    @field:SerializedName("avatar")
    val avatar: Any? = null,

    @field:SerializedName("token")
    val token: String? = null,

    @field:SerializedName("country_code")
    val countryCode: Any? = null,

    @field:SerializedName("dob")
    val dob: Any? = null,

    @field:SerializedName("device_token")
    val deviceToken: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("vehicle_cat_id")
    val vehicleCatId: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,
    @field:SerializedName("type")
    val type: Int? = null,

    @field:SerializedName("first_name")
    val firstName: String? = null,

    @field:SerializedName("vehicle_id")
    val vehicleId: Any? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("location_status")
    val locationStatus: Int? = null
)
