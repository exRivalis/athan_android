package com.alterpat.athan.tool

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.SystemClock
import android.util.Log
import com.alterpat.athan.AthanItem
import com.alterpat.athan.dao.PrayerDatabase
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import java.util.*
import kotlin.math.abs


class AthanReceiver : BroadcastReceiver() {

    private lateinit var context : Context

    override fun onReceive(context: Context?, intent: Intent?) {

        this.context = context!!

        if (Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent?.getAction())) {
            Log.d("startuptest", "StartUpBootReceiver BOOT_COMPLETED")
            createNotificationChannel(context)

            // get prayer for today
            // date in "yyyy MMM dd" format
            var now = System.currentTimeMillis()

            val df1 = SimpleDateFormat("yyyy-MM-dd")
            var dateStr = df1.format(now)

            // request today's prayers
            val db = PrayerDatabase.getInstance(context)
            var prayerDao = db.prayerDao
            doAsync {
                var prayers = prayerDao.loadByDay(dateStr)
                // schedule notifications
                prayers.forEach { prayer ->
                    if(prayer.timestamp > now){
                        scheduleNotifications(context, prayer.timestamp, prayer.name)
                    }
                }
            }
        }
        /*

        if(intent?.action == "android.intent.action.BOOT_COMPLETED"){
            ////// reset your alrarms here
            val serviceIntent = Intent(context, AthanService::class.java)
            context.startService(serviceIntent)
            //scheduleNotifications(context, 1, 16, 10, 17, "Test2")

        }

        if (intent?.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            ////// reset your alrarms here
            fireNotification(context, "Alarm", "start", false)

        }else if(intent?.action.equals("ATHAN_ALARM")){
            Toast.makeText(context, intent?.action, Toast.LENGTH_LONG).show()
            var prayer = intent?.getStringExtra("prayer")
            var content = "C'est l'heure de la prière du $prayer"
            fireNotification(context, "Athan", content, true)
        }
        */
    }



}