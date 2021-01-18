package com.alterpat.athan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.alterpat.athan.adapter.CityAdapter
import com.alterpat.athan.model.City
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class SearchActivity : AppCompatActivity() {
    private lateinit var mAdapter : CityAdapter
    private lateinit var cities : ArrayList<City>

    private val TAG = "SearchActivityTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        /**
         * Save previously searched cities
         * and show them every time on start
         * then replace recycler view content with results on search
         */

        cities = ArrayList()
        mAdapter = CityAdapter(cities)

        searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

        requestCities("Paris")

    }

    private fun requestCities(search:String) {
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
        }
    }
}