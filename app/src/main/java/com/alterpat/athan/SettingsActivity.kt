package com.alterpat.athan

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.alterpat.athan.model.UserConfig
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_settings2.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings2)

        setSupportActionBar(toolbar)
        //supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        searchLocation.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        calcMethod.setOnClickListener {
            startActivity(Intent(this, CalculationMethodActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun init(){
        /** load user conf **/
        val sharedPref = getSharedPreferences(
            getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
        var gsonBuilder: Gson = Gson()
        var jsonConf: String? = sharedPref.getString("userConfig", "")

        var userConfig : UserConfig

        if(jsonConf != "")
            userConfig = gsonBuilder.fromJson(jsonConf, UserConfig::class.java)
        else
            userConfig = UserConfig()

        locationName.text = userConfig.city
        juristicMethodName.text = userConfig.juristicName
        selectedAthanName.text = userConfig.athan
        calcMethodName.text = userConfig.methodName

        if(userConfig.fixed)
            calcMethodDescription.text = "Fajr: ${userConfig.fajrAngle}°, Isha: ${userConfig.ishaAngle} minutes"
        else
            calcMethodDescription.text = "Fajr: ${userConfig.fajrAngle}°, Isha: ${userConfig.ishaAngle}°"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if(item.itemId ==android.R.id.home){
            setResult(Activity.RESULT_OK)
            finish()
        }

        return true
    }
}