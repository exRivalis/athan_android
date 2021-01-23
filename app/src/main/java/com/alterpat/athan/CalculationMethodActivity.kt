package com.alterpat.athan

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.alterpat.athan.adapter.MethodClickListener
import com.alterpat.athan.adapter.MethodsAdapter
import com.alterpat.athan.model.CalcMethod
import com.alterpat.athan.model.UserConfig
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_calculation_method.*
import kotlinx.android.synthetic.main.activity_search.toolbar
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class CalculationMethodActivity : AppCompatActivity(), MethodClickListener {
    private val TAG = "CalcMethods"
    private lateinit var calcMethods : ArrayList<CalcMethod>
    private lateinit var mAdapter : MethodsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculation_method)

        setSupportActionBar(toolbar)
        //supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        /** load userConf from shared prefs **/
        val sharedPref = getSharedPreferences(
            getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
        var gsonBuilder: Gson = Gson()
        var jsonConf: String? = sharedPref.getString("userConfig", "")

        var userConfig : UserConfig

        if(jsonConf != "")
            userConfig = gsonBuilder.fromJson(jsonConf, UserConfig::class.java)
        else
            userConfig = UserConfig()

        calcMethods = ArrayList()
        mAdapter = MethodsAdapter(this, calcMethods, this)
        methodRecyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }

        /** Read standard calculation methods from raw folder **/
        val inputReader = resources.openRawResource(R.raw.calculation_methods)
        val reader = BufferedReader(InputStreamReader(inputReader))
        var jsonString = reader.readText()

        var json = JSONArray(jsonString)

        for(i  in 0 until json.length()){
            val calc = gsonBuilder.fromJson(json.getJSONObject(i).toString(), CalcMethod::class.java)
            //Log.d(TAG, calc.description)
            if(calc.name == userConfig.method)
                calc.isSelected = true

            calcMethods.add(calc)
        }

        calcMethods.add(CalcMethod(99, "Custom","Custom", false,0.0, 0.0))

        mAdapter.notifyDataSetChanged()



    }

    // handle return toolbar's button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if(item.itemId ==android.R.id.home){
            onBackPressed();
        }

        return true
    }

    override fun onClick(item: CalcMethod, position: Int) {
        Toast.makeText(baseContext, item.description, Toast.LENGTH_SHORT).show()
        calcMethods.forEach {
            it.isSelected = false
        }
        calcMethods[position].isSelected = true
        mAdapter.notifyDataSetChanged()

        /** update userConf **/
        updateUserConf(item)
    }

    private fun updateUserConf(method : CalcMethod){
        /** load userConf from shared prefs **/
        val sharedPref = getSharedPreferences(
            getString(R.string.athan_prefs_key), Context.MODE_PRIVATE)
        var gsonBuilder: Gson = Gson()
        var json: String? = sharedPref.getString("userConfig", "")

        var userConfig : UserConfig

        if(json != "")
            userConfig = gsonBuilder.fromJson(json, UserConfig::class.java)
        else
            userConfig = UserConfig()

        /** update userConf **/
        userConfig.methodName = method.description
        userConfig.method = method.name
        userConfig.fajrAngle = method.fajrAngle
        userConfig.ishaAngle = method.ishaAngle
        userConfig.fixed = method.fixed

        /** update shared prefs **/
        with (sharedPref.edit()) {
            putString("userConfig", gsonBuilder.toJson(userConfig))
            apply()
        }

    }
}