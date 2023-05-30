package com.example.firebasechat.ui.mainUi.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasechat.R
import com.example.firebasechat.model.User
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val context: Context, val usrArrList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val user:TextView = itemView.findViewById(R.id.userName)
        val profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_single_item, parent,false))
    }

    override fun getItemCount() = usrArrList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = usrArrList[position]
        holder.user.text = item.name
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("userName", item.name)
                putString("uid",item.uid)
                putString("pic",item.pic)
            }
            holder.user.findNavController().navigate(R.id.viewSendMessageFragment,bundle)
        }
        Glide.with(context).load(item.pic).into(holder.profileImage)
    }

}