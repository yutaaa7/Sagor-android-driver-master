package com.driver.sagardriverapp.api

import com.google.gson.annotations.SerializedName

data class ErrorResponse(@field:SerializedName("statusCode") val status: Int,
                         @field:SerializedName("message") val message: String,
                         @field:SerializedName("messageCode") val messageCode: String,
                         @field:SerializedName("success") val success: Boolean,
                         @field:SerializedName("timestamp") val timeStamp: Long,
                         @field:SerializedName("errors") val error: String,
                         @field:SerializedName("errorMessages") val errorMessages: ArrayList<String>)