package com.driver.sagardriverapp.uis.myRide

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.databinding.ItemTripsBinding
import com.driver.sagardriverapp.model.AcceptBookingsList
import com.driver.sagardriverapp.utils.Util
import com.driver.sagardriverapp.utils.Util.launchActivity

private const val s = "ongoing"

class AcceptedTripsAdapter(
    private val activity: Activity,
    val tripList: MutableList<AcceptBookingsList>
) :
    RecyclerView.Adapter<AcceptedTripsAdapter.CommonViewHolder>() {


    private lateinit var binding: ItemTripsBinding


    inner class CommonViewHolder(var binding: ItemTripsBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemTripsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding
        binding.tvBookingId.text =
            activity.getString(R.string.booking_id) + "" + tripList[position].bookingId
        if (tripList[position].discountPrice == null) {
            binding.tvPrice.text =
                tripList[position].priceUnit + " " + tripList[position].total_price
        }
        else{
            tripList[position].priceUnit + " " + tripList[position].price

        }

        binding.tvBookingId.visibility = View.GONE
        if (tripList.get(position).tripMode.equals("0")) {
            binding.tvTime.text =
                tripList[position].triplocations?.get(0)?.startTime + " " + tripList[position].triplocations?.get(
                    0
                )?.startTimeUnit
            binding.tvDate.text =
                tripList[position].triplocations?.get(0)?.startDate
            binding.tvCurrentLocationAddress.text =
                tripList[position].triplocations?.get(0)?.pickupLocation
            binding.tvDesAddress.text =
                tripList[position].triplocations?.get(0)?.dropLocation
            binding.tvCurrentLocation.text =
                tripList[position].triplocations?.get(0)?.pickupLocationTitle
            binding.tvDestination.text = tripList[position].triplocations?.get(0)?.dropLocationTitle

             var shouldEnableStart=Util.shouldEnableStartRideButton(
                 pickupDate = tripList[position].triplocations?.get(0)?.startDate.toString(),
                 pickupTime = tripList[position].triplocations?.get(0)?.startTime.toString(),
                 timeUnit = tripList[position].triplocations?.get(0)?.startTimeUnit.toString()
             )
          /*  var shouldEnableStart=Util.shouldEnableStartRideButton(
                pickupDate = "2024-10-29",
                pickupTime = "01:00:00",
                timeUnit = "pm",
            )*/
            if (tripList[position].triplocations?.get(0)?.enable_button == true)
            {
                binding.btViewTrip.setBackgroundResource(R.drawable.solid_green_10)
                binding.btViewTrip.isEnabled=true
            }
            else{
                binding.btViewTrip.setBackgroundResource(R.drawable.solid_gray)
                binding.btViewTrip.isEnabled=false

            }/*if (shouldEnableStart)
            {
                binding.btViewTrip.setBackgroundResource(R.drawable.solid_green_10)
                binding.btViewTrip.isEnabled=true
            }
            else{
                binding.btViewTrip.setBackgroundResource(R.drawable.solid_gray)
                binding.btViewTrip.isEnabled=false

            }*/
            if (tripList[position].triplocations?.get(0)?.tripStatus.equals("ongoing")
                || tripList[position].triplocations?.get(0)?.tripStatus.equals("ontheway")
            ) {

                binding.btViewTrip.visibility = View.GONE
            } else {
                binding.btViewTrip.visibility = View.VISIBLE

            }
        }
        if (tripList.get(position).tripMode.equals("1")) {
            if (tripList[position].triplocations?.get(0)?.tripStatus.equals("completed"))
            {
                binding.tvTime.text =
                    tripList[position].triplocations?.get(1)?.startTime + " " + tripList[position].triplocations?.get(
                        1
                    )?.startTimeUnit
                binding.tvDate.text =
                    tripList[position].triplocations?.get(1)?.startDate
                binding.tvCurrentLocationAddress.text =
                    tripList[position].triplocations?.get(1)?.pickupLocation
                binding.tvDesAddress.text =
                    tripList[position].triplocations?.get(1)?.dropLocation
                binding.tvCurrentLocation.text =
                    tripList[position].triplocations?.get(1)?.pickupLocationTitle
                binding.tvDestination.text = tripList[position].triplocations?.get(1)?.dropLocationTitle


                var shouldEnableStart=Util.shouldEnableStartRideButton(
                    pickupDate = tripList[position].triplocations?.get(1)?.startDate.toString(),
                    pickupTime = tripList[position].triplocations?.get(1)?.startTime.toString(),
                    timeUnit = tripList[position].triplocations?.get(1)?.startTimeUnit.toString()
                )
               /* var shouldEnableStart=Util.shouldEnableStartRideButton(
                    pickupDate = "2024-10-29",
                    pickupTime = "03:30:00",
                    timeUnit = "pm",
                )*/
                /*if (shouldEnableStart)
                {
                    binding.btViewTrip.setBackgroundResource(R.drawable.solid_green_10)
                    binding.btViewTrip.isEnabled=true

                }
                else{
                    binding.btViewTrip.setBackgroundResource(R.drawable.solid_gray)
                    binding.btViewTrip.isEnabled=false
                }*/
                if (tripList[position].triplocations?.get(1)?.enable_button == true)
                {
                    binding.btViewTrip.setBackgroundResource(R.drawable.solid_green_10)
                    binding.btViewTrip.isEnabled=true
                }
                else{
                    binding.btViewTrip.setBackgroundResource(R.drawable.solid_gray)
                    binding.btViewTrip.isEnabled=false

                }

                if (tripList[position].triplocations?.get(1)?.tripStatus.equals("ongoing")
                    || tripList[position].triplocations?.get(1)?.tripStatus.equals("ontheway")
                ) {

                    binding.btViewTrip.visibility = View.GONE

                } else {
                    binding.btViewTrip.visibility = View.VISIBLE

                }
            }
            else{
                binding.tvTime.text =
                    tripList[position].triplocations?.get(0)?.startTime + " " + tripList[position].triplocations?.get(
                        0
                    )?.startTimeUnit
                binding.tvDate.text =
                    tripList[position].triplocations?.get(0)?.startDate
                binding.tvCurrentLocationAddress.text =
                    tripList[position].triplocations?.get(0)?.pickupLocation
                binding.tvDesAddress.text =
                    tripList[position].triplocations?.get(0)?.dropLocation
                binding.tvCurrentLocation.text =
                    tripList[position].triplocations?.get(0)?.pickupLocationTitle
                binding.tvDestination.text = tripList[position].triplocations?.get(0)?.dropLocationTitle

                var shouldEnableStart=Util.shouldEnableStartRideButton(
                    pickupDate = tripList[position].triplocations?.get(0)?.startDate.toString(),
                    pickupTime = tripList[position].triplocations?.get(0)?.startTime.toString(),
                    timeUnit = tripList[position].triplocations?.get(0)?.startTimeUnit.toString()
                )
               /* var shouldEnableStart=Util.shouldEnableStartRideButton(
                    pickupDate = "2024-10-29",
                    pickupTime = "03:30:00",
                    timeUnit = "pm",
                )*/

               /* if (shouldEnableStart)
                {
                    binding.btViewTrip.setBackgroundResource(R.drawable.solid_green_10)
                    binding.btViewTrip.isEnabled=true

                }
                else{
                    binding.btViewTrip.setBackgroundResource(R.drawable.solid_gray)
                    binding.btViewTrip.isEnabled=false


                }*/
                if (tripList[position].triplocations?.get(0)?.enable_button == true)
                {
                    binding.btViewTrip.setBackgroundResource(R.drawable.solid_green_10)
                    binding.btViewTrip.isEnabled=true
                }
                else{
                    binding.btViewTrip.setBackgroundResource(R.drawable.solid_gray)
                    binding.btViewTrip.isEnabled=false

                }
                if (tripList[position].triplocations?.get(0)?.tripStatus.equals("ongoing")
                    || tripList[position].triplocations?.get(0)?.tripStatus.equals("ontheway")
                ) {

                    binding.btViewTrip.visibility = View.GONE
                } else {
                    binding.btViewTrip.visibility = View.VISIBLE

                }
            }

        }


      //  binding.btViewTrip.setBackgroundResource(R.drawable.solid_green_10)
        binding.btViewTrip.text = activity.getString(R.string.start_trip)
        binding.btAccept.text = activity.getString(R.string.view_trip)

        binding.btViewTrip.setOnClickListener {
            val bundle = Bundle()
            bundle?.putString("where", "accept_trip")
            bundle?.putString("booking_id", tripList[position].id.toString())
            activity.launchActivity(
                MyRideActivity::class.java, removeAllPrevActivities = true,
                bundle = bundle
            )

        }
        binding.btAccept.setOnClickListener {
            val bundle = Bundle()
            bundle?.putString("where", "accept_trip")
            bundle?.putString("booking_id", tripList[position].id.toString())
            activity.launchActivity(
                MyRideActivity::class.java, removeAllPrevActivities = true,
                bundle = bundle
            )
        }
    }

    override fun getItemCount(): Int {
        return tripList.size

    }
}