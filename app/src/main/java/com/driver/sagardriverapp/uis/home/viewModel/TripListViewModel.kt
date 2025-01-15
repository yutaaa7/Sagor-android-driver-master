package com.driver.sagardriverapp.uis.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.model.AcceptRidelResponse
import com.driver.sagardriverapp.model.TripListResponse
import com.driver.sagardriverapp.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripListViewModel @Inject constructor(private val tripRepository: TripRepository) :
    ViewModel() {

    var tripListResponse = MutableLiveData<Response<TripListResponse>>()
    var acceptTripResponse = MutableLiveData<Response<AcceptRidelResponse>>()

    fun tripList(
        authorization: String,
        vehicle_cat_id: String,
        booking_status: String,
        start_date: String
    ) {
        viewModelScope.launch {
            var response = tripRepository.tripList(

                authorization = authorization,
                vehicle_cat_id = vehicle_cat_id,
                booking_status = booking_status,
                start_date = start_date,
            )
            tripListResponse.postValue(Response.Loading(null))
            tripListResponse.postValue(response)
        }
    }

    fun acceptTrip(
        authorization: String,
        booking_id: String,
        driver_id: String
    ) {
        viewModelScope.launch {
            var response = tripRepository.acceptList(

                authorization = authorization,
                booking_id = booking_id,
                driver_id = driver_id
            )
            acceptTripResponse.postValue(Response.Loading(null))
            acceptTripResponse.postValue(response)
        }
    }


}