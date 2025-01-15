package com.driver.sagardriverapp.repository

import com.driver.sagardriverapp.api.ApiService
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.api.ResponseHandler
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.SchoolDriverTripDetailResponse
import com.driver.sagardriverapp.model.UpdateDriverLocationResponse
import com.driver.sagardriverapp.utils.AppConstants
import javax.inject.Inject

class MyRideChildRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun tripDetailsForSChoolDriverTrips(

        authorization: String,
        ride_id: String,

        ): Response<SchoolDriverTripDetailResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.tripDetailOfSchoolDriverTrip(

                    authorization = authorization,
                    ride_id = ride_id
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun updateSchoolDriverLocation(
        authorization: String,
        //booking_id: String,
        driver_id: String,
        current_latitude: String,
        current_longitude: String,
        trip_location_id: String,
    ): Response<UpdateDriverLocationResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.updateSchoolDriverLocation(
                    authorization = authorization,
                    //booking_id = booking_id,
                    driver_id = driver_id,
                    current_latitude = current_latitude,
                    current_longitude = current_longitude,
                    trip_location_id = trip_location_id,

                    ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }
  suspend fun endChildTrip(

        authorization: String,
        ride_id: String,

        ): Response<CommonResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.endChildTrip(

                    authorization = authorization,
                    ride_id = ride_id
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}