package com.driver.sagardriverapp.uis.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.model.AcceptBookingsListResponse
import com.driver.sagardriverapp.model.SchoolDriverTripListResponse
import com.driver.sagardriverapp.repository.AcceptedTripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcceptedTripViewModel @Inject constructor(private val acceptedTripRepository: AcceptedTripRepository) :
    ViewModel() {

    var tripListResponse = MutableLiveData<Response<AcceptBookingsListResponse>>()
    var schoolDriverTripListResponse = MutableLiveData<Response<SchoolDriverTripListResponse>>()

    fun acceptedTripsList(
        authorization: String,
        vehicle_cat_id: String,
        booking_status: String,
        driver_id: String
    ) {
        viewModelScope.launch {
            var response = acceptedTripRepository.acceptedTripList(

                authorization = authorization,
                vehicle_cat_id = vehicle_cat_id,
                booking_status = booking_status,
                driver_id = driver_id,
            )
            tripListResponse.postValue(Response.Loading(null))
            tripListResponse.postValue(response)
        }
    }

    fun schoolTripListApi(
        authorization: String,
        driver_id: String,
        school_id: String,
        date: String,
        bus_id: String,
    ) {
        viewModelScope.launch {
            var response = acceptedTripRepository.schoolDriverTripsList(

                authorization = authorization,
                driver_id = driver_id,
                school_id = school_id,
                date = date,
                bus_id=bus_id
            )
            schoolDriverTripListResponse.postValue(Response.Loading(null))
            schoolDriverTripListResponse.postValue(response)
        }
    }




}