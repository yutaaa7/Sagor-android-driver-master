package com.driver.sagardriverapp.uis.myRide.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.ReportIssueResponse
import com.driver.sagardriverapp.repository.ReportAnIssueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportAnIssueViewModel @Inject constructor(private val reportAnIssueRepository: ReportAnIssueRepository) :
    ViewModel() {

    var reportAnIssueReasonsResponse = MutableLiveData<Response<ReportIssueResponse>>()
    var reportAnIssueResponse = MutableLiveData<Response<CommonResponse>>()

    fun reportAnIssueReasonsApi(
        authorization: String
    ) {
        viewModelScope.launch {
            var response = reportAnIssueRepository.reportAnIssueReasons(
                authorization = authorization
            )
            reportAnIssueReasonsResponse.postValue(Response.Loading(null))
            reportAnIssueReasonsResponse.postValue(response)
        }
    }
    fun reportAnIssue(
        authorization: String,
        driver_id: String,
        trip_id: String,
        reason_id: String,
        comment: String,
    ) {
        viewModelScope.launch {
            var response = reportAnIssueRepository.reportAnIssue(
                authorization = authorization,
                driver_id = driver_id,
                trip_id=trip_id,
                reason_id=reason_id,
                comment=comment,
            )
            reportAnIssueResponse.postValue(Response.Loading(null))
            reportAnIssueResponse.postValue(response)
        }
    }


}