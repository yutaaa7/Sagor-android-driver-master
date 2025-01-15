package com.driver.sagardriverapp.api

import com.driver.sagardriverapp.model.AcceptBookingsListResponse
import com.driver.sagardriverapp.model.AcceptRidelResponse
import com.driver.sagardriverapp.model.ChildListResponse
import com.driver.sagardriverapp.model.CommonResponse
import com.driver.sagardriverapp.model.CompletedBookingResponse
import com.driver.sagardriverapp.model.GetLanguageStatusResponse
import com.driver.sagardriverapp.model.LoginResponse
import com.driver.sagardriverapp.model.PagesResponse
import com.driver.sagardriverapp.model.ReportIssueResponse
import com.driver.sagardriverapp.model.SchoolDriverTripDetailResponse
import com.driver.sagardriverapp.model.SchoolDriverTripListResponse
import com.driver.sagardriverapp.model.StartTripResponse
import com.driver.sagardriverapp.model.TripDetailResponse
import com.driver.sagardriverapp.model.TripListResponse
import com.driver.sagardriverapp.model.UpdateDriverLocationResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST(LOGIN)
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
        @Field("type") type: String,
        @Field("device_version") device_version: String
    ): LoginResponse

    @FormUrlEncoded
    @POST(TRIP_LIST)
    suspend fun tripList(
        @Header("Authorization") authorization: String,
        @Field("vehicle_cat_id") vehicle_cat_id: String,
        @Field("booking_status") booking_status: String,
        @Field("start_date") start_date: String
    ): TripListResponse

    @FormUrlEncoded
    @POST(SCHOOL_DRIVER_TRIPS)
    suspend fun schoolDriverTripList(
        @Header("Authorization") authorization: String,
        @Field("driver_id") driver_id: String,
        @Field("school_id") school_id: String,
        @Field("date") date: String,
        @Field("bus_id") bus_id: String
    ): SchoolDriverTripListResponse

    @FormUrlEncoded
    @POST(ACCEPTED_TRIP_LIST)
    suspend fun AcceptedTripsList(
        @Header("Authorization") authorization: String,
        @Field("vehicle_cat_id") vehicle_cat_id: String,
        @Field("booking_status") booking_status: String,
        @Field("driver_id") driver_id: String
    ): AcceptBookingsListResponse

    @FormUrlEncoded
    @POST(TRIP_DETAIL)
    suspend fun tripDetail(
        @Header("Authorization") authorization: String,
        @Field("booking_id") booking_id: String
    ): TripDetailResponse

    @FormUrlEncoded
    @POST(SCHOOL_DRIVER_TRIPS_DETAIL)
    suspend fun tripDetailOfSchoolDriverTrip(
        @Header("Authorization") authorization: String,
        @Field("ride_id") ride_id: String
    ): SchoolDriverTripDetailResponse

    @FormUrlEncoded
    @POST(CHILD_LIST)
    suspend fun ChildList(
        @Header("Authorization") authorization: String,
        @Field("ride_id") ride_id: String,
        @Field("search_keyword") search_keyword: String
    ): ChildListResponse

    @FormUrlEncoded
    @POST(ACCEPT_TRIP)
    suspend fun acceptTrip(
        @Header("Authorization") authorization: String,
        @Field("booking_id") booking_id: String,
        @Field("driver_id") driver_id: String
    ): AcceptRidelResponse

    @FormUrlEncoded
    @POST(START_TRIP)
    suspend fun startTrip(
        @Header("Authorization") authorization: String,
        @Field("booking_id") booking_id: String,
        @Field("driver_id") driver_id: String,
        @Field("trip_location_id") trip_location_id: String,

    ): StartTripResponse

    @FormUrlEncoded
    @POST(START_CHILD_TRIP)
    suspend fun startChildTrip(
        @Header("Authorization") authorization: String,
        @Field("ride_id") ride_id: String,

        ): CommonResponse


    @FormUrlEncoded
    @POST(GO_TRIP)
    suspend fun goTrip(
        @Header("Authorization") authorization: String,
        @Field("booking_id") booking_id: String,
        @Field("driver_id") driver_id: String,
        @Field("trip_location_id") trip_location_id: String,

        ): StartTripResponse

    @FormUrlEncoded
    @POST(UPDATE_FCM_TOKEN)
    suspend fun updateFcmToken(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
        @Field("type") type: String,
        @Field("device_version") device_version: String,
        @Field("fcm_token") fcm_token: String,

        ): CommonResponse

    @FormUrlEncoded
    @POST(END_CHILD_TRIP)
    suspend fun endChildTrip(
        @Header("Authorization") authorization: String,
        @Field("ride_id") ride_id: String,

        ): CommonResponse

    @FormUrlEncoded
    @POST(END_TRIP)
    suspend fun endTrip(
        @Header("Authorization") authorization: String,
        @Field("booking_id") booking_id: String,
        @Field("driver_id") driver_id: String,
        @Field("trip_location_id") trip_location_id: String,
        @Field("distance_covered") distance_covered: String,
        @Field("distance_covered_unit") distance_covered_unit: String,
    ): StartTripResponse

    @POST(LOGOUT)
    suspend fun logout(
        @Header("Authorization") authorization: String
    ): CommonResponse

    @FormUrlEncoded
    @POST(LANGUAGE_UPDATE)
    suspend fun languageUpdate(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
        @Field("lang_id") lang_id: String,
    ): CommonResponse

    @FormUrlEncoded
    @POST(HISTORY_NORMAL_DRIVER)
    suspend fun historyOfNormalTrip(
        @Header("Authorization") authorization: String,
        @Field("vehicle_cat_id") vehicle_cat_id: String,
        @Field("booking_status") booking_status: String,
        @Field("driver_id") driver_id: String
    ): CompletedBookingResponse

    @FormUrlEncoded
    @POST(GET_LANGUAGE_STATUS)
    suspend fun getLanguageStatus(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,

        ): GetLanguageStatusResponse

    @FormUrlEncoded
    @POST(CHILD_BOARD)
    suspend fun childBoarded(
        @Header("Authorization") authorization: String,
        @Field("child_id") child_id: String,
        @Field("pickup_time") pickup_time: String,
        @Field("pickup_time_unit") pickup_time_unit: String,
        @Field("pickup_latitude") pickup_latitude: String,
        @Field("pickup_longitude") pickup_longitude: String,
        @Field("pickup_location_title") pickup_location_title: String,
        @Field("pickup_location_address") pickup_location_address: String,
    ): CommonResponse

    @FormUrlEncoded
    @POST(CHILD_DROPPED)
    suspend fun childDrpped(
        @Header("Authorization") authorization: String,
        @Field("child_id") child_id: String
    ): CommonResponse

    @FormUrlEncoded
    @POST(REPORT_ISSUE_SUBMIT)
    suspend fun reportAnIssue(
        @Header("Authorization") authorization: String,
        @Field("driver_id") driver_id: String,
        @Field("trip_id") trip_id: String,
        @Field("reason_id") reason_id: String,
        @Field("comment") comment: String,
    ): CommonResponse

    @FormUrlEncoded
    @POST(UPDATE_DRIVER_LOCATION)
    suspend fun updateDriverLocation(
        @Header("Authorization") authorization: String,
        @Field("booking_id") booking_id: String,
        @Field("driver_id") driver_id: String,
        @Field("current_latitude") current_latitude: String,
        @Field("current_longitude") current_longitude: String,
        @Field("trip_location_id") trip_location_id: String,
        @Field("current_distance_time") current_distance_time: String,
        @Field("current_distance_time_unit") current_distance_time_unit: String,
        @Field("trip_status") trip_status: String,
        @Field("current_location_title") current_location_title: String,
        @Field("current_location_address") current_location_address: String,
    ): UpdateDriverLocationResponse

    @FormUrlEncoded
    @POST(UPDATE_SCHOOL_DRIVER_LOCATION)
    suspend fun updateSchoolDriverLocation(
        @Header("Authorization") authorization: String,
       // @Field("booking_id") booking_id: String,
        @Field("driver_id") driver_id: String,
        @Field("current_latitude") current_latitude: String,
        @Field("current_longitude") current_longitude: String,
        @Field("trip_id") trip_location_id: String,
    ): UpdateDriverLocationResponse

    @POST(REPORT_ISSUE)
    suspend fun ReportIssueReasons(
        @Header("Authorization") authorization: String
    ): ReportIssueResponse

    @GET(GET_PAGE_TEXT)
    suspend fun getPageText(
        @Path("page") page: String,
        @Header("Authorization") authorization: String
    ): PagesResponse
}