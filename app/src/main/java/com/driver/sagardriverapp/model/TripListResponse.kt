package com.driver.sagardriverapp.model

import com.google.gson.annotations.SerializedName

data class TripListResponse(

	@field:SerializedName("data")
	val data: List<TripList>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class TriplocationsItem(

	@field:SerializedName("drop_location_title")
	val dropLocationTitle: String? = null,

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

data class TripList(

	@field:SerializedName("coupon_code")
	val couponCode: Any? = null,

	@field:SerializedName("discount_price")
	val discountPrice: Any? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("booking_id")
	val bookingId: String? = null,

	@field:SerializedName("price_unit")
	val priceUnit: String? = null,

	@field:SerializedName("triplocations")
	val triplocations: List<TriplocationsItem?>? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("total_price")
	val total_price: String? = null,

	@field:SerializedName("vehicle_cat_id")
	val vehicleCatId: Int? = null,

	@field:SerializedName("vehiclecategories")
	val vehiclecategories: Vehiclecategories? = null,

	@field:SerializedName("payment")
	val payment: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("trip_mode")
	val tripMode: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Vehiclecategories(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
