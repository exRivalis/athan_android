package com.alterpat.athan

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.alterpat.athan.dao.UserConfig
import com.alterpat.athan.model.AthanItem
import com.alterpat.athan.model.PrayerTime
import com.alterpat.athan.tool.PrayerTimeManager
import com.alterpat.athan.tool.createNotificationChannel
import com.alterpat.athan.tool.scheduleNotifications
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    private lateinit var countdown_timer: CountDownTimer
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var prayers : ArrayList<PrayerTime>
    private val TAG = "MainActivityTag"

    companion object {
        val prayersNames = arrayListOf<String>("Fajr", "Duha", "Dhuhr", "Asr", "Maghrib", "Isha")

    }

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

        // update UI & set alarms
        init(userConfig)


    }

    private fun init(userConfig: UserConfig){
        val latitude = userConfig.lat
        var longitude = userConfig.long
        val timezone = userConfig.timezone

        val now = System.currentTimeMillis()
        prayers = PrayerTimeManager.getPrayers(latitude, longitude, timezone)

        townNameTV.text = userConfig.city


        Log.d(TAG, "notifications scheduled")


        prayers.forEach {prayer ->

            /** add item to screen **/
            prayersLayout.addView(
                AthanItem(
                    applicationContext,
                    prayer.name,
                    prayer.timeStr
                )
            )

            /** program an alarm if not already past time **/
            if (prayer.timestamp >= now){
                scheduleNotifications(this, prayer.timestamp, prayer.name)
            }
        }

    }

    private fun startCountDown(){
        val now = System.currentTimeMillis()
        // init with next Fajr time
        var millisInFuture : Long = abs(now - prayers[0].timestamp)

        // in case it's post Isha: millisInFututre won't change
        loop@
        for(prayer in prayers){
            if (prayer.timestamp >= now){
                millisInFuture = prayer.timestamp - now
                break@loop
            }
        }

        try {
            countdown_timer.cancel()
        }catch (e: Exception){

        }

        countdown_timer = object : CountDownTimer(millisInFuture, 1000) {
            override fun onFinish() {
                // if finished start countdown to next prayer
                startCountDown()

                /*TODO
                * show a dialog with a message saying it's time
                */
            }
            override fun onTick(remaining: Long) {
                val t = remaining/1000
                val hours = t / (60 * 60)
                val minutes = (t - hours*60*60) / 60
                val seconds = (t - hours*60*60 - minutes*60)
                counterTV.text = "%02d:%02d:%02d".format(hours, minutes, seconds)
            }
        }.start()
    }

    override fun onStart() {
        super.onStart()
        // start count down to next prayer
        startCountDown()
    }

    override fun onResume() {
        super.onResume()
        startCountDown()
    }

    override fun onStop() {
        super.onStop()
        countdown_timer.cancel()
    }

    override fun onPause() {
        super.onPause()
        countdown_timer.cancel()
    }
}