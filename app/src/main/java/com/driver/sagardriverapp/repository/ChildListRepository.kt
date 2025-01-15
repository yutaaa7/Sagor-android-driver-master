package com.driver.sagardriverapp.repository

import com.driver.sagardriverapp.api.ApiService
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.api.ResponseHandler
import com.driver.sagardriverapp.model.ChildListResponse
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.utils.AppConstants
import javax.inject.Inject

class ChildListRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun childList(

        authorization: String,
        ride_id: String,
        search_keyword: String
    ): Response<ChildListResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.ChildList(

                    authorization = authorization,
                    ride_id = ride_id,
                    search_keyword = search_keyword
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun childBoard(

        authorization: String,
        child_id: String,
        pickup_time: String,
        pickup_time_unit: String,
        pickup_latitude: String,
        pickup_longitude: String,
        pickup_location_title: String,
        pickup_location_address: String,
    ): Response<CommonResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.childBoarded(

                    authorization = authorization,
                    child_id = child_id,
                    pickup_time = pickup_time,
                    pickup_time_unit = pickup_time_unit,
                    pickup_latitude = pickup_latitude,
                    pickup_longitude = pickup_longitude,
                    pickup_location_title = pickup_location_title,
                    pickup_location_address = pickup_location_address
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun childDropped(

        authorization: String,
        child_id: String
    ): Response<CommonResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.childDrpped(

                    authorization = authorization,
                    child_id = child_id
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}