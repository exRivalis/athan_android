package com.alterpat.athan.model

import java.util.*
import kotlin.collections.ArrayList


class PrayerTime (val name: String, val timestamp: Long, val timeStr: String){
}


/****
class PrayTime2() {
    companion object {
        fun calculationMethod (id: Int) : String {
            var res = "Makkah"
            when(id){
                0 -> res = "Jafari"
                1 -> res = "Karachi"
                2 -> res = "ISNA"
                3 -> res = "MWL"
                4 -> res = "Makkah"
                5 -> res = "Egypt"
                6 -> res = "Tehran"
                7 -> res = "UOIF"
            }

            return res
        }

        fun juristicMethod(id : Int) : String {
            var res = "Shafi'i (Standard)"
            when(id){
                0 -> res = "Shafi'i, Hanbali, Maliki (Standard)"
                1 -> res = "Hanafi"
            }
            return res
        }
    }


    /**
    // Adjusting Methods for Higher Latitudes
    0: No adjustment
    1: middle of night
    2: 1/7th of night
    3: angle/60th of night

    // Time Formats
    0: 24-hour format
    1: 12-hour format
    2: 12-hour format with no suffix
    3: floating point number
    **/

    lateinit var timeNames : ArrayList<String>
    var numIterations = 1
    var offsets : IntArray = IntArray(7)

    fun PrayTime() {
        // Time Names
        timeNames = ArrayList<String>()
        timeNames.add("Fajr")
        timeNames.add("Sunrise")
        timeNames.add("Dhuhr")
        timeNames.add("Asr")
        timeNames.add("Sunset")
        timeNames.add("Maghrib")
        timeNames.add("Isha")

        // --------------------- Technical Settings --------------------
        numIterations = 1 // number of iterations needed to compute
        // times

        // ------------------- Calc Method Parameters --------------------

        // Tuning offsets {fajr, sunrise, dhuhr, asr, sunset, maghrib, isha}
        offsets[0] = 0
        offsets[1] = 0
        offsets[2] = 0
        offsets[3] = 0
        offsets[4] = 0
        offsets[5] = 0
        offsets[6] = 0

        /*
         *
         * fa : fajr angle ms : maghrib selector (0 = angle; 1 = minutes after
         * sunset) mv : maghrib parameter value (in angle or minutes) is : isha
         * selector (0 = angle; 1 = minutes after maghrib) iv : isha parameter
         * value (in angle or minutes)
         */
        //methodParams = HashMap<Int, DoubleArray>()
    }

    // ---------------------- Julian Date Functions -----------------------
    // calculate julian date from a calendar date
    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var year = year
        var month = month
        if (month <= 2) {
            year -= 1
            month += 12
        }
        val A = Math.floor(year / 100.0)
        val B = 2 - A + Math.floor(A / 4.0)
        return (Math.floor(365.25 * (year + 4716))
                + Math.floor(30.6001 * (month + 1)) + day + B) - 1524.5
    }

    // return prayer times for a given date
    private fun getPrayerTimes(
        date: Calendar, latitude: Double,
        longitude: Double, tZone: Double
    ): java.util.ArrayList<String?>? {
        val year = date[Calendar.YEAR]
        val month = date[Calendar.MONTH]
        val day = date[Calendar.DATE]

        var jDate = julianDate(year, month+1, day)
        val lonDiff = longitude / (15.0 * 24.0)
        jDate -= lonDiff

        // compute prayer times at given julian date
        var times: DoubleArray? =
            doubleArrayOf(5.0, 6.0, 12.0, 13.0, 18.0, 18.0, 18.0) // default times


        for (i in 1..this.numIterations) {
            times = computeTimes(times)
        }

        times = adjustTimes(times)
        times = tuneTimes(times)
        return computeDayTimes()
    }
    // Test Prayer times here
    var prayers = PrayTime()
}
        ****/