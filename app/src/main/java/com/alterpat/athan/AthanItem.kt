package com.alterpat.athan

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.athan_item.view.*

class AthanItem(context: Context, prayerName: String, prayerTime: String) : CardView(context) {
    init {
        inflate(context, R.layout.athan_item, this)
        prayerNameTV.text = prayerName
        prayerTimeTV.text = prayerTime
    }
}