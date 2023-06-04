package com.example.firebasechat.mvvm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.firebasechat.mvvm.dao.Dao
import com.example.firebasechat.mvvm.model.Message
import com.example.firebasechat.mvvm.model.Status
import com.example.firebasechat.mvvm.model.User

@Database(entities = [User::class, Status::class, Message::class], version = 1, exportSchema = false)
abstract class RoomDatabase {

    abstract fun dao(): Dao

    companion object{
        @Volatile
        var INSTANCE: RoomDatabase? = null

        fun getDataFromRoomDatabase(context: Context): RoomDatabase{
            if (INSTANCE != null) return INSTANCE!!
            synchronized(this) {
                return Room.databaseBuilder(context, RoomDatabase::class.java, "ChitChat").build()
            }
        }
    }
}