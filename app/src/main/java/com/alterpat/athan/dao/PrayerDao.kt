package com.alterpat.athan.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.*

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayers")
    fun getAll(): List<Prayer>

    @Query("SELECT * FROM prayers WHERE date  == :date")
    fun loadAllByDay(date: String): List<Prayer>

    @Insert
    fun insertAll(vararg prayers: Prayer)

    @Delete
    fun delete(prayer: Prayer)

}