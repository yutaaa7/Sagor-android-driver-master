package com.driver.sagardriverapp.repository

import com.driver.sagardriverapp.api.ApiService
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.api.ResponseHandler
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.LoginResponse
import com.driver.sagardriverapp.utils.AppConstants
import javax.inject.Inject

class LoginRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun login(
        email: String,
        password: String,
        device_type: String,
        device_token: String,
        type: String,
        device_version: String,
    ): Response<LoginResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.login(
                    email = email,
                    password = password,
                    device_type = device_type,
                    device_token = device_token,
                    type = type,
                    device_version=device_version
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


}