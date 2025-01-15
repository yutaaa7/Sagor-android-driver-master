package com.driver.sagardriverapp.uis.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.model.CompletedBookingResponse
import com.driver.sagardriverapp.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val historyRepository: HistoryRepository) :
    ViewModel() {

    var hostoryResponse = MutableLiveData<Response<CompletedBookingResponse>>()

    fun history(
        authorization: String,
        vehicle_cat_id: String,
        booking_status: String,
        driver_id: String
    ) {
        viewModelScope.launch {
            var response = historyRepository.History(
                authorization = authorization,
                vehicle_cat_id = vehicle_cat_id,
                booking_status = booking_status,
                driver_id = driver_id
            )
            hostoryResponse.postValue(Response.Loading(null))
            hostoryResponse.postValue(response)
        }
    }


}