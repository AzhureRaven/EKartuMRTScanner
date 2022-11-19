package com.example.ekartumrtscanner

data class Rute(
    var id_rute:Int,
    var nama_rute:String,
    var ppm:Double,
    var status_rute:Int
){
    override fun toString(): String {
        return "$nama_rute"
    }
}
