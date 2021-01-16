package com.alterpat.athan.tool

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class AthanReceiver : BroadcastReceiver() {

    private lateinit var context : Context

    override fun onReceive(context: Context?, intent: Intent?) {

        this.context = context!!


        Log.d("startuptest", "StartUpBootReceiver BOOT_COMPLETED outside");
        fireNotification(context, "Athan", "Test", false)


        if (Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent?.getAction())) {
            Log.d("startuptest", "StartUpBootReceiver BOOT_COMPLETED")
            //scheduleNotifications(context, 1, 16, 10, 17, "Test2")
            createNotificationChannel(context)
            fireNotification(context, "Athan", "Test", false)
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