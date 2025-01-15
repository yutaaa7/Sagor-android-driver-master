package com.driver.sagardriverapp.uis.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.LoginResponse
import com.driver.sagardriverapp.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {

    var loginResponse = MutableLiveData<Response<LoginResponse>>()
    var acceptTripResponse = MutableLiveData<Response<CommonResponse>>()
    var languageUpdateResponse = MutableLiveData<Response<CommonResponse>>()


    fun login(
        email: String,
        password: String,
        device_type: String,
        device_token: String,
        type: String,
        device_version: String,
    ) {
        viewModelScope.launch {
            var response = loginRepository.login(
                email = email,
                password = password,
                device_type = device_type,
                device_token = device_token,
                type = type,
                device_version=device_version
            )
            loginResponse.postValue(Response.Loading(null))
            loginResponse.postValue(response)
        }
    }

    fun languageUpdate(
        user_id: String,
        device_token: String,
        lang_id: String,
        authorization: String
    ) {
        viewModelScope.launch {
            var response = loginRepository.languageUpdate(
                user_id = user_id,
                device_token = device_token,
                lang_id = lang_id,
                authorization = authorization
            )
            languageUpdateResponse.postValue(Response.Loading(null))
            languageUpdateResponse.postValue(response)
        }
    }




}