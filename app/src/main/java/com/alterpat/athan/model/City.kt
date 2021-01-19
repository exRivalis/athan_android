package com.alterpat.athan.model

class City (var lat:Double,
            var lon : Double,
            var name : String,
            var state : String,
            var country : String) {

    override fun toString(): String {
        return "$name ,$state ,$country"
    }
}