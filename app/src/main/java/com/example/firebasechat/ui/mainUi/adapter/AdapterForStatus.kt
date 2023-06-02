/*
package com.example.firebasechat.ui.mainUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasechat.R
import com.example.firebasechat.model.Status
import com.example.firebasechat.ui.mainUi.fragment.HomeFragment
import com.example.firebasechat.ui.mainUi.fragment.ViewStatusFragment

class StatusAdapter(val context: Context, val statusList: ArrayList<Status>, val fragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(fragment){
            is HomeFragment -> {
                HomeFragmentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_status_single_item, parent, false))
            }

            is ViewStatusFragment->{
                StatusFragmentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_status_all, parent, false))
            }

            else -> {
                HomeFragmentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_status_single_item, parent, false))
            }
        }
    }

    override fun getItemCount() = statusList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentStatus = statusList[position]
        if (holder.javaClass == HomeFragmentViewHolder::class.java){
            val viewHolder = holder as HomeFragmentViewHolder
            if (holder.statusImg != null && holder.userProfile != null){
                Glide.with(context).load(currentStatus.statusUrl).into(holder.statusImg)
                Glide.with(context).load(currentStatus.userProfile).into(holder.userProfile)
            }
        }else{
            val viewHolder = holder as StatusFragmentViewHolder
            if (holder.statusImg1 != null && holder.userProfile1 != null){
                Glide.with(context).load(currentStatus.statusUrl).into(holder.statusImg1)
                Glide.with(context).load(currentStatus.userProfile).into(holder.userProfile1)
            }
        }


    }

    class HomeFragmentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val statusImg: ImageView = itemView.findViewById(R.id.statusImage)
        val userProfile: ImageView = itemView.findViewById(R.id.profile)
        val userName:TextView = itemView.findViewById(R.id.userName)
    }
    class StatusFragmentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val statusImg1: ImageView = itemView.findViewById(R.id.statusImage)
        val userProfile1: ImageView = itemView.findViewById(R.id.profile)
        val userName1:TextView = itemView.findViewById(R.id.userName)
    }
}*/
