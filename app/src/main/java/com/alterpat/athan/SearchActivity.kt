package com.alterpat.athan

import android.app.SearchManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.alterpat.athan.adapter.CityAdapter
import com.alterpat.athan.model.City
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.toolbar
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class SearchActivity : AppCompatActivity() {
    private lateinit var mAdapter : CityAdapter
    private lateinit var cities : ArrayList<City>

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


        cities = ArrayList()


        /*
        mAdapter = CityAdapter(cities)

        searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }
         */



        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        searchListView.adapter = adapter
        searchListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(this, "Search", Toast.LENGTH_LONG).show()
        }

        searchListView.emptyView = emptyTextView
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
                    runOnUiThread{
                        adapter.add(city)
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