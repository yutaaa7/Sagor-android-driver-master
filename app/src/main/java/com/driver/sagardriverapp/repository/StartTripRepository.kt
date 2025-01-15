package com.driver.sagardriverapp.repository

import com.driver.sagardriverapp.api.ApiService
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.api.ResponseHandler
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.SchoolDriverTripDetailResponse
import com.driver.sagardriverapp.utils.AppConstants
import javax.inject.Inject

class StartTripRepository @Inject constructor(private val apiService: ApiService) {
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

    suspend fun startChildTrips(

        authorization: String,
        ride_id: String,

        ): Response<CommonResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.startChildTrip(

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