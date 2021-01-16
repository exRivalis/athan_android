package com.alterpat.athan

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.alterpat.athan.tool.createNotificationChannel
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private val prayers = arrayListOf<String>("Fajr", "Duha", "Dhuhr", "Asr", "Maghrib", "Isha")
    private var timerSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // creates a notification channel if android version > Android O
        createNotificationChannel(this)

        // get city name from previous activity
        var city = intent.getStringExtra("city")
        townNameTV.text = city

        // get current date in hejir
        val locale = ULocale("@calendar=islamic")
        val islamicCalendar: Calendar = Calendar.getInstance(locale)

        // date in "yyyy MMM dd" format
        val df1 = SimpleDateFormat("dd MMMM, yyyy", locale)
        var dateStr = df1.format(islamicCalendar.time)

        dateStr = dateStr.replace("II", "Ath-thani", ignoreCase = false)
        dateStr = dateStr.replace("I", "Al-Awwal", ignoreCase = false)
        dateTV.text = dateStr

        // get time to compute remaining time to next prayer
        var today = Calendar.getInstance().apply { time = Date()}

        var prayersJson = JSONObject(intent.getStringExtra("prayers"))

        // set & add prayer items to screen
        prayers.forEach {
            var prayerName = it
            var prayerTime = prayersJson.getString(it)
                // add view to screen with prayer time
                prayersLayout.addView(
                    AthanItem(
                        applicationContext,
                        prayerName,
                        prayerTime
                    )
                )

                // set timer to next prayer
                // check if this prayer is next
                var prayerDate = Calendar.getInstance().apply {
                    time = Date()
                    set(Calendar.HOUR_OF_DAY, prayerTime.split(":")[0].toInt())
                    set(Calendar.MINUTE, prayerTime.split(":")[1].toInt())
                    set(Calendar.SECOND, 0)
                }

                if(!timerSet && prayerDate > today){
                    timerSet = true
                    nextPrayerTV.text = "$prayerName dans :"
                    counterTV.isCountDown = true
                    // since base needs an "elapsedRealtime" -> compute delta between realtime and device startup time
                    var delta = abs(SystemClock.elapsedRealtime() - today.timeInMillis)
                    // remove this delta from prayer time milliseconds
                    var millis = prayerDate.timeInMillis - delta
                    counterTV.base = millis
                    counterTV.start()
                }
        }

        // if still timerSet to false -> set remaining time to tomorrow fajr
        if(!timerSet){
            var prayerTime = prayersJson.getString(prayers[0])

            val tomorrow = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                time
            }

            var prayerDate = Calendar.getInstance().apply {
                time = Date()
                set(Calendar.HOUR_OF_DAY, prayerTime.split(":")[0].toInt())
                set(Calendar.MINUTE, prayerTime.split(":")[1].toInt())
                set(Calendar.SECOND, 0)
            }

            nextPrayerTV.text = "${prayers[0]} dans :"
            counterTV.isCountDown = true
            // since base needs an "elapsedRealtime" -> compute delta between realtime and device startup time
            var delta = abs(SystemClock.elapsedRealtime() - today.timeInMillis)
            // remove this delta from prayer time milliseconds
            var millis = prayerDate.timeInMillis - delta
            counterTV.base = millis
            counterTV.start()

        }

    }






}