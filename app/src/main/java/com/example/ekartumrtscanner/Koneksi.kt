package com.example.ekartumrtscanner

import android.graphics.Bitmap
import android.graphics.Color
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.DriverManager
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


object Koneksi {

    lateinit var koneksi: Connection
    fun startConnection(){
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
            Class.forName("org.mariadb.jdbc.Driver")
            //Class.forName("com.mysql.jdbc.Driver");
            val ip = IP.getIP() //pake ip laptop sekarang yg ipv4, cari di cmd ipconfig //copy IP.kt di discord, paste ke project sendiri, di gitignore itu biar gk tabrakan ip address masing-masing laptop
            //xampp kemungkinan juga perlu di run as administrator untuk bekerja
            //val ip = "localhost"
            val jdbcUrl = "jdbc:mariadb://$ip:3306/e_kartu_mrt"
            try {
                val connection = DriverManager.getConnection(jdbcUrl, "root", "")
                println(connection.isValid(0))
                println("Bekerja")

                //briefly cara pakenya
                /*val query = connection.prepareStatement("SELECT * FROM rute")
                val res = query.execute()//general purpose, return bool kalau berhasil
                val res2 = query.executeUpdate()//INSERT UPDATE DELETE, return int
                val result = query.executeQuery()//select

                while(result.next()){

                    val id = result.getInt("id_rute")

                    val name = result.getString("nama_rute")
                    println("$id $name")
                }*/

                koneksi = connection
            }
            catch (e:Exception){
                println(e)
            }
            koneksi = DriverManager.getConnection(jdbcUrl, "root", "")

    }

    fun getConnection():Connection{
        return koneksi
    }

    //helper
    fun getDate(dateTime: String, field: String = "dd MMMM yyyy", dateFormat: String = "yyyy-MM-dd HH:mm:ss"): String? {
        val input = SimpleDateFormat(dateFormat)
        val output = SimpleDateFormat(field)
        try {
            val getAbbreviate = input.parse(dateTime)    // parse input
            return output.format(getAbbreviate)    // format output
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    fun Int.toRupiah():String{
        // digunakan untuk mengubah format angka menjadi format uang rupiah
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("in","ID"))
        return numberFormat.format(this)
    }

    //EKartu
    fun getEKartu(id_kartu:Int): EKartu?{
        val query = getConnection().prepareStatement("select * from e_kartu where id_kartu = ${id_kartu} and status_kartu = 1")
        val result = query.executeQuery()
        var eKartu: EKartu? = null
        while(result.next()){
            eKartu = EKartu(result.getInt("id_kartu"),
                result.getString("nama_lengkap"),
                result.getString("username"),
                result.getString("password"),
                result.getString("email"),
                result.getString("tgl_lahir"),
                result.getString("kelamin"),
                result.getString("tgl_register"),
                result.getDouble("saldo"),
                result.getInt("status_kartu")
            )
        }
        return eKartu
    }

    fun tambahSaldo(eKartu: EKartu,saldo: Int){
        val query = getConnection().prepareStatement("update e_kartu set saldo = saldo + ? where id_kartu = ?")
        query.setInt(1,saldo)
        query.setInt(2,eKartu.id_kartu)
        val result = query.executeUpdate()
    }

    //ETiket
    fun getETiket(id_tiket:Int): ETiket?{
        val query = getConnection().prepareStatement("SELECT * from e_tiket where id_tiket = ${id_tiket}")
        val result = query.executeQuery()
        var eTiket:ETiket? = null
        while(result.next()){
            eTiket = ETiket(
                result.getInt("id_tiket"),
                result.getInt("id_kartu"),
                result.getInt("id_stasiun_awal"),
                result.getInt("id_stasiun_akhir"),
                result.getInt("id_rute"),
                result.getDouble("harga"),
                result.getString("tgl_cetak"),
                result.getString("tgl_masuk"),
                result.getString("tgl_keluar"),
                result.getInt("mode_tiket"),
                result.getInt("status_tiket")
            )
        }
        return eTiket
    }

    fun getETiketBaru(eKartu: EKartu): ETiket?{
        val query = getConnection().prepareStatement("SELECT * from e_tiket where id_kartu = ${eKartu.id_kartu} and id_stasiun_akhir is null")
        val result = query.executeQuery()
        var eTiket:ETiket? = null
        while(result.next()){
            eTiket = ETiket(
                result.getInt("id_tiket"),
                result.getInt("id_kartu"),
                result.getInt("id_stasiun_awal"),
                result.getInt("id_stasiun_akhir"),
                result.getInt("id_rute"),
                result.getDouble("harga"),
                result.getString("tgl_cetak"),
                result.getString("tgl_masuk"),
                result.getString("tgl_keluar"),
                result.getInt("mode_tiket"),
                result.getInt("status_tiket")
            )
        }
        return eTiket
    }

    fun ETiketMasuk(eTiket: ETiket){
        val query = getConnection().prepareStatement("update e_tiket set status_tiket = 3, tgl_masuk = now() where id_tiket = ${eTiket.id_tiket}")
        val result = query.executeUpdate()
    }
    fun ETiketKeluar(eTiket: ETiket){
        val query = getConnection().prepareStatement("update e_tiket set status_tiket = 0, tgl_keluar = now() where id_tiket = ${eTiket.id_tiket}")
        val result = query.executeUpdate()
    }
    fun ETiketKeluarEkartu(eTiket: ETiket){
        val query = getConnection().prepareStatement("update e_tiket set status_tiket = 0, tgl_keluar = now(), harga = ${eTiket.harga}, id_stasiun_akhir = ${eTiket.id_stasiun_akhir} where id_tiket = ${eTiket.id_tiket}")
        val result = query.executeUpdate()
    }

    fun checkETiketMasuk(eKartu: EKartu):ETiket?{
        val query = getConnection().prepareStatement("SELECT * from e_tiket where id_kartu = ${eKartu.id_kartu} and status_tiket = 3")
        val result = query.executeQuery()
        var eTiket:ETiket? = null
        while(result.next()){
            eTiket = ETiket(
                result.getInt("id_tiket"),
                result.getInt("id_kartu"),
                result.getInt("id_stasiun_awal"),
                result.getInt("id_stasiun_akhir"),
                result.getInt("id_rute"),
                result.getDouble("harga"),
                result.getString("tgl_cetak"),
                result.getString("tgl_masuk"),
                result.getString("tgl_keluar"),
                result.getInt("mode_tiket"),
                result.getInt("status_tiket")
            )
        }
        return eTiket
    }


    fun insertETiketEKartuMasuk(eTiket: ETiket){
        val query = getConnection().prepareStatement("insert into e_tiket (id_kartu,id_stasiun_awal,id_rute,mode_tiket) " +
                "values ('${eTiket.id_kartu}','${eTiket.id_stasiun_awal}','${eTiket.id_rute}','${eTiket.mode_tiket}')")
        val result = query.executeUpdate()
    }

    //Rute
    fun getRutes(): ArrayList<Rute>{
        val query = getConnection().prepareStatement("select * from rute where status_rute != 2")
        val result = query.executeQuery()
        val rutes = ArrayList<Rute>()
        while(result.next()){
            val rute = Rute(result.getInt("id_rute"),
                result.getString("nama_rute"),
                result.getDouble("ppm"),
                result.getInt("status_rute")
            )
            rutes.add(rute)
        }
        return rutes
    }

    //Stasiun
    fun getStasiun(dRute: DRute): Stasiun?{
        val query = getConnection().prepareStatement("select * from stasiun where id_stasiun = ${dRute.id_stasiun}")
        val result = query.executeQuery()
        var stasiun: Stasiun? = null
        while(result.next()){
            stasiun = Stasiun(result.getInt("id_stasiun"),
                result.getString("alamat"),
                result.getString("nama_stasiun"),
                result.getInt("status_stasiun")
            )
        }
        return stasiun
    }

    //DRute
    fun getDRutes(rute:Rute): ArrayList<DRute>{
        val query = getConnection().prepareStatement("select * from drute where id_rute = ${rute.id_rute} order by stasiun_ke asc")
        val result = query.executeQuery()
        val drutes = ArrayList<DRute>()
        while(result.next()){
            val drute = DRute(result.getInt("id_rute"),
                result.getInt("id_stasiun"),
                result.getInt("stasiun_ke"),
                result.getInt("jarak_next"),
            )
            drutes.add(drute)
        }
        return drutes
    }

    fun getDRuteAwal(eTiket: ETiket): DRute?{
        val query = getConnection().prepareStatement("select * from drute where id_rute = ${eTiket.id_rute} and id_stasiun = ${eTiket.id_stasiun_awal}")
        val result = query.executeQuery()
        var drute:DRute? = null
        while(result.next()){
            drute = DRute(result.getInt("id_rute"),
                result.getInt("id_stasiun"),
                result.getInt("stasiun_ke"),
                result.getInt("jarak_next"),
            )
        }
        return drute
    }
}