package com.fajar.elearning.siswa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fajar.elearning.siswa.databinding.ActivityDetailMateriBinding

class DetailMateriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMateriBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMateriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Ambil data dari Intent (Dikirim dari MateriActivity)
        val judul = intent.getStringExtra("EXTRA_JUDUL") ?: "Detail Materi"
        val deskripsi = intent.getStringExtra("EXTRA_DESKRIPSI")
        val fileUrl = intent.getStringExtra("EXTRA_FILE_PATH")
        val materiId = intent.getIntExtra("EXTRA_ID", 0)

        // 2. Tombol Mulai Latihan -> Kirim ID & NAMA MATERI
        binding.btnMulaiLatihan.setOnClickListener {
            val intent = Intent(this, LatihanActivity::class.java)
            intent.putExtra("EXTRA_MATERI_ID", materiId)

            // PENTING: Kirim Nama Materi juga!
            intent.putExtra("EXTRA_NAMA_MATERI", judul)

            startActivity(intent)
        }

        // 3. Tampilkan Data di UI
        binding.tvJudulDetail.text = judul
        binding.tvDeskripsiDetail.text = deskripsi ?: "Tidak ada deskripsi tambahan."

        // 4. Logic Tombol Buka File
        if (!fileUrl.isNullOrEmpty()) {
            binding.btnBukaFile.visibility = View.VISIBLE
            binding.btnBukaFile.setOnClickListener {
                bukaFileDiBrowser(fileUrl)
            }
        } else {
            binding.btnBukaFile.visibility = View.GONE
        }

        // 5. Tombol Back
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun bukaFileDiBrowser(fullUrl: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(fullUrl)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membuka link: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}