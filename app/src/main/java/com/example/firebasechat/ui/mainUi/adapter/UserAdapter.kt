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
import com.example.firebasechat.ui.mainUi.fragment.HomeFragment
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val context: Context, val usrArrList: ArrayList<User>, val listener:HomeFragment) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val user:TextView = itemView.findViewById(R.id.userName)
        val profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
        val isActive: TextView = itemView.findViewById(R.id.isActiveLogo)
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
                putBoolean("isActive", item.isActive)
            }
            holder.user.findNavController().navigate(R.id.viewSendMessageFragment,bundle)
        }
        if (holder.profileImage != null){
            Glide.with(context).load(item.pic).into(holder.profileImage)
        }
        else{
            holder.profileImage.setImageResource(R.drawable.personn)
        }

        if (item.isActive){
            holder.isActive.visibility = View.VISIBLE
        }else{
            holder.isActive.visibility = View.GONE
        }

        listener.getUid(item.uid)

    }

}