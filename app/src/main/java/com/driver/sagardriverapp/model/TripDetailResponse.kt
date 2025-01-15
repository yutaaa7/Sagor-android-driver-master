package com.driver.sagardriverapp.model

import com.google.gson.annotations.SerializedName

data class TripDetailResponse(

	@field:SerializedName("data")
	val data: List<TripDetail>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class TripDetail(

	@field:SerializedName("coupon_code")
	val couponCode: Any? = null,

	@field:SerializedName("total_price")
	val totalPrice: String? = null,

	@field:SerializedName("discount_price")
	val discountPrice: Any? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("booking_id")
	val bookingId: String? = null,

	@field:SerializedName("price_unit")
	val priceUnit: String? = null,

	@field:SerializedName("triplocations")
	val triplocations: List<Triplocations?>? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("vehicle_cat_id")
	val vehicleCatId: Int? = null,

	@field:SerializedName("vehiclecategories")
	val vehiclecategories: VehiclecategoriesHere? = null,

	@field:SerializedName("payment")
	val payment: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("trip_mode")
	val tripMode: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class User(

	@field:SerializedName("country_code")
	val countryCode: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("last_name")
	val lastName: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("avatar")
	val avatar: Any? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null
)

data class Triplocations(

	@field:SerializedName("drop_location_title")
	val dropLocationTitle: String? = null,

	@field:SerializedName("enable_button")
	val enable_button: Boolean? = null,

	@field:SerializedName("pickup_longitude")
	val pickupLongitude: String? = null,

	@field:SerializedName("distance")
	val distance: String? = null,

	@field:SerializedName("trip_status")
	val tripStatus: String? = null,

	@field:SerializedName("pickup_location")
	val pickupLocation: String? = null,

	@field:SerializedName("drop_location")
	val dropLocation: String? = null,

	@field:SerializedName("trip_phase")
	val tripPhase: String? = null,

	@field:SerializedName("pickup_location_title")
	val pickupLocationTitle: String? = null,

	@field:SerializedName("booking_id")
	val bookingId: Int? = null,

	@field:SerializedName("start_time")
	val startTime: String? = null,

	@field:SerializedName("trip_type")
	val tripType: String? = null,

	@field:SerializedName("pickup_latitude")
	val pickupLatitude: String? = null,

	@field:SerializedName("drop_latitude")
	val dropLatitude: String? = null,

	@field:SerializedName("start_time_unit")
	val startTimeUnit: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("distance_unit")
	val distanceUnit: String? = null,

	@field:SerializedName("drop_longitude")
	val dropLongitude: String? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null
)

data class VehiclecategoriesHere(

	@field:SerializedName("image")
	val image: Any? = null,

	@field:SerializedName("per_km_price")
	val perKmPrice: String? = null,

	@field:SerializedName("name_ar")
	val nameAr: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("waiting_price")
	val waitingPrice: String? = null,

	@field:SerializedName("capacity")
	val capacity: Int? = null
)
