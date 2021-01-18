package com.alterpat.athan.model

class UserConfig(
    var city : String = "Fontenay-aux-roses",
    var state : String = "Ile-de-France",
    var country : String = "FR",
    var lat : Double = 48.79330000,
    var lon : Double = 2.29270000,
    var timezone : Int = 1,
    var timeFormat : String = "24H",
    var method : Int = 4,
    var juristic : Int = 0,
    var high_latitude : Int = 1,
    var fajrAngle : Double = 18.5,
    var ishaAngle : Double = 17.0,
    var prayerAdjustment: List<Int> = listOf(0, 0, 0, 0, 0),
    var autoDetect : Boolean = false,
    var prayerAlert : Boolean = true,
    var athan : String = "Default"
) {



}