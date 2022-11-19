package com.example.ekartumrtscanner

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.ekartumrtscanner.Koneksi.getStasiunAwal
import com.example.ekartumrtscanner.Koneksi.toRupiah
import com.example.ekartumrtscanner.databinding.ActivityMainBinding
import org.json.JSONObject
import org.json.JSONTokener

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var codeScanner: CodeScanner
    lateinit var druteAdapter: ArrayAdapter<DRute>
    lateinit var drutes: ArrayList<DRute>
    lateinit var ruteAdapter: ArrayAdapter<Rute>
    lateinit var rutes: ArrayList<Rute>
    var rute = -1
    var drute = -1
    var stasiun: Stasiun? = null
    var harga = 0
    var jarak = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Koneksi.startConnection()
        setupPermissions()
        codeScanner()
        rutes = Koneksi.getRutes()
        ruteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rutes)
        binding.spnRute.adapter = ruteAdapter
        binding.spnRute.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                rute = p2
                binding.tvPPM.text = "${rutes[rute].ppm.toInt().toRupiah()}/Meter"
                getDRutes()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        binding.spnDRute.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                drute = p2
                stasiun = drutes[drute].getStasiun()
                binding.tvAlamat.text = stasiun?.alamat
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        binding.rbMasuk.isChecked = true
    }

    fun getDRutes(){
        drutes = Koneksi.getDRutes(rutes[rute])
        druteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, drutes)
        binding.spnDRute.adapter = druteAdapter
    }

    fun runScan(str: String){
        try {
            val jsonObject = JSONTokener(str).nextValue() as JSONObject
            val id = jsonObject.getInt("id")
            val mode = jsonObject.getInt("mode")
            //Toast.makeText(this, "$id $mode", Toast.LENGTH_SHORT).show()
            //E-Kartu
            if(mode==1){
                var ekartu = Koneksi.getEKartu(id)
                var etiket = ekartu?.let { Koneksi.checkETiketMasuk(it) }
                if(binding.rbMasuk.isChecked){
                    //cek Customer masih diluar MRT
                    if(etiket == null){
                        etiket = ETiket(0,ekartu!!.id_kartu,
                            drutes[drute].id_stasiun,null,
                            rutes[rute].id_rute,null,
                            "","","",1,1
                        )
                        Koneksi.insertETiket(etiket)
                        etiket = ekartu?.let { Koneksi.getETiketBaru(it) }
                        if (etiket != null) {
                            Koneksi.ETiketMasuk(etiket)
                            etiket = Koneksi.getETiket(etiket.id_tiket)
                        }
                        binding.tvInfo.text = "Masuk Stasiun ${stasiun?.nama_stasiun}\n"+
                                "Pada ${etiket?.tgl_masuk?.let { Koneksi.getDate(it,"dd MMMM yyyy HH:mm") }}"
                    }
                    else{
                        binding.tvInfo.text = "Scan Masuk Invalid, Sedang Di Dalam MRT"
                    }
                }
                else{
                    if(etiket != null){
                        val druteAwal = Koneksi.getDRuteAwal(etiket)
                        if(druteAwal != null && druteAwal.id_rute == rutes[rute].id_rute){
                            hitungHarga(druteAwal.stasiun_ke?.minus(1) ?:0,drute)
                            if(ekartu?.saldo!! >= harga){
                                etiket.harga = harga.toDouble()
                                etiket.id_stasiun_akhir = drutes[drute].id_stasiun
                                Koneksi.ETiketKeluarEkartu(etiket)
                                etiket = Koneksi.getETiket(etiket.id_tiket)
                                binding.tvInfo.text = "Keluar Stasiun ${stasiun?.nama_stasiun}\n"+
                                        "Pada ${etiket?.tgl_keluar?.let { Koneksi.getDate(it,"dd MMMM yyyy HH:mm") }}\n" +
                                        "Dengan Total Harga ${etiket?.harga?.toInt()?.toRupiah()}\n" +
                                        "Sepanjang ${jarak} Meter"
                            }
                            else{
                                binding.tvInfo.text = "Saldo Tidak Mencukupi! Harga: ${harga.toRupiah()}"
                            }
                        }
                        else{
                            binding.tvInfo.text = "Keluar Pada Rute Salah!"
                        }
                    }
                    else{
                        binding.tvInfo.text = "Scan Masuk Invalid, Sedang Diluar MRT"
                    }
                }
            }
            else{ //E-Tiket
                var etiket = Koneksi.getETiket(id)
                //masuk
                if(binding.rbMasuk.isChecked){
                    if(etiket?.status_tiket == 1){
                        if(stasiun?.id_stasiun == etiket?.id_stasiun_awal && rutes[rute].id_rute == etiket?.id_rute){
                            Koneksi.ETiketMasuk(etiket)
                            etiket = Koneksi.getETiket(etiket.id_tiket)
                            binding.tvInfo.text = "Masuk Stasiun ${stasiun?.nama_stasiun}\n"+
                                    "Pada ${etiket?.tgl_masuk?.let { Koneksi.getDate(it,"dd MMMM yyyy HH:mm") }}"
                        }
                        else{
                            binding.tvInfo.text = "Scan Masuk Invalid"
                        }
                    }
                    else{
                        binding.tvInfo.text = "Status E-Tiket Invalid!"
                    }
                }
                else{
                    if(etiket?.status_tiket == 3){
                        if(stasiun?.id_stasiun == etiket?.id_stasiun_akhir && rutes[rute].id_rute == etiket?.id_rute){
                            Koneksi.ETiketKeluar(etiket)
                            etiket = Koneksi.getETiket(etiket.id_tiket)
                            binding.tvInfo.text = "Keluar Stasiun ${stasiun?.nama_stasiun}\n"+
                                    "Pada ${etiket?.tgl_keluar?.let { Koneksi.getDate(it,"dd MMMM yyyy HH:mm") }}"
                        }
                        else{
                            binding.tvInfo.text = "Scan Keluar Invalid"
                        }
                    }
                    else{
                        binding.tvInfo.text = "Status E-Tiket Invalid!"
                    }
                }
            }
        }catch (e:Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun hitungHarga(awal:Int,akhir:Int){
        harga = 0
        jarak = 0
        if(awal > -1 && akhir > -1){
            if(awal != akhir){
                if(akhir > awal){
                    for(i in awal until akhir){
                        jarak += drutes[i].jarak_next
                    }
                }
                else{
                    for(i in akhir until awal){
                        jarak += drutes[i].jarak_next
                    }
                }
                harga = jarak * rutes[rute].ppm.toInt()
            }
        }
    }


    private fun codeScanner() {
        codeScanner = CodeScanner(this, binding.scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    binding.tvText.text = it.text
                    runScan(it.text)
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "codeScanner: ${it.message}")
                }
            }

            binding.scannerView.setOnClickListener {
                codeScanner.startPreview()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQ
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQ -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "You need the camera permission to use this app",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    companion object {
        private const val CAMERA_REQ = 101
    }
}