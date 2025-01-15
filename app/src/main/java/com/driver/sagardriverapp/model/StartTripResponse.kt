package com.driver.sagardriverapp.model

import com.google.gson.annotations.SerializedName

data class StartTripResponse(

	@field:SerializedName("data")
	val data: List<StartTrip>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class VehiclecategoriesStartTrip(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class StartTrip(

	@field:SerializedName("coupon_code")
	val couponCode: Any? = null,

	@field:SerializedName("total_price")
	val totalPrice: String? = null,

	@field:SerializedName("discount_price")
	val discountPrice: Any? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("vehicle")
	val vehicle: VehicleStartTrip? = null,

	@field:SerializedName("booking_id")
	val bookingId: String? = null,

	@field:SerializedName("price_unit")
	val priceUnit: String? = null,

	@field:SerializedName("triplocations")
	val triplocations: List<TriplocationsStartTrip?>? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("vehicle_cat_id")
	val vehicleCatId: Int? = null,

	@field:SerializedName("vehiclecategories")
	val vehiclecategories: VehiclecategoriesStartTrip? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("vehicle_id")
	val vehicleId: Int? = null,

	@field:SerializedName("user")
	val user: UserStartTrip? = null,

	@field:SerializedName("trip_mode")
	val tripMode: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class UserStartTrip(

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

data class VehicleStartTrip(

	@field:SerializedName("vehicle_number")
	val vehicleNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class TriplocationsStartTrip(

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
