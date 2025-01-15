package com.driver.sagardriverapp.repository

import com.driver.sagardriverapp.api.ApiService
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.api.ResponseHandler
import com.driver.sagardriverapp.model.CompletedBookingResponse
import com.driver.sagardriverapp.utils.AppConstants
import javax.inject.Inject

class HistoryRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun History(

        authorization: String,
        vehicle_cat_id: String,
        booking_status: String,
        driver_id: String
    ): Response<CompletedBookingResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.historyOfNormalTrip(
                    authorization = authorization,
                    vehicle_cat_id = vehicle_cat_id,
                    booking_status = booking_status,
                    driver_id = driver_id
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}