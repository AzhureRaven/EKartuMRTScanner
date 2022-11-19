package com.example.ekartumrtscanner

import android.os.Parcelable

data class EKartu(
    var id_kartu:Int,
    var nama_lengkap:String,
    var username:String,
    var password:String,
    var email:String,
    var tgl_lahir:String,
    var kelamin:String,
    var tgl_register:String,
    var saldo:Double,
    var status_kartu:Int
) {
    override fun toString(): String {
        return "EKartu(id_kartu=$id_kartu, nama_lengkap='$nama_lengkap', username='$username', password='$password', email='$email', tgl_lahir='$tgl_lahir', kelamin='$kelamin', tgl_register='$tgl_register', saldo=$saldo, status_kartu=$status_kartu)"
    }
}