package com.driver.sagardriverapp.uis.home

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.driver.sagardriverapp.databinding.ItemTripDetailsCheckoutBinding

class TripDetailsWithTypeAdapter (private val activity: Activity) : RecyclerView.Adapter<TripDetailsWithTypeAdapter.CommonViewHolder>() {

    private lateinit var binding: ItemTripDetailsCheckoutBinding


    inner class CommonViewHolder(var binding: ItemTripDetailsCheckoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemTripDetailsCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding






    }

    override fun getItemCount(): Int {
        return 3

    }
}