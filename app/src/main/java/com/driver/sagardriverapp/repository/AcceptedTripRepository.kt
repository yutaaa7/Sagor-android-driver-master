package com.driver.sagardriverapp.repository

import com.driver.sagardriverapp.api.ApiService
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.api.ResponseHandler
import com.driver.sagardriverapp.model.AcceptBookingsListResponse
import com.driver.sagardriverapp.model.SchoolDriverTripListResponse
import com.driver.sagardriverapp.utils.AppConstants
import javax.inject.Inject

class AcceptedTripRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun acceptedTripList(
        authorization: String,
        vehicle_cat_id: String,
        booking_status: String,
        driver_id: String
    ): Response<AcceptBookingsListResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.AcceptedTripsList(
                    authorization = authorization,
                    vehicle_cat_id = vehicle_cat_id,
                    booking_status = booking_status,
                    driver_id = driver_id,

                    ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun schoolDriverTripsList(
        authorization: String,
        driver_id: String,
        school_id: String,
        date: String,
        bus_id: String,
    ): Response<SchoolDriverTripListResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.schoolDriverTripList(
                    authorization = authorization,
                    driver_id = driver_id,
                    school_id = school_id,
                    date = date,
                    bus_id=bus_id
                    ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}