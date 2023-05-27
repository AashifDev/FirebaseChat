package com.example.firebasechat.utils

import android.content.Context
import android.preference.PreferenceManager
import android.widget.Toast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun getInt(context: Context?, key: String, defaultValue: Int): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(key, defaultValue)
    }

    fun getLong(context: Context?, key: String, defaultValue: Long): Long {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getLong(key, defaultValue)
    }

    fun putInt(context: Context?, key: String, value: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putInt(key, value).apply()
    }

    fun putLong(context: Context?, key: String, value: Long) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putLong(key, value).apply()
    }

    fun getString(context: Context?, key: String, defaultValue: String): String? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(key, defaultValue)
    }

    fun putString(context: Context?, key: String, value: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putString(key, value).apply()
    }

    fun getBoolean(context: Context?, key: String, defaultValue: Boolean): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(key, defaultValue)
    }

    fun putBoolean(context: Context?, key: String, value: Boolean) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putBoolean(key, value).apply()
    }

    fun remove(context: Context?, key: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().remove(key).apply()
    }

    fun clear(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().clear().apply()
    }

    fun createToast(context: Context?, msg:String?){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
    }

    fun changeDateFormat(date:String?):String?{
        var formattedDate = ""
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val outputFormat = SimpleDateFormat("dd-MM-yyyy")
            val date = inputFormat.parse(date)
            formattedDate = outputFormat.format(date)

        }catch (e:ParseException){
            e.printStackTrace()
        }
        return formattedDate
    }

    fun changeTimeFormat(time:String?):String?{
        var formattedTime = ""
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.US)
            val time = inputFormat.parse(time)
            formattedTime = outputFormat.format(time)
        }catch (e:ParseException){
            e.printStackTrace()
        }
        return formattedTime
    }

   /* fun changeDateFormat(time: String?): String? {
        var changedDate = ""
        try {
            val fmt = SimpleDateFormat("yyyy-MM-dd ")

            val date = fmt.parse(time)
            changedDate = fmt.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return changedDate
    }

    fun changeTimeFormat(time: String?): String?{
        var changedTime = ""
        try {
            val display = SimpleDateFormat("hh:mm a", Locale.US)
            val fmt = SimpleDateFormat("yyyy-MM-dd 'T' HH:mm:ss.SSS'Z'")
            val date = fmt.parse(time)
            changedTime = display.format(date)
        }catch (e: ParseException){
            e.printStackTrace()
        }
        return changedTime
    }*/
}




