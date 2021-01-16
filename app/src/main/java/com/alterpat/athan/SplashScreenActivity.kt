package com.alterpat.athan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Room
import com.alterpat.athan.dao.AppDatabase
import com.alterpat.athan.dao.Prayer
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.net.URL

class SplashScreenActivity : AppCompatActivity() {

    private val prayers = arrayListOf<String>("Fajr", "Duha", "Dhuhr", "Asr", "Maghrib", "Isha")
    private lateinit var db : AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        var countryCode = "FR"
        var zipCode = "92260"
        var method = 4
        var timeFormat = 0

        getPrayers(countryCode, zipCode, method, timeFormat, false)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "athan-db"
        ).build()


        /** THIS IS A TEST **/
        // insert a prayer to prayers table into local db
        val prayerDao = db.prayerDao()
        doAsync {
            prayerDao.insertAll(Prayer(19808, "2020-01-18", 20, 34, "Isha"))
        }
        /** END **/

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
                var prayersJson = json.getJSONObject("results")
                var city = json.getJSONObject("settings").getJSONObject("location").getString("city")

                var intent = Intent(applicationContext, MainActivity::class.java)

                intent.putExtra("city", city)
                intent.putExtra("prayers", prayersJson.toString())

                startActivity(intent)

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