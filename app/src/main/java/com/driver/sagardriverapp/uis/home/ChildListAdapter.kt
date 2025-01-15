package com.driver.sagardriverapp.uis.home

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.driver.sagardriverapp.R
import com.driver.sagardriverapp.databinding.ItemChildBinding
import com.driver.sagardriverapp.model.ChildrensdetailItem
import com.driver.sagardriverapp.utils.AppConstants
import com.driver.sagardriverapp.utils.SharedPref

class ChildListAdapter(private val activity: Activity, val childList: MutableList<ChildrensdetailItem>,
                       listener: OnItemClickListener
) :
    RecyclerView.Adapter<ChildListAdapter.CommonViewHolder>() {

    private lateinit var binding: ItemChildBinding
    private var selectListlistener: OnItemClickListener? = null
    init {

        selectListlistener = listener

    }
    interface OnItemClickListener {
        fun onItemClick(id: Int?)
        fun onBoardedClick(id: Int?)
    }

    inner class CommonViewHolder(var binding: ItemChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding

       // child status value

        //1 => not_present, 2 => boarded, 3 => dropped
        Glide.with(activity)
            .load(AppConstants.BASE_URL_IMAGE+""+childList[position].user?.get(0)?.avatar)
            .placeholder(R.drawable.mask)
            .into(binding.ivDriverImage1)
        binding.tvChildName.text = childList[position].user?.get(0)?.name
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {

            binding.tvGrade.text = childList[position].user?.get(0)?.grades?.name
        }
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
            binding.tvGrade.text = childList[position].user?.get(0)?.grades?.nameAr

        }

        if (childList[position].status.equals("1"))
        {
            binding.btPresent.visibility=View.VISIBLE
            binding.btBoarded.visibility=View.GONE
            binding.btDropped.visibility=View.GONE
        }
        if (childList[position].status.equals("2"))
        {
            binding.btPresent.visibility=View.GONE
            binding.btBoarded.visibility=View.VISIBLE
            binding.btDropped.visibility=View.GONE
        }
        if (childList[position].status.equals("3"))
        {
            binding.btPresent.visibility=View.GONE
            binding.btBoarded.visibility=View.GONE
            binding.btDropped.visibility=View.VISIBLE
        }

        binding.btPresent.setOnClickListener {
            /*binding.btPresent.visibility= View.GONE
            binding.btBoarded.visibility= View.VISIBLE
            binding.btDropped.visibility= View.GONE*/
            selectListlistener!!.onItemClick(childList[position].id )
        }
        binding.btBoarded.setOnClickListener {
           /* binding.btPresent.visibility= View.GONE
            binding.btBoarded.visibility= View.GONE
            binding.btDropped.visibility= View.VISIBLE*/
            selectListlistener!!.onBoardedClick(childList[position].id )
        }

    }

    override fun getItemCount(): Int {
        return childList.size

    }
}