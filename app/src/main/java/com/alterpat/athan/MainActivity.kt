package com.alterpat.athan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alterpat.athan.model.AthanItem
import com.alterpat.athan.model.PrayerTime
import com.alterpat.athan.model.SoundState
import com.alterpat.athan.model.UserConfig
import com.alterpat.athan.tool.PrayerTimeManager
import com.alterpat.athan.tool.cancelScheduledNotification
import com.alterpat.athan.tool.createNotificationChannel
import com.alterpat.athan.tool.scheduleNotification
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main3.*
import org.jetbrains.anko.Android
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    private lateinit var athanItems: ArrayList<AthanItem>
    private lateinit var countdown_timer: CountDownTimer
    private lateinit var prayers : ArrayList<PrayerTime>
    private lateinit var adapter : AthanAdapter
    private val TAG = "MainActivityTag"

    companion object {
        val prayersNames = arrayListOf<String>("Fajr", "Duha", "Dhuhr", "Asr", "Maghrib", "Isha")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        // creates a notification channel if android version > Android O
        createNotificationChannel(this)


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


        loadConfAndInit()


        // on city click launch search activity
        townNameTV.setOnClickListener {
            resultLauncher.launch(Intent(this, SearchActivity::class.java))
        }

        settingsBtn.setOnClickListener {
            resultLauncher.launch(Intent(this, SettingsActivity::class.java))
        }

    }

    // handle search activity result
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode === Activity.RESULT_OK) {
            // There are no request codes
            //val data: Intent? = result.data
            // reload userConfig
            loadConfAndInit()
        }
    }

    private fun loadConfAndInit(){
        val sharedPref = getSharedPreferences(
            getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
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

    private fun getAlarmId(athan : String) : Int {
        var res = 0

        when(athan){
            "Fajr"      -> res = 1
            "Duha"      -> res = 2
            "Dhuhr"     -> res = 3
            "Asr"       -> res = 4
            "Maghrib"   -> res = 5
            "Isha"      -> res = 6
        }

        return res
    }

    private fun init(userConfig: UserConfig){

        val now = System.currentTimeMillis()
        prayers = PrayerTimeManager.getPrayers(userConfig)
        athanItems = ArrayList<AthanItem>()

        townNameTV.text = userConfig.city
        //townNameTV.text = "\u06de \u0671\u0644\u0644\u0651\u064e\u0647\u064f \u0646\u064f\u0648\u0631\u064f \u0671\u0644]\u0633\u0651\u064e\u0645\u064e"


        Log.d(TAG, "notifications scheduled")



        prayers.forEach {prayer ->
            athanItems.add(AthanItem(prayer.name, prayer.timeStr))

            /*
      prayersLayout.addView(
          AthanItem(
              applicationContext,
              prayer.name,
              prayer.timeStr
          )
      )
       */
            /** program an alarm if not already past time **/

            /***
             *
             * CAUTION: When changing conf YOU should delete all previously programmed alarms first
             *
             ***/
            cancelScheduledNotification(this, prayer.timestamp, prayer.name, getAlarmId(prayer.name))


            if (prayer.timestamp >= now){
                scheduleNotification(this, prayer.timestamp, prayer.name, getAlarmId(prayer.name))
            }
        }

        scheduleNotification(this, System.currentTimeMillis()+1000, "test", getAlarmId("Fajr"))

        /** add item to ListView **/
        //athanItems[1].selected = true
        adapter = AthanAdapter(this, athanItems)
        prayersLayout.adapter = adapter

    }

    private fun startCountDown(){
        val now = System.currentTimeMillis()
        // init with next Fajr time
        Log.d(TAG, "${prayers[0].name} ${prayers[0].timeStr}")
        var millisInFuture : Long = abs(now - (prayers[0].timestamp + 24*60*60*1000))

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

    private class AthanAdapter(val context: Context, var athanItems: ArrayList<AthanItem>) : BaseAdapter() {
        private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // override other abstract methods here
        override fun getView(position: Int, convertView: View?, container: ViewGroup?): View? {

            var convertView: View? = convertView
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.athan_item, container, false)
            }

            convertView?.findViewById<TextView>(R.id.prayerNameTV)?.text = athanItems[position].prayerName
            convertView?.findViewById<TextView>(R.id.prayerTimeTV)?.text = athanItems[position].prayerTime

            when(athanItems[position].sound){
                SoundState.ON -> null //convertView?.findViewById<ImageView>(R.id.soundCtrl)?.setImageResource()
                SoundState.OFF -> null
                SoundState.BEEP -> null
            }

            when(athanItems[position].selected){
                true -> convertView?.findViewById<View>(R.id.main)?.background = context.getDrawable(R.drawable.round_bg_gray)
                false -> convertView?.findViewById<View>(R.id.main)?.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
            }

            return convertView
        }

        override fun getItem(position: Int): Any {
            return athanItems[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return athanItems.size
        }
    }
}