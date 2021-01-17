package com.alterpat.athan

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import com.alterpat.athan.dao.UserConfig
import com.alterpat.athan.tool.FileManager
import com.alterpat.athan.tool.PrayTime
import com.alterpat.athan.tool.createNotificationChannel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        val prayersNames = arrayListOf<String>("Fajr", "Duha", "Dhuhr", "Asr", "Maghrib", "Isha")

    }
    private var timerSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // creates a notification channel if android version > Android O
        createNotificationChannel(this)


        //townNameTV.text = city

        /** set hejir date **/
        // get current date in hejir
        val locale = ULocale("@calendar=islamic")
        val islamicCalendar: Calendar = Calendar.getInstance(locale)

        // date in "yyyy MMM dd" format
        val df1 = SimpleDateFormat("dd MMMM, yyyy", locale)
        var dateStr = df1.format(islamicCalendar.time)

        dateStr = dateStr.replace("II", "Ath-thani", ignoreCase = false)
        dateStr = dateStr.replace("I", "Al-Awwal", ignoreCase = false)
        dateTV.text = dateStr

        /** populate screen elements **/

        // get user conf from shared pref
        /** Get user preferences **/


        //var gsonBuilder: Gson = Gson()
        //val userConf : String = gsonBuilder.toJson(UserConfig())
        //FileManager.saveConf(this, userConf)
        //FileManager.write(userConf)


        val sharedPref = getSharedPreferences(
            "athanPrefs", Context.MODE_PRIVATE)
        var gsonBuilder: Gson = Gson()
        var json: String? = sharedPref.getString("userConfig", "")

        var userConfig : UserConfig

        if(json != "")
            userConfig = gsonBuilder.fromJson(json, UserConfig::class.java)
        else
            userConfig = UserConfig()

        updateUI(userConfig)


    }

    private fun updateUI(userConfig: UserConfig){
        val latitude = userConfig.lat
        var longitude = userConfig.long
        val timezone = userConfig.timezone

        val prayerTimes : HashMap<String, String> = PrayTime.getPrayerTimes(latitude, longitude, timezone)

        prayersNames.forEach {prayerName ->
            prayersLayout.addView(
                AthanItem(
                    applicationContext,
                    prayerName,
                    prayerTimes.get(prayerName)!!
                )
            )
        }
    }
}