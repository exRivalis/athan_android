package com.alterpat.athan.model

class PrayerMethod(var id:Int,
                   var description:String,
                   var fajrAngle: Double,
                   var ishaAngle: Double) {

    constructor() : this(0, "", 0.0, 0.0) {

    }

}