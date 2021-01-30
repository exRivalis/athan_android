package com.alterpat.athan

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alterpat.athan.model.City
import com.alterpat.athan.model.UserConfig
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class SearchActivity : AppCompatActivity() {

    private val TAG = "SearchActivityTag"
    private lateinit var adapter : ArrayAdapter<City>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        /**
         * Save previously searched cities
         * and show them every time on start
         * then replace recycler view content with results on search
         */

        setSupportActionBar(toolbar)
        //supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        adapter = ArrayAdapter(this, R.layout.city_item)
        searchListView.adapter = adapter

        searchListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            /** update UserConf and return finish activity for result **/
            Toast.makeText(this, adapter.getItem(position).toString(), Toast.LENGTH_LONG).show()
            updateUserConf(adapter.getItem(position)!!)
            //finishActivity(Activity.RESULT_OK)
            setResult(Activity.RESULT_OK)
            finish()

        }

        searchListView.emptyView = emptyTextView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if(item.itemId ==android.R.id.home){
            onBackPressed();
        }

        return true
    }

    private fun updateUserConf(city : City){
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
        userConfig.city = city.name
        userConfig.country = city.country
        userConfig.state = city.state
        userConfig.lat = city.lat
        userConfig.lon = city.lon

        /** update shared prefs **/
        with (sharedPref.edit()) {
            putString("userConfig", gsonBuilder.toJson(userConfig))
            apply()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val search = menu?.findItem(R.id.nav_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search City"

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Toast.makeText(this, query, Toast.LENGTH_LONG).show()
                //adapter.add(City(0.9, 0.2, "Paris", "13:18", "France"))
                requestCities(query!!)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    /*
    fun queryCities(callback: (search:String) -> ArrayList<City>) {

        var format = "json"
        var addressDetails = 1 // or 0
        var nbResults = 8

        var baseUrl = "https://nominatim.openstreetmap.org/search?"
        var request = baseUrl +
                "q=$search" +
                "&format=$format"+
                "&addressdetails=$addressDetails"+
                "&limit=$nbResults"

        doAsync {
            val respJsonStr = URL(request).readText()
            var json : JSONObject
            var success = true

            try {
                json = JSONObject(respJsonStr)
                try {
                    json.getJSONObject("error")
                }
                catch (e: Exception){

                }
            }catch (e : Exception){

            }


            if (success) {
                val jsonArray = JSONArray(respJsonStr)

                for (i in 0 until jsonArray.length()){
                    val jsonCity = jsonArray.get(i) as JSONObject

                    val lat = jsonCity.getDouble("lat")
                    val lon = jsonCity.getDouble("lon")

                    val address = jsonCity.getJSONObject("address")
                    var cityName : String
                    if(address.has("city"))
                        cityName = address.getString("city")
                    else if(address.has("town"))
                        cityName = address.getString("town")
                    else
                        continue
                    Log.d(TAG, address.toString())

                    var state = address.getString("state")
                    var country = address.getString("country")
                    var city = City(lat, lon, cityName, state, country)

                    cities.add(city)
                }

                runOnUiThread{
                    mAdapter.notifyDataSetChanged()
                }

            } else {
                runOnUiThread {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.search_failed_msg),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }

            return cities
        }
    }

     */

    private fun requestCities( search:String){
        var format = "json"
        var addressDetails = 1 // or 0
        var nbResults = 14

        var baseUrl = "https://nominatim.openstreetmap.org/search?"
        var request = baseUrl +
                        "q=$search" +
                "&format=$format"+
                "&addressdetails=$addressDetails"+
                "&limit=$nbResults"

        adapter.clear()

        // show progress bar while searching
        progressIndicator.visibility = View.VISIBLE

        doAsync {
            // create header field to specify device language
            var conn = URL(request).openConnection()

            conn.setRequestProperty("Accept-Language", Locale.getDefault().language)

            val inputStream = conn.getInputStream()
            var bufferReader = BufferedReader(inputStream.reader())
            val respJsonStr = bufferReader.readText()

            //val respJsonStr = URL(request).readText()
            var json : JSONObject
            var success = true

            try {
                json = JSONObject(respJsonStr)
                try {
                    json.getJSONObject("error")
                }
                catch (e: Exception){

                }
            }catch (e : Exception){

            }


            if (success) {
                val jsonArray = JSONArray(respJsonStr)
                val addedNames = ArrayList<String>()

                runOnUiThread{
                    // Done -> hide progress bar while searching
                    progressIndicator.visibility = View.GONE
                }

                for (i in 0 until jsonArray.length()){
                    val jsonCity = jsonArray.get(i) as JSONObject

                    if(!jsonCity.has("class"))
                        continue

                    /*
                    if(jsonCity.getString("type") != "administrative"
                        && jsonCity.getString("type") != "city"
                        && jsonCity.getString("type") != "town")
                        continue
                     */

                    if(jsonCity.getString("class") != "boundary" && jsonCity.getString("class") != "place")
                        continue


                    val lat = jsonCity.getDouble("lat")
                    val lon = jsonCity.getDouble("lon")

                    val address = jsonCity.getJSONObject("address")
                    var cityName : String
                    if(address.has("city"))
                        cityName = address.getString("city")
                    else if(address.has("town"))
                        cityName = address.getString("town")
                    else
                        continue
                    Log.d(TAG, address.toString())

                    var state = address.getString("state")
                    var country = address.getString("country")
                    var city = City(lat, lon, cityName, state, country)

                    val cont = "$city$state$country"
                    val added = cont in addedNames
                    if(!added) {
                        addedNames.add(cont)
                        runOnUiThread{
                            adapter.add(city)
                        }
                    }


                }

            } else {
                runOnUiThread {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.search_failed_msg),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
    }
}