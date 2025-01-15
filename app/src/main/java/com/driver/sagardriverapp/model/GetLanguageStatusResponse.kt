package com.driver.sagardriverapp.model

import com.google.gson.annotations.SerializedName

data class GetLanguageStatusResponse(

	@field:SerializedName("data")
	val data: GetLanguageStatus? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class GetLanguageStatus(

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("lang_id")
	val langId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
