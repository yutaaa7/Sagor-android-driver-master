package com.driver.sagardriverapp.uis.myRide.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.model.AcceptRidelResponse
import com.driver.sagardriverapp.model.StartTripResponse
import com.driver.sagardriverapp.model.TripDetailResponse
import com.driver.sagardriverapp.model.UpdateDriverLocationResponse
import com.driver.sagardriverapp.repository.MyRideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRideViewModel @Inject constructor(private val myRideRepository: MyRideRepository) :
    ViewModel() {

    var tripDetailResponse = MutableLiveData<Response<TripDetailResponse>>()
    var goTripResponse = MutableLiveData<Response<StartTripResponse>>()
    var acceptTripResponse = MutableLiveData<Response<AcceptRidelResponse>>()
    var startTripResponse = MutableLiveData<Response<StartTripResponse>>()
    var endTripResponse = MutableLiveData<Response<StartTripResponse>>()
    var updateDriverLocationResponse = MutableLiveData<Response<UpdateDriverLocationResponse>>()

    fun tripDetail(
        authorization: String,
        booking_id: String,

        ) {
        viewModelScope.launch {
            var response = myRideRepository.tripDetail(

                authorization = authorization,
                booking_id = booking_id
            )
            tripDetailResponse.postValue(Response.Loading(null))
            tripDetailResponse.postValue(response)
        }
    }

    fun tripGo(
        authorization: String,
        booking_id: String,
        driver_id: String,
        trip_location_id: String,

        ) {
        viewModelScope.launch {
            var response = myRideRepository.tripGo(
                authorization = authorization,
                booking_id = booking_id,
                driver_id = driver_id,
                trip_location_id = trip_location_id
            )
            goTripResponse.postValue(Response.Loading(null))
            goTripResponse.postValue(response)
        }
    }

    fun acceptTrip(
        authorization: String,
        booking_id: String,
        driver_id: String
    ) {
        viewModelScope.launch {
            var response = myRideRepository.acceptList(

                authorization = authorization,
                booking_id = booking_id,
                driver_id = driver_id
            )
            acceptTripResponse.postValue(Response.Loading(null))
            acceptTripResponse.postValue(response)
        }
    }

    fun startTrip(
        authorization: String,
        booking_id: String,
        driver_id: String,
        trip_location_id: String,

    ) {
        viewModelScope.launch {
            var response = myRideRepository.startTrip(
                authorization = authorization,
                booking_id = booking_id,
                driver_id = driver_id,
                trip_location_id = trip_location_id,

            )
            startTripResponse.postValue(Response.Loading(null))
            startTripResponse.postValue(response)
        }
    }

    fun updateDriverLocation(
        authorization: String,
        booking_id: String,
        driver_id: String,
        current_latitude: String,
        current_longitude: String,
        trip_location_id: String,
        current_distance_time: String,
        current_distance_time_unit: String,
        trip_status: String,
        current_location_title: String,
        current_location_address: String,
    ) {
        viewModelScope.launch {
            var response = myRideRepository.updateDriverLocation(
                authorization = authorization,
                booking_id = booking_id,
                driver_id = driver_id,
                current_latitude = current_latitude,
                current_longitude = current_longitude,
                trip_location_id = trip_location_id,
                current_distance_time=current_distance_time,
                current_distance_time_unit=current_distance_time_unit,
                trip_status=trip_status,
                current_location_title=current_location_title,
                current_location_address=current_location_address,
            )
            updateDriverLocationResponse.postValue(Response.Loading(null))
            updateDriverLocationResponse.postValue(response)
        }
    }
    fun endTrip(
        authorization: String,
        booking_id: String,
        driver_id: String,
        trip_location_id: String,
        distance_covered: String,
        distance_covered_unit: String
    ) {
        viewModelScope.launch {
            var response = myRideRepository.endTrip(
                authorization = authorization,
                booking_id = booking_id,
                driver_id = driver_id,
                trip_location_id = trip_location_id,
                distance_covered=distance_covered,
                distance_covered_unit=distance_covered_unit
            )
            endTripResponse.postValue(Response.Loading(null))
            endTripResponse.postValue(response)
        }
    }


}