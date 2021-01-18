package com.alterpat.athan.tool

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.alterpat.athan.MainActivity.Companion.prayersNames
import com.alterpat.athan.R
import com.alterpat.athan.model.UserConfig
import com.google.gson.Gson


class AthanReceiver : BroadcastReceiver() {

    private lateinit var context : Context
    val  TAG = "startuptest"

    override fun onReceive(context: Context?, intent: Intent?) {

        this.context = context!!

        if (Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent?.getAction()) ||
            Intent.ACTION_BOOT_COMPLETED.equals(intent?.getAction())) {

            Log.d(TAG, "StartUpBootReceiver BOOT_COMPLETED")
            /** read user configs and set remaining alarms at boot **/
            //createAlarms()
            //val res = FileManager.read()
        }

        // fire athan notification
        if(intent?.action.equals("ATHAN_ALARM")){
            var prayer = intent?.getStringExtra("prayer")
            var title = "C'est l'heure de la prière du $prayer"
            var content = "Ne remets pas la prière à plus tard!"
            fireNotification(context, title, content, true)
        }
    }

    private fun createAlarms(){
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.athanSharedPrefs), Context.MODE_PRIVATE
        )
        var gsonBuilder: Gson = Gson()
        var json: String? = sharedPref.getString(context.getString(R.string.userConfig), "")

        var userConfig : UserConfig

        if(json != "")
            userConfig = gsonBuilder.fromJson(json, UserConfig::class.java)
        else
            userConfig = UserConfig()

        val latitude = userConfig.lat
        var longitude = userConfig.lon
        val timezone = userConfig.timezone

        val prayerTimes : HashMap<String, String> = PrayTime.getPrayerTimes(latitude, longitude, timezone)

        Log.d(TAG, userConfig.city)

        prayersNames.forEach {prayerName ->
            prayerTimes.get(prayerName)!!

        }
    }



}