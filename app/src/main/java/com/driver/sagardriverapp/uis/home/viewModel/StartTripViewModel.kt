package com.driver.sagardriverapp.uis.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.SchoolDriverTripDetailResponse
import com.driver.sagardriverapp.repository.StartTripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartTripViewModel @Inject constructor(private val startTripRepository: StartTripRepository) :
    ViewModel() {

    var schoolDriverTripDetailResponse = MutableLiveData<Response<SchoolDriverTripDetailResponse>>()
    var startChildTripResponse = MutableLiveData<Response<CommonResponse>>()

    fun tripDetailsForSChoolDriverTrips(

        authorization: String,
        ride_id: String
    ) {
        viewModelScope.launch {
            var response = startTripRepository.tripDetailsForSChoolDriverTrips(

                authorization = authorization,
                ride_id = ride_id
            )
            schoolDriverTripDetailResponse.postValue(Response.Loading(null))
            schoolDriverTripDetailResponse.postValue(response)
        }
    }

    fun startChildTrips(

        authorization: String,
        ride_id: String
    ) {
        viewModelScope.launch {
            var response = startTripRepository.startChildTrips(

                authorization = authorization,
                ride_id = ride_id
            )
            startChildTripResponse.postValue(Response.Loading(null))
            startChildTripResponse.postValue(response)
        }
    }


}