package com.alterpat.athan

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.alterpat.athan.dao.PrayerDatabase
import com.alterpat.athan.tool.AthanReceiver
import com.alterpat.athan.tool.AthanService
import com.alterpat.athan.tool.createNotificationChannel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    //private val prayersNames = arrayListOf<String>("Fajr", "Duha", "Dhuhr", "Asr", "Maghrib", "Isha")
    private var timerSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // creates a notification channel if android version > Android O
        createNotificationChannel(this)

        //val serviceIntent = Intent(this, AthanService::class.java)
        //serviceIntent.putExtra("input", "Hello")

        //startService(serviceIntent)
        val intent = Intent(this,  AthanReceiver::class.java)
        intent.action = "startUp"
        sendBroadcast(intent)

        // get city name from shared prefs
        /** Get user preferences **/
        val sharedPref = getSharedPreferences(
            "athanPrefs", Context.MODE_PRIVATE
        )

        var countryCode = sharedPref.getString("countryCode", "FR")!!
        var city = sharedPref.getString("city", "Fontenay-aux-roses")!!
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

        // populate screen elements
        setPrayers()

    }

    private fun setPrayers(){
        // get time to compute remaining time to next prayer
        var today = Calendar.getInstance().apply { time = Date()}

        val db = PrayerDatabase.getInstance(baseContext)
        val prayerDao = db.prayerDao

        // request today's prayers
        doAsync {
            val df1 = SimpleDateFormat("yyyy-MM-dd")
            var dateStr = df1.format(System.currentTimeMillis())

            /** THIS IS A TEST **/
            /*
            val pTime = System.currentTimeMillis() + 3 * 60 * 1000
            val p =Prayer(pTime, "2021-01-16", "14:99", "Tesdt")
            prayerDao.insert(p)
            */
            /** END **/

            var prayers = prayerDao.loadByDay(dateStr)
            // set & add prayer items to screen
            prayers.forEach { prayer ->
                var prayerName = prayer.name
                var prayerTime = prayer.time
                // add view to screen with prayer time
                runOnUiThread{
                    prayersLayout.addView(
                        AthanItem(
                            applicationContext,
                            prayerName,
                            prayerTime
                        )
                    )
                }


                // set countdown to next prayer
                // check if this prayer is next

                if(!timerSet && prayer.timestamp > today.timeInMillis){
                    timerSet = true
                    nextPrayerTV.text = "$prayerName dans :"
                    counterTV.isCountDown = true
                    // since base needs an "elapsedRealtime" -> compute delta between realtime and device startup time
                    var delta = abs(SystemClock.elapsedRealtime() - today.timeInMillis)
                    // remove this delta from prayer time milliseconds
                    var millis = prayer.timestamp - delta
                    counterTV.base = millis
                    counterTV.start()
                }
            }

            // if still timerSet to false -> set remaining time to tomorrow fajr
            if(!timerSet){
                val tomorrow = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    time
                }

                var prayerDate = Calendar.getInstance().apply {
                    time = Date()
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, prayers[0].time.split(":")[0].toInt())
                    set(Calendar.MINUTE, prayers[0].time.split(":")[1].toInt())
                    set(Calendar.SECOND, 0)
                }

                nextPrayerTV.text = "${prayers[0].name} dans"
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




}