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
            // if already checked ask for confirmation
            if(!prayerAlertSwitch.isChecked){
                MaterialAlertDialogBuilder(this)
                    .setTitle("Disable Prayer Alerts?")
                    .setMessage("You will no longer receive prayer alerts from Athan")
                    .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                        // Respond to negative button press
                        prayerAlertSwitch.isChecked = true
                    }
                    .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                        // Respond to positive button press
                        updateSoundOn(prayerAlertSwitch.isChecked)
                    }
                    .show()
            }
            else{
                // switch it on
                updateSoundOn(prayerAlertSwitch.isChecked)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun showJuristicMethodDialog(){
        val itemsDescriptions = arrayOf("Standard (Shafi'i, Hanbali, Maliki)", "Hanafi")
        val itemsMethodNames = arrayOf("Standard", "Hanafi")

        val checkedItem = if (userConfig.juristicMethod == "Standard") 0 else 1

        var selectedJuristicMethod = userConfig.juristicMethod
        var selectedJuristicDescription = userConfig.juristicDescription

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.juristic_method))
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                // Respond to neutral button press

            }
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                // Respond to positive button press
                updateUserConf(selectedJuristicDescription, selectedJuristicMethod)
            }
            // Single-choice items (initialized with checked item)
            .setSingleChoiceItems(itemsDescriptions, checkedItem) { dialog, which ->
                // Respond to item chosen
                selectedJuristicDescription = itemsDescriptions[which]
                selectedJuristicMethod = itemsMethodNames[which]
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

    private fun updateUserConf(description : String, method: String){
        /** load userConf from shared prefs **/
        val sharedPref = getSharedPreferences(
            getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
        var gsonBuilder: Gson = Gson()

        /** update userConf **/
        userConfig.juristicDescription = description
        userConfig.juristicMethod = method

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
        juristicMethodName.text = userConfig.juristicDescription
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
            onBackPressed()
        }

        return true
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}