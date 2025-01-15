package com.driver.sagardriverapp.uis.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.GetLanguageStatusResponse
import com.driver.sagardriverapp.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) :
    ViewModel() {

    var logoutResponse = MutableLiveData<Response<CommonResponse>>()
    var acceptTripResponse = MutableLiveData<Response<CommonResponse>>()
    var languageUpdateResponse = MutableLiveData<Response<CommonResponse>>()
    var getLanguageStatusResponse = MutableLiveData<Response<GetLanguageStatusResponse>>()


    fun logout(
        auth: String,

    ) {
        viewModelScope.launch {
            var response = profileRepository.logout(
              auth=auth
            )
            logoutResponse.postValue(Response.Loading(null))
            logoutResponse.postValue(response)
        }
    }

    fun languageUpdate(
        user_id: String,
        device_token: String,
        lang_id: String,
        authorization: String
    ) {
        viewModelScope.launch {
            var response = profileRepository.languageUpdate(
                user_id = user_id,
                device_token = device_token,
                lang_id = lang_id,
                authorization = authorization
            )
            languageUpdateResponse.postValue(Response.Loading(null))
            languageUpdateResponse.postValue(response)
        }
    }


    fun getLanguageStatus(
        user_id: String,
        device_token: String,
        authorization: String
    ) {
        viewModelScope.launch {
            var response = profileRepository.getLanguageStatus(
                user_id = user_id,
                device_token = device_token,
                authorization = authorization
            )
            getLanguageStatusResponse.postValue(Response.Loading(null))
            getLanguageStatusResponse.postValue(response)
        }
    }


}