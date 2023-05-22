package com.example.firebasechat.ui.mainUi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechat.R
import com.example.firebasechat.model.Message
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val msgList: ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val SENT_MESSAGE = 2
    val RECEIVE_MESSAGE = 1



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1){
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.receive_message_single_item,parent,false)
            ReceiveViewHolder(view)
        }else{
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.send_message_single_item,parent,false)
            SendViewHolder(view)
        }
    }

    override fun getItemCount() = msgList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = msgList[position]
        if (holder.javaClass == SendViewHolder::class.java){
            val viewHolder = holder as SendViewHolder
            holder.sentMessage.text = currentMessage.message
        }else{
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message

        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = msgList[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            SENT_MESSAGE
        }else{
            RECEIVE_MESSAGE
        }
    }

    class SendViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sentMessage: TextView = itemView.findViewById(R.id.send)
    }
    class ReceiveViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val receiveMessage: TextView = itemView.findViewById(R.id.receive)
    }
}