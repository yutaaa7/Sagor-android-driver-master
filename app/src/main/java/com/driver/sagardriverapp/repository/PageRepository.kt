package com.driver.sagardriverapp.repository

import com.driver.sagardriverapp.api.ApiService
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.api.ResponseHandler
import com.driver.sagardriverapp.model.PagesResponse
import com.driver.sagardriverapp.utils.AppConstants
import javax.inject.Inject

class PageRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun getPageText(
        page: String,
        authorization: String
    ): Response<PagesResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.getPageText(
                    page = page,
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