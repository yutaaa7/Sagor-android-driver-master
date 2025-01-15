package com.driver.sagardriverapp.uis.myRide

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.driver.sagardriverapp.databinding.ItemReportAnIssueReasonsBinding
import com.driver.sagardriverapp.model.ReportIssue
import com.driver.sagardriverapp.utils.SharedPref

class ReportAnIssueReasonsAdapter(
    private val activity: Activity,
    val reportList: MutableList<ReportIssue>,
    private val clickListener: onItemClickListenre
) :
    RecyclerView.Adapter<ReportAnIssueReasonsAdapter.CommonViewHolder>() {
    private lateinit var itemClickListener: onItemClickListenre

    private var selectedItemPosition: Int = RecyclerView.NO_POSITION

    private lateinit var binding: ItemReportAnIssueReasonsBinding
    interface onItemClickListenre {
        fun onClick(id: Int?)
    }

    init {
        itemClickListener = clickListener
    }

    inner class CommonViewHolder(var binding: ItemReportAnIssueReasonsBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemReportAnIssueReasonsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
            binding.tvReason.text = reportList[position].name
        }
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
            binding.tvReason.text = reportList[position].nameAr

        }
        // Change background or text color based on selection
        if (position == selectedItemPosition) {
            binding.tvReason.isChecked = true
        } else {
            binding.tvReason.isChecked = false

        }
        binding.tvReason.setOnClickListener {
            // Handle item selection
            val previousPosition = selectedItemPosition
            selectedItemPosition = position

            // Notify changes for updating UI
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedItemPosition)
            // binding.ivCancel.setImageResource(R.drawable.dot_circle)
           itemClickListener.onClick(reportList[position].id)
        }


    }

    override fun getItemCount(): Int {
        return reportList.size

    }
}