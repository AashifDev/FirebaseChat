package com.example.firebasechat.ui.mainUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasechat.databinding.UserStatusAllBinding
import com.example.firebasechat.databinding.UserStatusSingleItemBinding
import com.example.firebasechat.model.Status
import com.example.firebasechat.ui.mainUi.fragment.HomeFragment
import com.example.firebasechat.ui.mainUi.fragment.MyStatusFragment
import com.example.firebasechat.ui.mainUi.fragment.ViewStatusFragment
import com.example.firebasechat.utils.MyDiffUtil

class StatusAdapter(val context: Context, var statusList: ArrayList<Status>, val fragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (fragment) {
            is HomeFragment -> {
                HomeFragmentViewHolder(
                    UserStatusSingleItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }

            is ViewStatusFragment -> {
                StatusFragmentViewHolder(
                    UserStatusAllBinding.inflate(LayoutInflater.from(parent.context) ,parent, false))
            }

            is MyStatusFragment ->{
                StatusFragmentViewHolder(
                    UserStatusAllBinding.inflate(LayoutInflater.from(parent.context) ,parent, false))
            }

            else -> {
                HomeFragmentViewHolder(
                    UserStatusSingleItemBinding.inflate(LayoutInflater.from(parent.context) ,parent, false))
            }
        }
    }

    override fun getItemCount() = statusList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentStatus = statusList[position]

        when(holder.javaClass == holder::class.java){

            (holder.javaClass == HomeFragmentViewHolder::class.java) ->{
                val viewHolder = holder as HomeFragmentViewHolder
                holder.binding.userName.text = currentStatus.userName
                if (holder.binding.statusImage != null && holder.binding.profile != null) {
                    Glide.with(context).load(currentStatus.statusUrl).into(holder.binding.statusImage)
                    Glide.with(context).load(currentStatus.userProfile).into(holder.binding.profile)
                }
            }

            (holder.javaClass == StatusFragmentViewHolder::class.java) ->{
                val viewHolder = holder as StatusFragmentViewHolder
                holder.binding.userName.text = currentStatus.userName
                holder.binding.dateTime.text = currentStatus.dateTime
                if (holder.binding.statusImage != null) {
                    Glide.with(context).load(currentStatus.statusUrl).into(holder.binding.statusImage)
                }
            }

            (holder.javaClass == MyStatusFragmentViewHolder::class.java) ->{
                val viewHolder = holder as MyStatusFragmentViewHolder
                holder.binding.userName.text = currentStatus.userName
                holder.binding.dateTime.text = currentStatus.dateTime
                if (holder.binding.statusImage != null) {
                    Glide.with(context).load(currentStatus.statusUrl).into(holder.binding.statusImage)
                }
            }
            else -> {
                val viewHolder = holder as HomeFragmentViewHolder
                holder.binding.userName.text = currentStatus.userName
                if (holder.binding.statusImage != null && holder.binding.profile != null) {
                    Glide.with(context).load(currentStatus.statusUrl).into(holder.binding.statusImage)
                    Glide.with(context).load(currentStatus.userProfile).into(holder.binding.profile)
                }
            }
        }
    }

    class HomeFragmentViewHolder(val binding: UserStatusSingleItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class StatusFragmentViewHolder(val binding: UserStatusAllBinding) :
        RecyclerView.ViewHolder(binding.root)

    class MyStatusFragmentViewHolder(val binding: UserStatusAllBinding):
            RecyclerView.ViewHolder(binding.root)

    fun setData(newStatusList: ArrayList<Status>){
        val diffUtil = MyDiffUtil(statusList,newStatusList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        statusList = newStatusList
        diffResult.dispatchUpdatesTo(this)
    }
}
