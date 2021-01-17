package com.alterpat.athan.tool

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AthanService : Service() {
    //private lateinit var context: Context
    val TAG = "STARTUP_BOOT_ATHAN"

    override fun onCreate() {
        super.onCreate()

    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
    /*
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val msg = intent?.getStringExtra("input")
        Log.d(TAG, "Started Service")


        var now = System.currentTimeMillis()

        val df1 = SimpleDateFormat("yyyy-MM-dd")
        var dateStr = df1.format(now)

        var prayers : List<Prayer> = ArrayList()

        Log.d(TAG, "${prayers.size}")

        // request today's prayers
        val db = PrayerDatabase.getInstance(baseContext)
        var prayerDao = db.prayerDao

        prayers = prayerDao.loadByDay("2021-01-17")
        Log.d(TAG, "${prayers.size}")


        doAsync {
            prayers = prayerDao.loadByDay("2021-01-17")

            runOnUiThread {
                Log.d(TAG, "${prayers.size}")
            }


            // schedule notifications
            prayers.forEach { prayer ->
                if(prayer.timestamp > now){
                    Log.d(TAG, "${Date(prayer.timestamp)} ${prayer.name}")
                    scheduleNotifications(baseContext, prayer.timestamp, prayer.name)
                }
            }

            stopSelf()
        }
        return START_NOT_STICKY
    }


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    /*
    companion object {
        fun enqueueWork(context: Context?, intent: Intent?) {
            Log.d("startuptest", "enqueued")

            enqueueWork(context, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        var now = System.currentTimeMillis()

        val df1 = SimpleDateFormat("yyyy-MM-dd")
        var dateStr = df1.format(now)

        var prayers : List<Prayer> = ArrayList()

        Log.d("startuptest", "${prayers.size}")

        // request today's prayers
        val db = PrayerDatabase.getInstance(context)
        var prayerDao = db.prayerDao



        prayers = prayerDao.loadByDay("2021-01-17")

        Log.d("startuptest", "${prayers.size}")


        // schedule notifications
        prayers.forEach { prayer ->
            if(prayer.timestamp > now){
                Log.d("startuptest", "${Date(prayer.timestamp)} ${prayer.name}")
                scheduleNotifications(context, prayer.timestamp, prayer.name)
            }
        }


    }

     */

*/

}