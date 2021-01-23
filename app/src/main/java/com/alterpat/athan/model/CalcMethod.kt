package com.alterpat.athan.model

data class CalcMethod
    (
    val id: Int,
    val name: String,
    val description: String,
    val fixed : Boolean = false,
    val fajrAngle: Double,
    val ishaAngle: Double,
    var isSelected : Boolean = false){

    override
    fun toString() : String{
        return description
    }

    fun getInfo() : String {
        if(fixed)
            return "Fajr: $fajrAngle°, Isha: $ishaAngle Minutes"

        return "Fajr: $fajrAngle°, Isha: $ishaAngle°"

    }
}

