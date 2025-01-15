package com.driver.sagardriverapp.model

import com.google.gson.annotations.SerializedName

data class ReportIssueResponse(

	@field:SerializedName("data")
	val data: List<ReportIssue>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class ReportIssue(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name_ar")
	val nameAr: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
