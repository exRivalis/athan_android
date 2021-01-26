package com.alterpat.athan.tool

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.util.Log
import androidx.core.app.NotificationCompat
import com.alterpat.athan.BuildConfig
import com.alterpat.athan.MainActivity
import com.alterpat.athan.R
import com.alterpat.athan.model.UserConfig
import com.google.gson.Gson

val CHANNEL_ID = "athan_notification_channel"
private val NOTIFICATION_ID = 0


// schedules a notification for firing at a specific day & time
fun scheduleNotification(context: Context, timestamp: Long, athan: String,  alarmId: Int) {

    var alarmIntent = Intent(context, AthanReceiver::class.java).let { intent ->
        intent.action = "ATHAN_ALARM"
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        intent.putExtra("prayer", athan)
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
fun cancelScheduledNotification(context: Context, timestamp: Long, athan: String, alarmId: Int) {

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
fun createNotificationChannel(context: Context) {
    var mNotifyManager = context.getSystemService(NotificationManager::class.java) as NotificationManager

    // notification channels are only available in API 26 and higher
    // Create a NotificationChannel

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "Athan Notification",
            NotificationManager.IMPORTANCE_HIGH)

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)

        // Creating an Audio Attribute
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

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

        // load res id from userConf
        var uri = Uri.parse(
            "android.resource://"
                    + context.packageName
                    + "/"
                    + userConfig.athanRes)

        notificationChannel.setSound(uri, audioAttributes)
        notificationChannel.description = "Athan";

        mNotifyManager!!.createNotificationChannel(notificationChannel);

    } else {
        TODO("VERSION.SDK_INT < O")
    }
}


// fire a notification
fun fireNotification(context: Context, title: String, content: String, soundOn: Boolean){
    val notificationPendingIntent = Intent(context, MainActivity::class.java).let { i ->
        PendingIntent.getActivity(
            context,
            NOTIFICATION_ID, i, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    var mNotifyManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())

    var uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.athan);

    var notifyBuilder = NotificationCompat.Builder(context!!, CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.mipmap.ic_notification)
        .setContentIntent(notificationPendingIntent)
        .setAutoCancel(false)
    notifyBuilder.setSound(uri, AudioManager.STREAM_MUSIC)

    /*
    if(soundOn)
        notifyBuilder.setSound(uri, AudioManager.STREAM_MUSIC)
    else
        notifyBuilder.setSound(null)
     */

    mNotifyManager.notify(0, notifyBuilder.build())
}