package com.example.ekartumrtscanner

import android.os.Parcelable

data class ETiket(
    var id_tiket:Int,
    var id_kartu:Int,
    var id_stasiun_awal:Int,
    var id_stasiun_akhir:Int?,
    var id_rute:Int,
    var harga:Double?,
    var tgl_cetak:String,
    var tgl_masuk:String?,
    var tgl_keluar:String?,
    var mode_tiket:Int,
    var status_tiket:Int
){
}