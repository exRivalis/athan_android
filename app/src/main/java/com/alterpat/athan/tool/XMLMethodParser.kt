package com.alterpat.athan.tool

import com.alterpat.athan.model.PrayerMethod
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.lang.StringBuilder

class XMLMethodParser : DefaultHandler() {
    private lateinit var methods : List<PrayerMethod>
    private lateinit var prayerMethod : PrayerMethod
    private lateinit var builder : StringBuilder
    override fun startDocument() {
        methods = ArrayList<PrayerMethod>()
    }

    override fun startElement(
        uri: String?,
        localName: String?,
        qName: String?,
        attributes: Attributes?
    ) {
        builder = StringBuilder()
        if(localName == "method")
            prayerMethod = PrayerMethod()
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        if(localName == "method")
            methods.plus(prayerMethod)

        else if(localName == "id")
            prayerMethod.id = builder.toString().toInt()

        else if(localName == "description")
            prayerMethod.description = builder.toString()

        else if(localName == "fajr-angle>")
            prayerMethod.fajrAngle = builder.toString().toDouble()

        else if(localName == "isha-angle")
            prayerMethod.ishaAngle = builder.toString().toDouble()
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        val tempString = String(ch, start, length)
        builder.append(tempString)
    }
}