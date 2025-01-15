package com.driver.sagardriverapp.uis.home

import android.app.Activity
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.databinding.ItemChildAllTripsBinding
import com.driver.sagardriverapp.model.SchoolDriverTripList
import com.driver.sagardriverapp.utils.SharedPref
import com.driver.sagardriverapp.utils.Util.launchActivity

class ChildAllTripsAdapter(
    private val activity: Activity,
    val childTripList: MutableList<SchoolDriverTripList>
) :
    RecyclerView.Adapter<ChildAllTripsAdapter.CommonViewHolder>() {


    private lateinit var binding: ItemChildAllTripsBinding


    inner class CommonViewHolder(var binding: ItemChildAllTripsBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemChildAllTripsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding
        //1 => upcoming , 2 => started, 3 => completed
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {

            binding.tvSchoolName.text = childTripList[position].school?.name
            binding.tvCurrentLocation.text = childTripList[position].startLocationTitle
            binding.tvCurrAddress.text = childTripList[position].startLocationAddress
            binding.tvDestination.text = childTripList[position].endLocationTitle
            binding.tvDesAdd.text = childTripList[position].endLocationAddress
        }

        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
            binding.tvSchoolName.text = childTripList[position].school?.nameAr
            binding.tvCurrentLocation.text = childTripList[position].startLocationTitleAr
            binding.tvCurrAddress.text = childTripList[position].startLocationAddressAr
            binding.tvDestination.text = childTripList[position].endLocationTitleAr
            binding.tvDesAdd.text = childTripList[position].endLocationAddressAr
        }
        binding.tvSchoolId.text =
            activity.getString(R.string.school_id) + childTripList[position].schoolId.toString()

        binding.tvTime.text = childTripList[position].expectedStartTime
        if (childTripList[position].rideStatus.equals("2")) {
            binding.tvTime.setTextColor(activity.getColor(R.color.green))
        }
        if (childTripList[position].rideStatus.equals("1")) {
            binding.tvTime.setTextColor(activity.getColor(R.color.theme_blue))

        }
        if (childTripList[position].rideStatus.equals("3")) {
            binding.tvTime.setTextColor(activity.getColor(R.color.red))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Ensure API 31+
                binding.cardChild.setRenderEffect(
                    RenderEffect.createBlurEffect(5f, 5f, Shader.TileMode.CLAMP)
                )
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Ensure API 31+

                binding.cardChild.setRenderEffect(
                    null
                )
            }
        }


        binding.cardChild.setOnClickListener {
            if (!childTripList[position].rideStatus.equals("3")) {
                val bundle = Bundle()
                bundle?.putString("ride_id", childTripList[position].id.toString())
                activity.launchActivity(
                    StartTripActivity::class.java,
                    removeAllPrevActivities = false, bundle = bundle
                )

            }

        }


    }

    override fun getItemCount(): Int {
        return childTripList.size

    }
}