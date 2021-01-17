package com.alterpat.athan.tool

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.alterpat.athan.dao.Prayer
import com.alterpat.athan.dao.PrayerDatabase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AthanReceiver : BroadcastReceiver() {

    private lateinit var context : Context
    val  TAG = "startuptest"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {

        this.context = context!!

        Log.d("startuptest", intent?.action!!)


        if (Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent?.getAction()) ||
            Intent.ACTION_BOOT_COMPLETED.equals(intent?.getAction())
            || "startUp" == intent?.action) {


            Log.d("startuptest", "StartUpBootReceiver BOOT_COMPLETED")

            //val serviceIntent = Intent(context, AthanService::class.java)
            //serviceIntent.putExtra("input", "Hello")

            //context.startForegroundService(serviceIntent)
            //context.createDeviceProtectedStorageContext().startForegroundService(serviceIntent)

            Log.d("startuptest", "Service called")


            // for the task to be executed witout risking being interrupted by system
            // we should use a service
            //AthanService.enqueueWork(context, serviceIntent)
            //Log.d("startuptest", "${prayers.size}")

            //createNotificationChannel(context)

            // get prayer for today
            // date in "yyyy MMM dd" format

            /*
            var now = System.currentTimeMillis()

            val df1 = SimpleDateFormat("yyyy-MM-dd")
            var dateStr = df1.format(now)

            var prayers : List<Prayer> = ArrayList()

            Log.d(TAG, "${prayers.size}")

            // request today's prayers
            val db = PrayerDatabase.getInstance(context)
            var prayerDao = db.prayerDao


            doAsync {
                prayers = prayerDao.loadByDay("2021-01-17")
                Log.d(TAG, "${prayers.size}")

                prayers = prayerDao.loadByDay("2021-01-17")

                    Log.d(TAG, "${prayers.size}")



                // schedule notifications
                prayers.forEach { prayer ->
                    if(prayer.timestamp > now){
                        Log.d(TAG, "${Date(prayer.timestamp)} ${prayer.name}")
                        scheduleNotifications(context, prayer.timestamp, prayer.name)
                    }
                }

            }

             */
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