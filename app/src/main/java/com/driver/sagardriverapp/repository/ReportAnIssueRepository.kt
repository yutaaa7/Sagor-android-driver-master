package com.driver.sagardriverapp.repository

import com.driver.sagardriverapp.api.ApiService
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.api.ResponseHandler
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.ReportIssueResponse
import com.driver.sagardriverapp.utils.AppConstants
import javax.inject.Inject

class ReportAnIssueRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun reportAnIssueReasons(
        authorization: String
    ): Response<ReportIssueResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.ReportIssueReasons(
                    authorization = authorization
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }
 suspend fun reportAnIssue(
        authorization: String,
        driver_id: String,
        trip_id: String,
        reason_id: String,
        comment: String,

    ): Response<CommonResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.reportAnIssue(
                    authorization = authorization,
                    driver_id = driver_id,
                    trip_id=trip_id,
                    reason_id=reason_id,
                    comment=comment,
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}