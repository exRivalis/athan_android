package com.alterpat.athan.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "prayers")
data class Prayer(
    @PrimaryKey @ColumnInfo(name = "timestamp") val timestamp : Long,
    @ColumnInfo(name = "date") val date : String,
    @ColumnInfo(name = "time") val time : String,
    @ColumnInfo(name = "name") val name : String
    )