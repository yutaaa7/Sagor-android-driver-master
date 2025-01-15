package com.driver.sagardriverapp.repository

import com.driver.sagardriverapp.api.ApiService
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.api.ResponseHandler
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.utils.AppConstants
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun updateFcmToken(

        authorization: String,
        email: String,
        device_type:String,
        device_token:String,
        type:String,
        device_version:String,
        fcm_token:String,

    ): Response<CommonResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.updateFcmToken(

                    authorization = authorization,
                    email=email,
                    device_type=device_type,
                    device_token=device_token,
                    type=type,
                    device_version=device_version,
                    fcm_token=fcm_token,
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}