package com.driver.sagardriverapp.repository

import com.driver.sagardriverapp.api.ApiService
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.api.ResponseHandler
import com.driver.sagardriverapp.model.AcceptRidelResponse
import com.driver.sagardriverapp.model.TripListResponse
import com.driver.sagardriverapp.utils.AppConstants
import javax.inject.Inject

class TripRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun tripList(
        authorization: String,
        vehicle_cat_id: String,
        booking_status: String,
        start_date: String
    ): Response<TripListResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.tripList(
                    authorization = authorization,
                    vehicle_cat_id = vehicle_cat_id,
                    booking_status = booking_status,
                    start_date = start_date,

                    ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun acceptList(
        authorization: String,
        booking_id: String,
        driver_id: String
    ): Response<AcceptRidelResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.acceptTrip(
                    authorization = authorization,
                    booking_id = booking_id,
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