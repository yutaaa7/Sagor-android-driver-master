package com.driver.sagardriverapp.uis.myRide.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.SchoolDriverTripDetailResponse
import com.driver.sagardriverapp.model.UpdateDriverLocationResponse
import com.driver.sagardriverapp.repository.MyRideChildRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRideChildViewModel @Inject constructor(private val myRideChildRepository: MyRideChildRepository) :
    ViewModel() {

    var schoolDriverTripDetailResponse = MutableLiveData<Response<SchoolDriverTripDetailResponse>>()
    var endChildTripResponse = MutableLiveData<Response<CommonResponse>>()
    var updateDriverLocationResponse = MutableLiveData<Response<UpdateDriverLocationResponse>>()


    fun updateSchoolDriverLocation(
        authorization: String,
       // booking_id: String,
        driver_id: String,
        current_latitude: String,
        current_longitude: String,
        trip_location_id: String,
    ) {
        viewModelScope.launch {
            var response = myRideChildRepository.updateSchoolDriverLocation(
                authorization = authorization,
               // booking_id = booking_id,
                driver_id = driver_id,
                current_latitude = current_latitude,
                current_longitude = current_longitude,
                trip_location_id = trip_location_id,
            )
            updateDriverLocationResponse.postValue(Response.Loading(null))
            updateDriverLocationResponse.postValue(response)
        }
    }

    fun tripDetailsForSChoolDriverTrips(

        authorization: String,
        ride_id: String
    ) {
        viewModelScope.launch {
            var response = myRideChildRepository.tripDetailsForSChoolDriverTrips(

                authorization = authorization,
                ride_id = ride_id
            )
            schoolDriverTripDetailResponse.postValue(Response.Loading(null))
            schoolDriverTripDetailResponse.postValue(response)
        }
    }


    fun endChildTrip(

        authorization: String,
        ride_id: String
    ) {
        viewModelScope.launch {
            var response = myRideChildRepository.endChildTrip(

                authorization = authorization,
                ride_id = ride_id
            )
            endChildTripResponse.postValue(Response.Loading(null))
            endChildTripResponse.postValue(response)
        }
    }
}