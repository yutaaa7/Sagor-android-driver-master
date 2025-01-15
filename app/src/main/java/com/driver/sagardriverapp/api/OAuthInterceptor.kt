package com.driver.sagardriverapp.api


import com.driver.sagardriverapp.utils.SharedPref
import okhttp3.Interceptor

class OAuthInterceptor : Interceptor {

    // private val tokenType: String = "Basic"
    private val tokenType: String = "Bearer"
    private var accessToken: String? = null

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
        request.apply {
            addHeader("Content-Type", "application/json")
            //addHeader("appsource", "android")
            //addHeader("appversion", BuildConfig.VERSION_NAME)
        }
        accessToken = SharedPref.readString(SharedPref.ACCESS_TOKEN)
        //accessToken = "dXNlcjphN2I0Mjg5Ni05MmYxLTQ3NjEtYTUwYy05MDcxNjZjNjdkNTU="
        if (accessToken != null && accessToken?.isNotEmpty() == true) {
            request.header("Authorization", "$tokenType $accessToken")
        }
        return chain.proceed(request.build())
    }
}