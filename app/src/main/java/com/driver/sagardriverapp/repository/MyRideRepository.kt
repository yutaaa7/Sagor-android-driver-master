package com.driver.sagardriverapp.repository

import com.driver.sagardriverapp.api.ApiService
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.api.ResponseHandler
import com.driver.sagardriverapp.model.AcceptRidelResponse
import com.driver.sagardriverapp.model.StartTripResponse
import com.driver.sagardriverapp.model.TripDetailResponse
import com.driver.sagardriverapp.model.UpdateDriverLocationResponse
import com.driver.sagardriverapp.utils.AppConstants
import javax.inject.Inject

class MyRideRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun tripDetail(
        authorization: String,
        booking_id: String
    ): Response<TripDetailResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.tripDetail(
                    authorization = authorization,
                    booking_id = booking_id

                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


    suspend fun tripGo(
        authorization: String,
        booking_id: String,
        driver_id: String,
        trip_location_id: String,
    ): Response<StartTripResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.goTrip(
                    authorization = authorization,
                    booking_id = booking_id,
                    driver_id = driver_id,
                    trip_location_id = trip_location_id
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

    suspend fun startTrip(
        authorization: String,
        booking_id: String,
        driver_id: String,
        trip_location_id: String,

    ): Response<StartTripResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.startTrip(
                    authorization = authorization,
                    booking_id = booking_id,
                    driver_id = driver_id,
                    trip_location_id = trip_location_id,


                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun updateDriverLocation(
        authorization: String,
        booking_id: String,
        driver_id: String,
        current_latitude: String,
        current_longitude: String,
        trip_location_id: String,
        current_distance_time: String,
        current_distance_time_unit: String,
        trip_status: String,
        current_location_title: String,
        current_location_address: String,
    ): Response<UpdateDriverLocationResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.updateDriverLocation(
                    authorization = authorization,
                    booking_id = booking_id,
                    driver_id = driver_id,
                    current_latitude = current_latitude,
                    current_longitude = current_longitude,
                    trip_location_id = trip_location_id,
                    current_distance_time=current_distance_time,
                    current_distance_time_unit=current_distance_time_unit,
                    trip_status=trip_status,
                    current_location_title=current_location_title,
                    current_location_address=current_location_address,
                    ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun endTrip(
        authorization: String,
        booking_id: String,
        driver_id: String,
        trip_location_id: String,
        distance_covered: String,
        distance_covered_unit: String
    ): Response<StartTripResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.endTrip(
                    authorization = authorization,
                    booking_id = booking_id,
                    driver_id = driver_id,
                    trip_location_id = trip_location_id,
                    distance_covered = distance_covered,
                    distance_covered_unit = distance_covered_unit

                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}