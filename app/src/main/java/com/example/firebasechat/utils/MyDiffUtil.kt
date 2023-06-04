package com.example.firebasechat.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.firebasechat.mvvm.model.Status

class MyDiffUtil(
    private val oldList: ArrayList<Status>,
    private val newList: ArrayList<Status>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        when{
            oldList[oldItemPosition].id != newList[newItemPosition].id->{
                return false
            }
            oldList[oldItemPosition].statusUrl != newList[newItemPosition].statusUrl->{
                return false
            }
            oldList[oldItemPosition].userName != newList[newItemPosition].userName->{
                return false
            }
            oldList[oldItemPosition].userProfile != newList[newItemPosition].userProfile->{
                return false
            }
            oldList[oldItemPosition].dateTime != newList[newItemPosition].dateTime->{
                return false
            }
            else-> return true
        }
    }
}