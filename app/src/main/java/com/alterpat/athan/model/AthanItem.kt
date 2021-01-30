package com.alterpat.athan.model

import android.content.Context
import androidx.cardview.widget.CardView
import com.alterpat.athan.R
import kotlinx.android.synthetic.main.athan_item.view.*
import java.io.Serializable

class AthanItem(var prayerName: String, var prayerTime: String, var sound: SoundState = SoundState.ON, var selected: Boolean = false) : Serializable{
    init {
        //inflate(context, R.layout.athan_item, this)
        //prayerNameTV.text = prayerName
        //prayerTimeTV.text = prayerTime
    }
}

enum class SoundState {
    ON,
    OFF,
    BEEP
}