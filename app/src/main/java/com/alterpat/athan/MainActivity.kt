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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alterpat.athan.model.AthanItem
import com.alterpat.athan.model.PrayerTime
import com.alterpat.athan.model.SoundState
import com.alterpat.athan.model.UserConfig
import com.alterpat.athan.tool.PrayerTimeManager
import com.alterpat.athan.tool.cancelScheduledNotification
import com.alterpat.athan.tool.createNotificationChannels
import com.alterpat.athan.tool.scheduleNotification
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.activity_main3.prayersLayout
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    private lateinit var athanItems: ArrayList<AthanItem>
    private lateinit var countdown_timer: CountDownTimer
    private lateinit var prayers : ArrayList<PrayerTime>
    private lateinit var adapter : AthanAdapter
    private lateinit var sheetBehavior: BottomSheetBehavior<View>

    private val TAG = "MainActivityTag"

    companion object {
        val prayersNames = arrayListOf<String>("Fajr", "Duha", "Dhuhr", "Asr", "Maghrib", "Isha")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        // creates a notification channel if android version > Android O
        createNotificationChannels(this)


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

        // detect bottom sheet expansion/collapse & update content to always show next prayer on peek mode
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // if expanding and peek item is still visible -> hide it
                if(slideOffset > 0 && nextPrayerItem.visibility == View.VISIBLE){
                    nextPrayerItem.visibility = View.GONE
                }


            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // when collapsed -> show peek item
                when(newState){
                    4 -> nextPrayerItem.visibility = View.VISIBLE
                }
            }

        })


        /** setup UI **/
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
            prayersNames[0] -> res = 1
            prayersNames[1] -> res = 2
            prayersNames[2] -> res = 3
            prayersNames[3] -> res = 4
            prayersNames[4] -> res = 5
            prayersNames[5] -> res = 6
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


        /** load userConf from shared prefs **/
        val sharedPref = getSharedPreferences(
            getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
        var gsonBuilder: Gson = Gson()

        var jsonConf: String? = sharedPref.getString("userConfig", "")
        var userConfig : UserConfig
        if(jsonConf != "")
            userConfig = gsonBuilder.fromJson(jsonConf, UserConfig::class.java)
        else
            userConfig = UserConfig()

        prayers.forEach {prayer ->
            val position = prayers.indexOf(prayer)
            val soundState = userConfig.prayerSoundCtl[position]
            athanItems.add(AthanItem(prayer.name, prayer.timeStr, soundState))

            /** program an alarm if not already past time **/

            /***
             *
             * CAUTION: When changing conf YOU should delete all previously programmed alarms first
             *
             ***/
            cancelScheduledNotification(this, prayer.timestamp, prayer.name, getAlarmId(prayer.name))


            if (prayer.timestamp >= now && userConfig.prayerAlert){
                scheduleNotification(this, prayer.timestamp, prayer.name, getAlarmId(prayer.name), soundState)
            }
        }

        //scheduleNotification(this, System.currentTimeMillis()+500, "test", getAlarmId("Fajr"), SoundState.OFF)
        //scheduleNotification(this, System.currentTimeMillis()+1500, "test", getAlarmId("Fajr"), SoundState.BEEP)

        /** add item to ListView **/
        //athanItems[1].selected = true
        adapter = AthanAdapter(this, athanItems)
        prayersLayout.adapter = adapter

        /** on click change prayer call : ON, OFF or BEEP sound & update userConf **/
        prayersLayout.setOnItemClickListener { parent, view, position, id ->
            var athanItem  = athanItems[position]
            when(athanItem.sound){
                SoundState.ON -> {
                    athanItems[position].sound = SoundState.BEEP
                    Toast.makeText(applicationContext, getString(R.string.athan_switche_beep)+" ${athanItem.prayerName}", Toast.LENGTH_LONG).show()
                }
                SoundState.BEEP -> {
                    athanItems[position].sound = SoundState.OFF
                    Toast.makeText(applicationContext, getString(R.string.athan_switched_off)+" ${athanItem.prayerName}", Toast.LENGTH_LONG).show()
                }
                SoundState.OFF -> {
                    athanItems[position].sound = SoundState.ON
                    Toast.makeText(applicationContext, getString(R.string.athan_switched_on)+" ${athanItem.prayerName}", Toast.LENGTH_LONG).show()
                }
            }
            adapter.notifyDataSetChanged()

            if(athanItem.prayerName == nextPrayerItem.findViewById<TextView>(R.id.prayerNameTV).text)
                updateNextPrayerUI(athanItem)

            // update userConf
            updateUserConfCalls(position, athanItems[position].sound)
        }
    }

    private fun updateUserConfCalls(position: Int, soundState: SoundState){
        /** load userConf from shared prefs **/
        val sharedPref = getSharedPreferences(
            getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
        var gsonBuilder: Gson = Gson()

        var jsonConf: String? = sharedPref.getString("userConfig", "")
        var userConfig : UserConfig
        if(jsonConf != "")
            userConfig = gsonBuilder.fromJson(jsonConf, UserConfig::class.java)
        else
            userConfig = UserConfig()

        /** update userConf **/
        var array = userConfig.prayerSoundCtl.toTypedArray()
        array[position] = soundState
        userConfig.prayerSoundCtl = array.toList()

        /** save to shared prefs **/
        with (sharedPref.edit()) {
            putString("userConfig", gsonBuilder.toJson(userConfig))
            apply()
        }

    }
    private fun startCountDown(){
        val now = System.currentTimeMillis()
        var nextPrayer : PrayerTime = PrayerTime("Fajr", prayers[0].timestamp + 24*60*60*1000, prayers[0].timeStr)
        var position = 0

        // init with next Fajr time
        Log.d(TAG, "${prayers[0].name} ${prayers[0].timeStr}")
        var millisInFuture : Long = abs(now - (prayers[0].timestamp + 24*60*60*1000))

        // in case it's post Isha: millisInFututre won't change
        loop@
        for(prayer in prayers){
            if (prayer.timestamp >= now){
                millisInFuture = prayer.timestamp - now
                nextPrayer = prayer

                position = prayers.indexOf(prayer)

                athanItems.map { it.selected = false }
                athanItems[position].selected = true
                adapter.notifyDataSetChanged()

                break@loop
            }
        }

        // set sound icon
        updateNextPrayerUI(athanItems[position])

        // update background image
        updateBackground(nextPrayer, millisInFuture)

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
                val t = remaining/1000 // this is now in seconds
                val hours = t / (60 * 60)
                val minutes = (t - hours*60*60) / 60
                val seconds = (t - hours*60*60 - minutes*60)
                counterTV.text = "%02d:%02d:%02d".format(hours, minutes, seconds)

                // update background image 15, 30 minutes before nextPrayer
                when(remaining){
                    15 * 60L, 30 * 60L -> updateBackground(nextPrayer, remaining)
                }

            }
        }.start()
    }


    /** set the right background image at the right time **/
    private fun updateBackground(nextPrayer: PrayerTime, millisInFuture: Long){
        val now = System.currentTimeMillis()

        when(nextPrayer?.name){
            prayersNames[0] -> {
                // fajr is next in less than 30 minutes
                //TODO change first one to fajr_bg when desigend
                if(millisInFuture <= 30 * 60 * 1000L)
                    mainBg.setImageResource(R.drawable.isha_bg)
                else
                    mainBg.setImageResource(R.drawable.isha_bg)
            }
            prayersNames[1] -> {
                //TODO change second one to fajr_bg when desigend
                // shuruq is next in less than 15 minutes
                if(millisInFuture <= 15 * 60 * 1000)
                    mainBg.setImageResource(R.drawable.shuruq)
                else
                    mainBg.setImageResource(R.drawable.isha_bg)
            }
            prayersNames[2] -> {
                // dhohr is next & shuruq is less than 30 minutes ago
                if(now - prayers[1].timestamp <= 30 * 60 * 1000L)
                    mainBg.setImageResource(R.drawable.shuruq)
                else
                    mainBg.setImageResource(R.drawable.dhohr)
            }
            prayersNames[3] -> {
                // next is Asr in less than 30 minutes
                // TODO change first to asr_bg when ready
                if(millisInFuture <= 30 * 60 * 1000)
                    mainBg.setImageResource(R.drawable.dhohr)
                else
                    mainBg.setImageResource(R.drawable.dhohr)
            }
            prayersNames[4] -> {
                // next is maghrib in less than 30 minutes
                // TODO change second to asr_bg
                if(millisInFuture <= 30 * 60 * 1000)
                    mainBg.setImageResource(R.drawable.maghrib_bg)
                else
                    mainBg.setImageResource(R.drawable.dhohr)
            }
            prayersNames[5] -> {
                // next is Isha & Maghrib was less than 45 minutes ago
                if(now - prayers[4].timestamp <= 45 * 60 * 1000L)
                    mainBg.setImageResource(R.drawable.maghrib_bg)
                else
                    mainBg.setImageResource(R.drawable.isha_bg)
            }
        }

    }

    private fun updateNextPrayerUI(athanItem: AthanItem){
        when(athanItem.sound){
            SoundState.ON -> nextPrayerItem.findViewById<ImageView>(R.id.soundCtrl).setImageResource(R.drawable.ic_sound_on)
            SoundState.BEEP -> nextPrayerItem.findViewById<ImageView>(R.id.soundCtrl).setImageResource(R.drawable.ic_sound_beep)
            SoundState.OFF -> nextPrayerItem.findViewById<ImageView>(R.id.soundCtrl).setImageResource(R.drawable.ic_sound_off)
        }

        // update nextPrayerItem
        nextPrayerItem.findViewById<TextView>(R.id.prayerNameTV).text = athanItem.prayerName
        nextPrayerItem.findViewById<TextView>(R.id.prayerTimeTV).text = athanItem.prayerTime
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
                SoundState.ON -> convertView?.findViewById<ImageView>(R.id.soundCtrl)?.setImageResource(R.drawable.ic_sound_on)
                SoundState.OFF -> convertView?.findViewById<ImageView>(R.id.soundCtrl)?.setImageResource(R.drawable.ic_sound_off)
                SoundState.BEEP -> convertView?.findViewById<ImageView>(R.id.soundCtrl)?.setImageResource(R.drawable.ic_sound_beep)
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