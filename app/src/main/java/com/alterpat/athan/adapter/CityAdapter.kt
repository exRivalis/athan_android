package com.alterpat.athan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alterpat.athan.R
import com.alterpat.athan.model.City

class CityAdapter(private val cities : ArrayList<City>) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    class CityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cityInfo: TextView = view.findViewById(R.id.cityInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.city_item, parent, false)

        return CityViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cities[position]
        val content = "${city.name}, ${city.state}, ${city.country}"
        holder.cityInfo.text = content
    }
}