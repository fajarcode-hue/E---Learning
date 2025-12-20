package com.fajar.elearning.siswa

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajar.elearning.siswa.adapters.SoalAdapter
import com.fajar.elearning.siswa.api.RetrofitClient
import com.fajar.elearning.siswa.databinding.ActivityLatihanBinding
import com.fajar.elearning.siswa.models.LatihanResponse
import com.fajar.elearning.siswa.models.SubmitLatihanRequest
import com.fajar.elearning.siswa.models.SubmitResponse
import com.fajar.elearning.siswa.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LatihanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLatihanBinding
    private lateinit var sessionManager: SessionManager

    // Map untuk menyimpan jawaban: "ID_SOAL" -> "JAWABAN (A/B/C/D)"
    private val jawabanSiswa = mutableMapOf<String, String>()

    private var materiId: Int = 0
    private var latihanId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLatihanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Ambil ID Materi dari Intent
        materiId = intent.getIntExtra("EXTRA_MATERI_ID", 0)

        // Setup RecyclerView
        binding.rvSoal.layoutManager = LinearLayoutManager(this)

        // Load Data
        loadSoal()

        // Klik Tombol Submit
        binding.btnSubmit.setOnClickListener {
            konfirmasiSubmit()
        }
    }

    private fun loadSoal() {
        val token = sessionManager.getToken()
        if (token == null) {
            finish()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        RetrofitClient.instance.getLatihanByMateri(materiId, token).enqueue(object : Callback<LatihanResponse> {
            override fun onResponse(call: Call<LatihanResponse>, response: Response<LatihanResponse>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data

                    if (data != null && !data.listSoal.isNullOrEmpty()) {
                        latihanId = data.id // Simpan ID Latihan

                        // Pasang Adapter
                        val adapter = SoalAdapter(data.listSoal) { idSoal, jawaban ->
                            // Update jawaban setiap kali diklik
                            jawabanSiswa[idSoal] = jawaban
                        }
                        binding.rvSoal.adapter = adapter
                    } else {
                        binding.tvEmpty.visibility = View.VISIBLE
                        binding.btnSubmit.isEnabled = false
                        Toast.makeText(this@LatihanActivity, "Belum ada soal untuk materi ini", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = "Gagal memuat soal"
                }
            }

            override fun onFailure(call: Call<LatihanResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@LatihanActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun konfirmasiSubmit() {
        AlertDialog.Builder(this)
            .setTitle("Kumpulkan Jawaban?")
            .setMessage("Pastikan semua soal terjawab. Anda tidak bisa mengulangi.")
            .setPositiveButton("Kumpulkan") { _, _ ->
                submitJawaban()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun submitJawaban() {
        val token = sessionManager.getToken() ?: return
        binding.progressBar.visibility = View.VISIBLE

        val request = SubmitLatihanRequest(latihanId, jawabanSiswa)

        RetrofitClient.instance.submitLatihan(request, token).enqueue(object : Callback<SubmitResponse> {
            override fun onResponse(call: Call<SubmitResponse>, response: Response<SubmitResponse>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.success == true) {
                    val hasil = response.body()!!

                    // Tampilkan Hasil Nilai
                    AlertDialog.Builder(this@LatihanActivity)
                        .setTitle("Hasil Latihan")
                        .setMessage("Nilai: ${hasil.nilai}\nBenar: ${hasil.benar} dari ${hasil.totalSoal} soal")
                        .setPositiveButton("Selesai") { _, _ ->
                            finish() // Tutup halaman latihan
                        }
                        .setCancelable(false)
                        .show()
                } else {
                    Toast.makeText(this@LatihanActivity, "Gagal mengirim jawaban", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SubmitResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@LatihanActivity, "Error koneksi", Toast.LENGTH_SHORT).show()
            }
        })
    }
}