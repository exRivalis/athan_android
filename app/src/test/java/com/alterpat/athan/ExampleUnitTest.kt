package com.alterpat.athan

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}


/*
class SleepDatabaseTest {

    private lateinit var prayerDao: PrayerDao
    private lateinit var db: PrayerDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, PrayerDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        prayerDao = db.prayerDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNight() {
        val date = "2021-01-16"
        val prayer = Prayer(System.currentTimeMillis(), date, "08:56", "Fajr")
        prayerDao.insert(prayer)
        val prayerRes = prayerDao.loadByDay(date)
        assertEquals(prayerRes.get(0).date, date)
    }
}
 */
