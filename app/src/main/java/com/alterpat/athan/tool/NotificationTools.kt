package com.alterpat.athan.tool

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.core.app.NotificationCompat
import com.alterpat.athan.BuildConfig
import com.alterpat.athan.MainActivity
import com.alterpat.athan.R
import com.alterpat.athan.model.SoundState
import com.alterpat.athan.model.UserConfig
import com.google.gson.Gson
import java.lang.Exception

val TAG: String = "NotificationTool"
//val CHANNEL_ID = "athan_notification_channel"
val CHANNEL_ID_SILENT = "athan_notification_silent"
val CHANNEL_ID_BEEP = "athan_notification_beep"
val CHANNEL_NAME = "Athan Notification"
private val NOTIFICATION_ID = 0


// schedules a notification for firing at a specific day & time
fun scheduleNotification(context: Context, timestamp: Long, athan: String,  alarmId: Int, soundState: SoundState) {

    var soundStateInt = 1
    when(soundState){
        SoundState.ON -> soundStateInt = 1
        SoundState.OFF -> soundStateInt = -1
        SoundState.BEEP -> soundStateInt = 0
    }

    var alarmIntent = Intent(context, AthanReceiver::class.java).let { intent ->
        intent.action = "ATHAN_ALARM"
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        intent.putExtra("prayer", athan)
        intent.putExtra("soundStateInt", soundStateInt)
        PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    var alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    alarmMgr?.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        timestamp,
        alarmIntent
    )
}

// cancel an alarm
fun cancelScheduledNotification(context: Context, athan: String, alarmId: Int) {

    var alarmIntent = Intent(context, AthanReceiver::class.java).let { intent ->
        intent.action = "ATHAN_ALARM"
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        intent.putExtra("prayer", athan)
        PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    var alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmMgr.cancel(alarmIntent)
}

// create a notification channel to be able to fire notifications
fun createNotificationChannels(context: Context) {
    // create prayer call notification channel
    createNotificationChannel(context)
    // create alarm beep notification channel
    createNotificationChannelBeep(context)
    // create silent notification channel
    createNotificationChannelSilent(context)
}

fun createNotificationChannel(context: Context) {
    var mNotifyManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    /** load user conf **/
    val sharedPref = context.getSharedPreferences(
        context.getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
    var gsonBuilder: Gson = Gson()
    var jsonConf: String? = sharedPref.getString("userConfig", "")
    var userConfig : UserConfig
    if(jsonConf != "")
        userConfig = gsonBuilder.fromJson(jsonConf, UserConfig::class.java)
    else
        userConfig = UserConfig()

    val CHANNEL_ID = userConfig.CHANNEL_ID
    val CHANNEL_ID_OLD = userConfig.CHANNEL_ID_OLD

    // notification channels are only available in API 26 and higher
    // Create a NotificationChannel

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // delete previous notification channels
        //try {
        mNotifyManager.deleteNotificationChannel(CHANNEL_ID_OLD)
        Log.d(TAG, "channel deleted")
        //}catch (e: Exception){

        //}

        // recreate the notification channel
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH)

        notificationChannel.enableLights(true)
        //notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)

        // Creating an Audio Attribute
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()


        // load res id from userConf
        var uri = Uri.parse(
            "android.resource://"
                    + context.packageName
                    + "/"
                    + userConfig.athanRes)
        Log.d(TAG, "Notification channel recreated ${userConfig.athanRes}")
        /*
        var uri = Uri.parse(
            "android.resource://"
                    + context.packageName
                    + "/"
                    + R.raw.athan)
         */

        notificationChannel.setSound(uri, audioAttributes)
        notificationChannel.description = "Athan";

        mNotifyManager.createNotificationChannel(notificationChannel)
        Log.d(TAG, "channel created")


    } else {
        //TODO("VERSION.SDK_INT < O")
    }
}
fun createNotificationChannelSilent(context: Context) {
    var mNotifyManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // create the notification channel
        val notificationChannel = NotificationChannel(
            CHANNEL_ID_SILENT,
            CHANNEL_NAME + " Silent",
            NotificationManager.IMPORTANCE_HIGH)

        // prepare audio attributes
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        // prepare silent uri
        var uri = Uri.parse(
            "android.resource://"
                    + context.packageName
                    + "/"
                    + R.raw.silent)

        notificationChannel.enableLights(true)
        //notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Athan";
        notificationChannel.setSound(uri, audioAttributes)

        mNotifyManager.createNotificationChannel(notificationChannel)

    } else {
        //TODO("VERSION.SDK_INT < O")
    }
}
fun createNotificationChannelBeep(context: Context) {
    var mNotifyManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // create the notification channel
        val notificationChannel = NotificationChannel(
            CHANNEL_ID_BEEP,
            CHANNEL_NAME + " Alarm Beep",
            NotificationManager.IMPORTANCE_HIGH)

        notificationChannel.enableLights(true)
        //notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)

        // Creating an Audio Attribute
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)
        notificationChannel.description = "Athan";

        mNotifyManager.createNotificationChannel(notificationChannel)

    } else {
        //TODO("VERSION.SDK_INT < O")
    }
}

// fire a notification
fun fireNotification(context: Context, title: String, content: String, soundState: SoundState){
    /** load user conf **/
    val sharedPref = context.getSharedPreferences(
        context.getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
    var gsonBuilder: Gson = Gson()
    var jsonConf: String? = sharedPref.getString("userConfig", "")
    var userConfig : UserConfig
    if(jsonConf != "")
        userConfig = gsonBuilder.fromJson(jsonConf, UserConfig::class.java)
    else
        userConfig = UserConfig()

    if(userConfig.prayerAlert){
        /**  see whether it should be  silent, alarm or prayer call channel **/
        var CHANNEL_ID : String
        when(soundState){
            SoundState.ON -> CHANNEL_ID = userConfig.CHANNEL_ID
            SoundState.OFF -> CHANNEL_ID = CHANNEL_ID_SILENT
            SoundState.BEEP -> CHANNEL_ID = CHANNEL_ID_BEEP
        }

        val notificationPendingIntent = Intent(context, MainActivity::class.java).let { i ->
            PendingIntent.getActivity(
                context,
                NOTIFICATION_ID, i, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        var mNotifyManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())
        var notifyBuilder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_notification)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(false)

        Log.d(TAG, "${userConfig.athanRes}")
        mNotifyManager.notify(0, notifyBuilder.build())
    }

}

fun updateNotification(context: Context, prayerName: String, alarmId: Int, soundState: SoundState){
    /** load userConf from shared prefs **/
    val sharedPref = context.getSharedPreferences(
        context.getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
    var gsonBuilder: Gson = Gson()

    var jsonConf: String? = sharedPref.getString("userConfig", "")
    var userConfig : UserConfig
    if(jsonConf != "")
        userConfig = gsonBuilder.fromJson(jsonConf, UserConfig::class.java)
    else
        userConfig = UserConfig()
    var prayers = PrayerTimeManager.getPrayers(userConfig)
    val timestamp = prayers[alarmId].timestamp
    // cancel this alarm with the previous SoundState
    cancelScheduledNotification(context, prayerName, alarmId)
    // recreate it with the new SoundState
    scheduleNotification(context, timestamp, prayerName, alarmId, soundState)

}