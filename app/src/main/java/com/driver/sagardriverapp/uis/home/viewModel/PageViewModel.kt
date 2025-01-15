package com.driver.sagardriverapp.uis.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driver.sagardriverapp.api.Response
import com.driver.sagardriverapp.model.PagesResponse
import com.driver.sagardriverapp.repository.PageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PageViewModel @Inject constructor(private val pageRepository: PageRepository) :
    ViewModel() {

    var pageResponse = MutableLiveData<Response<PagesResponse>>()

    fun pagesData(
        page: String,
        authorization: String
    ) {
        viewModelScope.launch {
            var response = pageRepository.getPageText(
                page = page,
                authorization = authorization
            )
            pageResponse.postValue(Response.Loading(null))
            pageResponse.postValue(response)
        }
    }


}