package com.alterpat.athan

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.alterpat.athan.model.UserConfig
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var userConfig : UserConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar)
        //supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        /** Search for a new location **/
        searchLocation.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        /** change calculation method **/
        calcMethod.setOnClickListener {
            startActivity(Intent(this, CalculationMethodActivity::class.java))
        }

        /** change Asr juristic method **/
        juristicMethod.setOnClickListener {
            showJuristicMethodDialog()
        }

        /** change prayer call**/
        athanSelection.setOnClickListener {
            startActivity(Intent(this, AthanSelectionActivity::class.java))
        }


        /** switch sound on/off **/
        prayerAlertSwitch.setOnClickListener{
            updateSoundOn(prayerAlertSwitch.isChecked)
        }
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun showJuristicMethodDialog(){
        val singleItems = arrayOf("Standard (Shafi'i, Hanbali, Maliki)", "Hanafi")
        val itemNames = arrayOf("Standard", "Hanafi")

        val checkedItem = if (userConfig.juristic == "Standard") 0 else 1

        var selectedJuristicDescription = userConfig.juristic
        var selectedJuristicName = userConfig.juristicName

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.juristic_method))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                // Respond to neutral button press

            }
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                // Respond to positive button press
                updateUserConf(selectedJuristicName, selectedJuristicDescription)
            }
            // Single-choice items (initialized with checked item)
            .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                // Respond to item chosen
                selectedJuristicName = itemNames[which]
                selectedJuristicDescription = singleItems[which]
            }
            .show()
    }

    private fun updateSoundOn(soundOn: Boolean){
        /** load userConf from shared prefs **/
        val sharedPref = getSharedPreferences(
            getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
        var gsonBuilder: Gson = Gson()

        /** update userConf **/
        userConfig.prayerAlert = soundOn

        /** update shared prefs **/
        with (sharedPref.edit()) {
            putString("userConfig", gsonBuilder.toJson(userConfig))
            apply()
        }
    }

    private fun updateUserConf(juristic : String, description: String){
        /** load userConf from shared prefs **/
        val sharedPref = getSharedPreferences(
            getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
        var gsonBuilder: Gson = Gson()

        /** update userConf **/
        userConfig.juristic = juristic
        userConfig.juristicName = description

        /** update shared prefs **/
        with (sharedPref.edit()) {
            putString("userConfig", gsonBuilder.toJson(userConfig))
            apply()
        }

        /*** update this activity **/
        juristicMethodName.text = description

    }

    private fun init(){
        /** load user conf **/
        val sharedPref = getSharedPreferences(
            getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
        var gsonBuilder: Gson = Gson()
        var jsonConf: String? = sharedPref.getString("userConfig", "")

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


        if(userConfig.prayerAlert)
            prayerAlertSwitch.isChecked = true

        if(userConfig.autoDetect)
            autoLocationSwitch.isChecked = true

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