package com.driver.sagardriverapp.repository

import com.driver.sagardriverapp.api.ApiService
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.api.ResponseHandler
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.GetLanguageStatusResponse
import com.driver.sagardriverapp.utils.AppConstants
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun logout(
        auth: String,

    ): Response<CommonResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.logout(
                   authorization = auth
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun languageUpdate(
        user_id: String,
        device_token: String,
        lang_id: String,
        authorization: String
    ): Response<CommonResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.languageUpdate(
                    user_id = user_id,
                    device_type = AppConstants.DEVICE_TYPE,
                    device_token = device_token,
                    lang_id = lang_id,
                    authorization = authorization
                ), AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


    suspend fun getLanguageStatus(
        user_id: String,
        device_token: String,
        authorization: String
    ): Response<GetLanguageStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.getLanguageStatus(
                    user_id = user_id,
                    device_type = AppConstants.DEVICE_TYPE,
                    device_token = device_token,
                    authorization = authorization
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

}