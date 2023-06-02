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

class StatusAdapter(val context: Context, val statusList: ArrayList<Status>) : RecyclerView.Adapter<StatusAdapter.ViewHolder>() {
    class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val statusImg: ImageView = itemView.findViewById(R.id.statusImage)
        val userProfile: ImageView = itemView.findViewById(R.id.profile)
        val userName:TextView = itemView.findViewById(R.id.userName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_status_single_item, parent, false))
    }

    override fun getItemCount() = statusList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = statusList[position]
        holder.userName.text = item.userName
        if (holder.statusImg != null && holder.userProfile != null){
            Glide.with(context).load(item.statusUrl).into(holder.statusImg)
            Glide.with(context).load(item.userProfile).into(holder.userProfile)
        }
    }
}