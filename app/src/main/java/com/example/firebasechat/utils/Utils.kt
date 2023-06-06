package com.example.firebasechat.utils

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.firebasechat.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
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

    /*
        MM/dd/yyyy hh:mm:ss tt	08/05/2006 03:05:15 PM
        M/d/yy h :m:s tt	8/5/06 3:5:15 PM
        ddd MMM dd yyyy	Sat Aug 05 2006
        "yyyy-MM-dd hh:mm:ss a"  2023-06-06 05:50:51 PM
        dddd, MMMM dd yyyy	Saturday, August 05 2006
    */

    /*fun changeTimeFormat(time:String?):String?{
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
    }*/

   /*fun changeDateFormat(time: String?): String? {
        var changedDate = ""
        try {
            val fmt = SimpleDateFormat("yyyy-MM-dd ")

            val date = fmt.parse(time)
            changedDate = fmt.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return changedDate
    }*/

    /*fun changeTimeFormat(time: String?): String?{
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

    fun dateTime(calendar: Calendar): String {
        val dtForm: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a",Locale.US)
        val date: String = dtForm.format(calendar.time)
        return date
    }


    fun getImageUriFromBitmap(context: Context?, bitmap: Bitmap?):Uri{
        val bytes = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,100, bytes)
        val path = MediaStore.Images.Media.insertImage(context?.contentResolver,bitmap,"Title",null)
        return Uri.parse(path)
    }

    fun getUriFromFile(context: Context?, bitmap: Bitmap):Uri{
        val tempFile = File.createTempFile("profile", ".png")
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fileOutPut = FileOutputStream(tempFile)
        fileOutPut.write(bitmapData)
        fileOutPut.flush()
        fileOutPut.close()
        return Uri.fromFile(tempFile)
    }

}




