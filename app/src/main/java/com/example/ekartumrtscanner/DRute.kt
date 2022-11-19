package com.example.ekartumrtscanner


data class DRute(
    var id_rute:Int,
    var id_stasiun:Int,
    var stasiun_ke:Int,
    var jarak_next:Int
){
    override fun toString(): String {
        return "$stasiun_ke. ${Koneksi.getStasiun(this)?.nama_stasiun}"
    }
}
