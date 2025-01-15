package com.driver.sagardriverapp.uis.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.databinding.ItemTripsBinding
import com.driver.sagardriverapp.model.TripList
import com.driver.sagardriverapp.uis.myRide.MyRideActivity
import com.driver.sagardriverapp.utils.Util.launchActivity

class TripsAdapter(
    private val activity: Activity, val tripList: MutableList<TripList>,
    listener: OnItemClickListener
) :
    RecyclerView.Adapter<TripsAdapter.CommonViewHolder>() {

    private lateinit var binding: ItemTripsBinding
    private var selectListlistener: OnItemClickListener? = null

    init {

        selectListlistener = listener

    }

    interface OnItemClickListener {
        fun onItemClick(id: Int?)
    }

    inner class CommonViewHolder(var binding: ItemTripsBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemTripsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding

        binding.tvBookingId.text =
            activity.getString(R.string.booking_id) + "" + tripList[position].bookingId
        binding.tvTime.text =
            tripList[position].triplocations?.get(0)?.startTime + " " + tripList[position].triplocations?.get(
                0
            )?.startTimeUnit
        if (tripList[position].discountPrice == null) {
            binding.tvPrice.text =
                tripList[position].priceUnit + " " + tripList[position].total_price
        }
        else{
            binding.tvPrice.text =
                tripList[position].priceUnit + " " + tripList[position].price
        }

        binding.tvDate.text =
            tripList[position].triplocations?.get(0)?.startDate
        binding.tvCurrentLocationAddress.text =
            tripList[position].triplocations?.get(0)?.pickupLocation
        binding.tvCurrentLocation.text =
            tripList[position].triplocations?.get(0)?.pickupLocationTitle
        binding.tvDestination.text =
            tripList[position].triplocations?.get(0)?.dropLocationTitle
        binding.tvDesAddress.text =
            tripList[position].triplocations?.get(0)?.dropLocation

        binding.btViewTrip.setOnClickListener {
            val bundle = Bundle()
            bundle?.putString("where", "view_trip")
            bundle?.putString("booking_id", tripList[position].id.toString())

            activity.launchActivity(
                MyRideActivity::class.java, removeAllPrevActivities = false,
                bundle = bundle
            )

        }
        binding.btAccept.setOnClickListener {
            selectListlistener!!.onItemClick(tripList[position].id)

        }


    }

    override fun getItemCount(): Int {
        return tripList.size

    }
}