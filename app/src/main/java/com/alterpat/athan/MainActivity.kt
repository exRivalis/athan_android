package com.alterpat.athan

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.lang.Integer.max
import java.net.URL


class MainActivity : AppCompatActivity() {

    private val prayers = arrayListOf<String>("Fajr", "Duha", "Dhuhr", "Asr", "Maghrib", "Isha")

    private val PRIMARY_CHANNEL_ID = "athan_notification_channel"
    private var mNotifyManager: NotificationManager? = null

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
        var i = 34
        scheduleNotifications(1, 16, 9, 35, "Test")
        scheduleNotifications(1, 16, 9, 40, "Test 2")
        //scheduleNotifications(20, i+1)
        //scheduleNotifications(20, i+2)

        var countryCode = "FR"
        var zipCode = "92260"
        var method = 4
        var timeFormat = 0

        getPrayers(countryCode, zipCode, method, timeFormat, false)


        val locale = ULocale("@calendar=islamic")
        val islamicCalendar: Calendar = Calendar.getInstance(locale)

        // date in "yyyy MMM dd" format
        val df1 = SimpleDateFormat("dd MMMM, yyyy", locale)
        var dateStr = df1.format(islamicCalendar.time)

        dateStr = dateStr.replace("II", "Ath-thani", ignoreCase = false)
        dateStr = dateStr.replace("I", "Al-Awwal", ignoreCase = false)
        dateTV.text = dateStr


    /*
        val locale: Locale = Locale.forLanguageTag("en-US-u-ca-islamic-umalqura")
        val chrono: Chronology = Chronology.ofLocale(locale)
        System.out.println(chrono.toString())

     */
    }

    private fun getPrayers(countryCode:String, zipCode:String, method:Int=3, timeFormat:Int=0, fullMonth: Boolean = false){
        val baseUrl = "https://www.islamicfinder.us/index.php/api/prayer_times?"
        var url = baseUrl + "country=$countryCode&zipcode=$zipCode&method=$method&time_format=$timeFormat"

        if(fullMonth)
            url += "&show_entire_month"


        doAsync {
            val respJsonStr = URL(url).readText()
            val json = JSONObject(respJsonStr)

            val success = json.getBoolean("success")
            if (success) {

                runOnUiThread {
                    townNameTV.text =
                        json.getJSONObject("settings").getJSONObject("location").getString("city")
                }

                var prayersJson = json.getJSONObject("results")

                prayers.forEach {
                    var prayerName = it
                    var prayerTime = prayersJson.getString(it)
                    runOnUiThread {
                        prayersLayout.addView(AthanItem(applicationContext, prayerName, prayerTime))
                    }
                }
            } else {
                runOnUiThread {
                    Toast.makeText(
                        baseContext,
                        " Echec lors de la récupération des données ",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
        // pour demain

        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            time
        }

        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")

        val tomorrowAsString: String = dateFormat.format(tomorrow)


        var url2 = baseUrl + "country=$countryCode&zipcode=$zipCode&method=$method&time_format=$timeFormat&date=${tomorrowAsString}"


        var date = tomorrowAsString.split('-')
        val month = date[1].toInt()
        val day = date[2].toInt()

        doAsync {
            val respJsonStr = URL(url2).readText()
            val json = JSONObject(respJsonStr)

            val success = json.getBoolean("success")
            if(success) {

                runOnUiThread {
                    townNameTV.text = json.getJSONObject("settings").getJSONObject("location").getString("city")
                }

                var prayersJson = json.getJSONObject("results")

                prayers.forEach {
                    var prayerName = it
                    var prayerTime = prayersJson.getString(prayerName)
                    var h = prayerTime.split(':')[0].toInt()
                    var m = prayerTime.split(':')[1].toInt()
                    scheduleNotifications(month, day, h, m, prayerName)
                }
            }

        }

        /*
        runOnUiThread {
            scheduleNotifications(month, 14, 22, 53)
        }
         */

    }



    private fun createNotificationChannel() {
        mNotifyManager = getSystemService(NotificationManager::class.java) as NotificationManager

        // notification channels are only available in API 26 and higher
        // Create a NotificationChannel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Athan Notification",
                NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)

            // Creating an Audio Attribute
            // Creating an Audio Attribute
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()

            var uri = Uri.parse(
                "android.resource://"
                        + getPackageName()
                        + "/"
                        + R.raw.athan)

            notificationChannel.setSound(uri, audioAttributes)
            notificationChannel.setDescription("Athan");

            mNotifyManager!!.createNotificationChannel(notificationChannel);

        } else {
            TODO("VERSION.SDK_INT < O")
        }



    }

    private fun scheduleNotifications(month: Int, day: Int, hour: Int, minute: Int, athan: String) {
        // Set the alarm to start
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.MONTH, max(0, month-1))
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")

        val tomorrowAsString: String = dateFormat.format(calendar)
        System.out.println(tomorrowAsString)

        alarmIntent = Intent(this, AthanReceiver::class.java).let { intent ->
            intent.action = "ATHAN_ALARM"
            intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            intent.putExtra("prayer", athan)
            PendingIntent.getBroadcast(this, calendar.timeInMillis.toInt(), intent, 0)
        }


        alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmMgr?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )

        var uri = Uri.parse(
                "android.resource://"
                        + getPackageName()
                        + "/"
                        + R.raw.athan)
    }

}