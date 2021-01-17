package com.alterpat.athan

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Room
import com.alterpat.athan.dao.PrayerDatabase
import com.alterpat.athan.dao.Prayer
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class SplashScreenActivity : AppCompatActivity() {

    //private val prayerNames = arrayListOf<String>("Fajr", "Duha", "Dhuhr", "Asr", "Maghrib", "Isha")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        /** Get user preferences **/
        val sharedPref = getSharedPreferences(
            "athanPrefs", Context.MODE_PRIVATE
        )

        var countryCode = sharedPref.getString("countryCode", "FR")!!
        var city = sharedPref.getString("city", "Fontenay-aux-roses")!!
        var zipCode = sharedPref.getString("zipCode", "92260")!!
        var method = sharedPref.getInt("method", 4)
        var timeFormat = sharedPref.getInt("timeFormat", 0)
        val lastLoad = sharedPref.getString("lastLoad", "")!!

        val df1 = android.icu.text.SimpleDateFormat("yyyy-MM-dd")
        var todayStr = df1.format(System.currentTimeMillis())

        // get prayers for the whole current month
        /*
       if(lastLoad != todayStr)
            getPrayers(todayStr, countryCode, zipCode, method, timeFormat, true)
       else
         startActivity(Intent(applicationContext, MainActivity::class.java))

         */

        test()

    }

    private fun test(){
        doAsync {
            val db = PrayerDatabase.getInstance(baseContext)
            var prayerDao = db.prayerDao
            prayerDao.clean()

            var now = System.currentTimeMillis()


            val d1 = Date(now + 2*6 * 10000)
            val d2 = Date(now + 7*6 * 10000)
            val d3 = Date(now + 9*6 * 10000)
            val d4 = Date(now + 12*6 * 10000)
            val d5 = Date(now + 18*6 * 10000)

            System.out.println(d1)
            System.out.println(d2)
            System.out.println(d3)
            System.out.println(d4)
            System.out.println(d5)

            prayerDao.insert( Prayer(d1.time,"2021-01-17", "00:00", "Test1"))
            prayerDao.insert( Prayer(d2.time,"2021-01-17", "01:00", "Test2"))
            prayerDao.insert( Prayer(d3.time,"2021-01-17", "02:00", "Test3"))
            prayerDao.insert( Prayer(d4.time,"2021-01-17", "03:00", "Test4"))
            prayerDao.insert( Prayer(d5.time,"2021-01-17", "04:00", "Test5"))

            startActivity(Intent(applicationContext, MainActivity::class.java))

        }

    }

    private fun getPrayers(todayStr: String, countryCode:String, zipCode:String, method:Int=3, timeFormat:Int=0, fullMonth: Boolean = false){
        val baseUrl = "https://www.islamicfinder.us/index.php/api/prayer_times?"
        var url = baseUrl + "country=$countryCode&zipcode=$zipCode&method=$method&time_format=$timeFormat"

        if(fullMonth)
            url += "&show_entire_month"


        doAsync {
            val respJsonStr = URL(url).readText()
            val json = JSONObject(respJsonStr)

            // save prayers to db
            // insert a prayer to prayers table into local db
            val db = PrayerDatabase.getInstance(baseContext)
            var prayerDao = db.prayerDao
            prayerDao.clean()

            val success = json.getBoolean("success")
            if (success) {
                var prayersJson = json.getJSONObject("results")

                var prayers : Array<Prayer> = arrayOf()

                // for each day add it to prayers
                prayersJson.keys().forEach { date ->
                    // get a day's prayers
                    val dayPrayers = prayersJson.getJSONObject(date)
                    dayPrayers.keys().forEach { prayerName ->
                        // get a single prayer & add it to prayers
                        var time = dayPrayers.getString(prayerName)
                        var timestamp : Long= SimpleDateFormat("yyyy-MM-dd hh:mm").parse("$date $time").let { it.time }
                        val prayer = Prayer(timestamp, date, time, prayerName)
                        prayerDao.insert(prayer)
                    }
                }


                val sharedPref = getSharedPreferences(
                    "athanPrefs", Context.MODE_PRIVATE
                )

                with (sharedPref.edit()) {
                    putString("lastLoad", todayStr)
                    apply()
                }

                startActivity(Intent(applicationContext, MainActivity::class.java))

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
    }

    /*

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
                    scheduleNotifications(applicationContext, month, day, h, m, prayerName)
                }
            }

        }

     */

}