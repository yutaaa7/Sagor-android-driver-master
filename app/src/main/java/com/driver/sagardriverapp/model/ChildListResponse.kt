package com.driver.sagardriverapp.model

import com.google.gson.annotations.SerializedName

data class ChildListResponse(

	@field:SerializedName("data")
	val data: ChildList? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class ChildrensdetailItem(

	@field:SerializedName("trip_id")
	val tripId: Int? = null,

	@field:SerializedName("student_id")
	val studentId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user")
	val user: List<UserItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class VehicleChildList(

	@field:SerializedName("vehicle_number")
	val vehicleNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class UserItem(

	@field:SerializedName("grade_id")
	val gradeId: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("avatar")
	val avatar: Any? = null,

	@field:SerializedName("grades")
	val grades: GradesChildList? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null
)

data class GradesChildList(

	@field:SerializedName("name_ar")
	val nameAr: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class SchoolChildList(

	@field:SerializedName("name_ar")
	val nameAr: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class ChildList(

	@field:SerializedName("childrensdetail")
	val childrensdetail: List<ChildrensdetailItem>? = mutableListOf(),

	@field:SerializedName("schooldetail")
	val schooldetail: List<SchooldetailItem?>? = null
)

data class SchooldetailItem(

	@field:SerializedName("school_id")
	val schoolId: Int? = null,

	@field:SerializedName("school")
	val school: SchoolChildList? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("bus_id")
	val busId: Int? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("vehicle")
	val vehicle: VehicleChildList? = null
)

data class Grades(

	@field:SerializedName("name_ar")
	val nameAr: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
