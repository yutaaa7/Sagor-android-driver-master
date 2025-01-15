package com.driver.sagardriverapp.uis.home

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.databinding.ItemTripsBinding
import com.driver.sagardriverapp.model.CompletedBooking

class HistoryAdapter(
    private val activity: Activity,
    val historyList: MutableList<CompletedBooking>
) :
    RecyclerView.Adapter<HistoryAdapter.CommonViewHolder>() {


    private lateinit var binding: ItemTripsBinding


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

        binding.tvBookingId.visibility = View.GONE
        binding.tvBookingId.text =
            activity.getString(R.string.booking_id) + "" + historyList.get(position).bookingId
        binding.tvTime.text =
            historyList.get(position).triplocations?.get(0)?.startTime + " " + historyList.get(
                position
            ).triplocations?.get(0)?.startTimeUnit
        binding.tvDate.text = historyList.get(position).triplocations?.get(0)?.startDate
        if (historyList[position].discountPrice == null) {
            binding.tvPrice.text =
                historyList.get(position).priceUnit + " " + historyList.get(position).total_price
        }
        else{
            historyList.get(position).priceUnit + " " + historyList.get(position).price

        }

        binding.tvCurrentLocation.text =
            historyList.get(position).triplocations?.get(0)?.pickupLocationTitle
        binding.tvCurrentLocationAddress.text =
            historyList.get(position).triplocations?.get(0)?.pickupLocation

        binding.tvDestination.text =
            historyList.get(position).triplocations?.get(0)?.dropLocationTitle

        binding.tvDesAddress.text =
            historyList.get(position).triplocations?.get(0)?.dropLocation

        binding.btViewTrip.setBackgroundResource(R.drawable.solid_green_10)
        binding.btViewTrip.text = activity.getString(R.string.start_trip)
        binding.btAccept.text = activity.getString(R.string.view_trip)

        binding.llALlButtons.visibility = View.GONE
        binding.btCompleted.visibility = View.VISIBLE


    }

    override fun getItemCount(): Int {
        return historyList.size

    }
}