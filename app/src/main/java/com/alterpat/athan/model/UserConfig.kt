package com.alterpat.athan.model

import com.alterpat.athan.R

class UserConfig(
    var city : String = "Fontenay-aux-roses",
    var state : String = "Ile-de-France",
    var country : String = "FR",
    var lat : Double = 48.79330000,
    var lon : Double = 2.29270000,
    var timezone : Int = 1,
    var timeFormat : String = "24H",
    var methodName : String = "Makkah - Umm Al-Qura",
    var method : String = "Makkah",
    var juristicName : String = "Standard (Shafi'i, Hanbali, Maliki)",
    var juristic : String = "Standard",
    var high_latitude : Int = 1,
    var fajrAngle : Double = 18.5,
    var ishaAngle : Double = 90.0,
    var fixed : Boolean = true,
    var prayerAdjustment: List<Int> = listOf(0, 0, 0, 0, 0, 0),
    var prayerSoundCtl: List<SoundState> = listOf(SoundState.ON, SoundState.BEEP, SoundState.ON, SoundState.ON, SoundState.ON, SoundState.ON),
    var autoDetect : Boolean = false,
    var prayerAlert : Boolean = true,
    var athan : String = "Default",
    var athanRes : Int = R.raw.al_hossaini,
    var CHANNEL_ID : String = "athan_notification_channel",
    var CHANNEL_ID_OLD : String = "athan_notification_channel"

) {



}