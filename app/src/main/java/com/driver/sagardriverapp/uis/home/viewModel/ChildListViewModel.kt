package com.driver.sagardriverapp.uis.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.model.ChildListResponse
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.repository.ChildListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChildListViewModel @Inject constructor(private val childListRepository: ChildListRepository) :
    ViewModel() {

    var childListResponse = MutableLiveData<Response<ChildListResponse>>()
    var childBoardResponse = MutableLiveData<Response<CommonResponse>>()

    fun childList(
        authorization: String,
        ride_id: String,
        search_keyword: String
    ) {
        viewModelScope.launch {
            var response = childListRepository.childList(

                authorization = authorization,
                ride_id = ride_id,
                search_keyword = search_keyword
            )
            childListResponse.postValue(Response.Loading(null))
            childListResponse.postValue(response)
        }
    }

    fun childBoard(
        authorization: String,
        child_id: String,
        pickup_time: String,
        pickup_time_unit: String,
        pickup_latitude: String,
        pickup_longitude: String,
        pickup_location_title: String,
        pickup_location_address: String,
    ) {
        viewModelScope.launch {
            var response = childListRepository.childBoard(
                authorization = authorization,
                child_id = child_id,
                pickup_time = pickup_time,
                pickup_time_unit = pickup_time_unit,
                pickup_latitude = pickup_latitude,
                pickup_longitude = pickup_longitude,
                pickup_location_title = pickup_location_title,
                pickup_location_address = pickup_location_address
            )
            childBoardResponse.postValue(Response.Loading(null))
            childBoardResponse.postValue(response)
        }
    }

    fun childDropped(
        authorization: String,
        child_id: String
    ) {
        viewModelScope.launch {
            var response = childListRepository.childDropped(
                authorization = authorization,
                child_id = child_id
            )
            childBoardResponse.postValue(Response.Loading(null))
            childBoardResponse.postValue(response)
        }
    }


}