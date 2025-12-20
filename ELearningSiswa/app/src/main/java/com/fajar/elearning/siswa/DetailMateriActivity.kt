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
        val judul = intent.getStringExtra("EXTRA_JUDUL")
        val deskripsi = intent.getStringExtra("EXTRA_DESKRIPSI")

        // Data ini sekarang isinya sudah Link Lengkap (http://...)
        // karena Backend sudah mengirim 'file_url'
        val fileUrl = intent.getStringExtra("EXTRA_FILE_PATH")

        val materiId = intent.getIntExtra("EXTRA_ID", 0) // Ambil ID

        binding.btnMulaiLatihan.setOnClickListener {
            val intent = Intent(this, LatihanActivity::class.java)
            intent.putExtra("EXTRA_MATERI_ID", materiId) // Kirim ID ke LatihanActivity
            startActivity(intent)
        }

        // Debugging: Cek apakah link muncul di layar?
        Toast.makeText(this, "Link File: $fileUrl", Toast.LENGTH_LONG).show()

        // 2. Tampilkan Judul & Deskripsi
        binding.tvJudulDetail.text = judul
        binding.tvDeskripsiDetail.text = deskripsi ?: "Tidak ada deskripsi tambahan."

        // 3. Logic Tombol Buka File
        if (!fileUrl.isNullOrEmpty()) {
            binding.btnBukaFile.visibility = View.VISIBLE

            binding.btnBukaFile.setOnClickListener {
                bukaFileDiBrowser(fileUrl)
            }
        } else {
            // Kalau null, tombol disembunyikan
            binding.btnBukaFile.visibility = View.GONE
        }

        // 4. Tombol Back
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun bukaFileDiBrowser(fullUrl: String) {
        try {
            // Karena 'fullUrl' sudah berisi http://..., kita tidak perlu nambah BASE_URL lagi
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(fullUrl)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membuka link: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}