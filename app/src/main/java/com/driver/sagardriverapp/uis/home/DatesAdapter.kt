package com.driver.sagardriverapp.uis.home

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.databinding.ItemDatesBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatesAdapter(private val activity: Activity,val dates: List<String>,
                   listener: OnItemClickListener) : RecyclerView.Adapter<DatesAdapter.CommonViewHolder>() {

    private lateinit var binding: ItemDatesBinding
    private var selectedPosition = 0 // Initially select the first item
    private var selectListlistener: OnItemClickListener? = null
    init {

        selectListlistener = listener

    }
    interface OnItemClickListener {
        fun onItemClick(id: String)
    }

    inner class CommonViewHolder(var binding: ItemDatesBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemDatesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding

        if (position == selectedPosition)
        {
            binding.llDates.setBackgroundResource(R.drawable.solid_green)
            binding.tvDate.setTextColor(activity.getColor(R.color.white))
            binding.tvMonth.setTextColor(activity.getColor(R.color.white))
        }
        else{
            binding.llDates.background=null
            binding.tvDate.setTextColor(activity.getColor(R.color.theme_blue))
            binding.tvMonth.setTextColor(activity.getColor(R.color.theme_blue))
        }
        val dateString = dates[position]

        val (day, month) = formatDateAndMonth(dateString)
        binding.tvDate.text=day
        binding.tvMonth.text=month

        binding.llDates.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            // Notify adapter to refresh the selected and deselected items
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            selectListlistener!!.onItemClick(dates[position])
        }





    }

    override fun getItemCount(): Int {
        return dates.size

    }
    fun formatDateAndMonth(dateString: String): Pair<String, String> {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val outputMonthFormat = SimpleDateFormat("MMM", Locale.getDefault())

        val date = inputFormat.parse(dateString)

        val day = outputDateFormat.format(date ?: Date())
        val month = outputMonthFormat.format(date ?: Date())

        return Pair(day, month)
    }
}