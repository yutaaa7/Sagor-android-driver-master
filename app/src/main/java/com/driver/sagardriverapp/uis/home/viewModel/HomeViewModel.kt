package com.driver.sagardriverapp.uis.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(private val homeRepository: HomeRepository) :
    ViewModel() {

    var commonResponse = MutableLiveData<Response<CommonResponse>>()

    fun updateFcmToken(
        authorization: String,
        email: String,
        device_type:String,
        device_token:String,
        type:String,
        device_version:String,
        fcm_token:String,
    ) {
        viewModelScope.launch {
            var response = homeRepository.updateFcmToken(
                authorization = authorization,
                email=email,
                device_type=device_type,
                device_token=device_token,
                type=type,
                device_version=device_version,
                fcm_token=fcm_token,
            )
            commonResponse.postValue(Response.Loading(null))
            commonResponse.postValue(response)
        }
    }


}