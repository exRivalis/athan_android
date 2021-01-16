package com.alterpat.athan.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*
    The following code defines an AppDatabase class to hold the database.
    AppDatabase defines the database configuration and serves as the app's
    main access point to the persisted data. The database class must satisfy the following conditions:

    The class must be annotated with a @Database annotation that includes an entities array
    that lists all of the data entities associated with the database.
    The class must be an abstract class that extends RoomDatabase.
    For each DAO class that is associated with the database, the database
    class must define an abstract method that has zero arguments and returns an instance of the DAO class.
 */

@Database(entities = [Prayer::class], version = 2, exportSchema = false)
abstract class PrayerDatabase : RoomDatabase() {
    abstract val prayerDao: PrayerDao

    companion object {
        @Volatile
        private var INSTANCE: PrayerDatabase? = null
        fun getInstance(context: Context): PrayerDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PrayerDatabase::class.java,
                        "athan_database")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}