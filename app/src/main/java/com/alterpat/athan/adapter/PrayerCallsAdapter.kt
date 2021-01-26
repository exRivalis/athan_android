package com.alterpat.athan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alterpat.athan.R
import com.alterpat.athan.model.PrayerCallItem
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView

class PrayerCallsAdapter(private val context: Context, private var prayerCalls: ArrayList<PrayerCallItem>, var clickListener: PrayerCallClickListener) :
    RecyclerView.Adapter<PrayerCallsAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.athanName)
        val length: TextView = view.findViewById(R.id.athanLength)
        val playBtn: ImageButton = view.findViewById(R.id.playBtn)
        val selected : ImageView = view.findViewById(R.id.selected)

        fun start(prayerCall: PrayerCallItem, clickListener: PrayerCallClickListener){
            itemView.setOnClickListener{
                clickListener.onClick(prayerCall, adapterPosition)
            }

            playBtn.setOnClickListener {
                clickListener.onPlay(prayerCall, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.athan_call_item2, parent, false)

        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return prayerCalls.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = prayerCalls[position].athanName
        holder.length.text = prayerCalls[position].length

        holder.start(prayerCalls[position], clickListener)

        if(prayerCalls[position].isSelected){
            holder.name.setTextColor(context.getColor(R.color.background_color))
            holder.length.setTextColor(context.getColor(R.color.background_color))
            holder.selected.visibility = View.VISIBLE
        }else {
            holder.name.setTextColor(context.getColor(R.color.colorText))
            holder.length.setTextColor(context.getColor(R.color.colorText))
            holder.selected.visibility = View.GONE
        }

        if(prayerCalls[position].isPlaying){
            holder.playBtn.setImageResource(R.drawable.ic_stop)
            holder.name.setTextColor(context.getColor(R.color.colorPlay))
            holder.length.setTextColor(context.getColor(R.color.colorPlay))
        }else {
            holder.playBtn.setImageResource(R.drawable.ic_play)
            if(prayerCalls[position].isSelected){
                holder.name.setTextColor(context.getColor(R.color.background_color))
                holder.length.setTextColor(context.getColor(R.color.background_color))
            }else {
                holder.name.setTextColor(context.getColor(R.color.colorText))
                holder.length.setTextColor(context.getColor(R.color.colorText))
            }
        }

    }

}

interface PrayerCallClickListener {
    fun onClick(item: PrayerCallItem, position: Int)
    fun onPlay(item: PrayerCallItem, position: Int)
    //fun onLongClick(view: View?, position: Int)
}

