package com.alterpat.athan.dao

import androidx.room.*
import java.util.*

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayers")
    fun getAll(): List<Prayer>

    @Query("SELECT * FROM prayers WHERE date  == :date")
    fun loadByDay(date: String): List<Prayer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(prayer: Prayer)

    @Delete
    fun delete(prayer: Prayer)

    @Query("DELETE FROM prayers")
    fun clean()

}