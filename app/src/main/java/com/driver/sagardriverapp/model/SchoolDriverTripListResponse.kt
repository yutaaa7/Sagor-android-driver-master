package com.driver.sagardriverapp.model

import com.google.gson.annotations.SerializedName

data class SchoolDriverTripListResponse(

	@field:SerializedName("data")
	val data: List<SchoolDriverTripList>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class SchoolDriverTripList(

	@field:SerializedName("driver_id")
	val driverId: Int? = null,

	@field:SerializedName("start_location_address_ar")
	val startLocationAddressAr: String? = null,

	@field:SerializedName("distance")
	val distance: Any? = null,

	@field:SerializedName("end_longitude")
	val endLongitude: String? = null,

	@field:SerializedName("end_location_address_ar")
	val endLocationAddressAr: String? = null,

	@field:SerializedName("start_location_title_ar")
	val startLocationTitleAr: String? = null,

	@field:SerializedName("speed_km_p_h")
	val speedKmPH: String? = null,

	@field:SerializedName("deleted_by")
	val deletedBy: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("end_latitude")
	val endLatitude: String? = null,

	@field:SerializedName("booking_id")
	val bookingId: Any? = null,

	@field:SerializedName("end_location_address")
	val endLocationAddress: String? = null,

	@field:SerializedName("school_id")
	val schoolId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("school")
	val school: School? = null,

	@field:SerializedName("start_latitude")
	val startLatitude: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("bus_id")
	val busId: Int? = null,

	@field:SerializedName("distance_unit")
	val distanceUnit: Any? = null,

	@field:SerializedName("end_location_title_ar")
	val endLocationTitleAr: String? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null,

	@field:SerializedName("end_time_unit")
	val endTimeUnit: Any? = null,

	@field:SerializedName("ride_status")
	val rideStatus: String? = null,

	@field:SerializedName("start_location_address")
	val startLocationAddress: String? = null,

	@field:SerializedName("distance_covered_unit")
	val distanceCoveredUnit: Any? = null,

	@field:SerializedName("start_location_title")
	val startLocationTitle: String? = null,

	@field:SerializedName("end_time")
	val endTime: Any? = null,

	@field:SerializedName("distance_covered")
	val distanceCovered: Any? = null,

	@field:SerializedName("created_by")
	val createdBy: Any? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("expected_end_time")
	val expectedEndTime: String? = null,

	@field:SerializedName("expected_start_time")
	val expectedStartTime: String? = null,

	@field:SerializedName("start_time")
	val startTime: Any? = null,

	@field:SerializedName("start_longitude")
	val startLongitude: String? = null,

	@field:SerializedName("end_location_title")
	val endLocationTitle: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Any? = null,

	@field:SerializedName("start_time_unit")
	val startTimeUnit: Any? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class School(

	@field:SerializedName("name_ar")
	val nameAr: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
