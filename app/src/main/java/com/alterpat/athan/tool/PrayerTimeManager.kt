package com.alterpat.athan.tool

import android.icu.util.Calendar
import com.alterpat.athan.MainActivity
import com.alterpat.athan.PrayerTime

class PrayerTimeManager {
    companion object{
        fun getPrayers(latitude: Double, longitude: Double, timezone: Int): ArrayList<PrayerTime>{
            val prayerTimes : HashMap<String, String> = PrayTime.getPrayerTimes(latitude, longitude, timezone)

            var prayers = ArrayList<PrayerTime>()

            MainActivity.prayersNames.forEach {prayerName ->
                // create prayer timestamp
                val now = System.currentTimeMillis()
                val prayerTime = prayerTimes[prayerName]!!
                val hour = prayerTime.split(":")[0].toInt()
                val minutes = prayerTime.split(":")[1].toInt()
                val timestamp: Long = Calendar.getInstance().apply {
                    timeInMillis = now
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minutes)
                    set(Calendar.SECOND, 0)
                }.timeInMillis

                prayers.add(PrayerTime(prayerName, timestamp, prayerTime))
            }

            return prayers
        }
    }
}