package com.alterpat.athan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.alterpat.athan.tool.scheduleNotifications


class AthanReceiver : BroadcastReceiver() {

    private lateinit var context : Context

    override fun onReceive(context: Context?, intent: Intent?) {

        this.context = context!!

        scheduleNotifications(context, 1, 16, 10, 17, "Test2")

        /*

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