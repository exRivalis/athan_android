package com.alterpat.athan.model

import android.content.Context
import androidx.cardview.widget.CardView
import com.alterpat.athan.R
import kotlinx.android.synthetic.main.athan_item.view.*

class AthanItem(context: Context, prayerName: String, prayerTime: String) : CardView(context) {
    init {
        inflate(context, R.layout.athan_item, this)
        prayerNameTV.text = prayerName
        prayerTimeTV.text = prayerTime
    }
}