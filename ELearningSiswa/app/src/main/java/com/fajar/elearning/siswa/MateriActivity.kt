package com.fajar.elearning.siswa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajar.elearning.siswa.adapters.MateriAdapter
import com.fajar.elearning.siswa.api.RetrofitClient
import com.fajar.elearning.siswa.databinding.ActivityMateriBinding
import com.fajar.elearning.siswa.models.MateriResponse
import com.fajar.elearning.siswa.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MateriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMateriBinding
    private lateinit var sessionManager: SessionManager

    // Variabel untuk menampung data yang dikirim dari Dashboard
    private var mapelId: Int = 0
    private var namaMapel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMateriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // 1. Ambil Data Mapel dari Intent (Kiriman dari Dashboard)
        mapelId = intent.getIntExtra("EXTRA_MAPEL_ID", 0)
        namaMapel = intent.getStringExtra("EXTRA_NAMA_MAPEL") ?: "Materi Pelajaran"

        // 2. Set Judul Header
        binding.tvNamaMapelHeader.text = namaMapel

        // 3. Tombol Back
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 4. Setup RecyclerView
        binding.rvMateri.layoutManager = LinearLayoutManager(this)

        // 5. Load Data Materi
        loadDataMateri()
    }

    private fun loadDataMateri() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE

        val token = sessionManager.getToken()

        if (token != null) {
            RetrofitClient.instance.getMateriByMapel(mapelId, token).enqueue(object : Callback<MateriResponse> {
                override fun onResponse(call: Call<MateriResponse>, response: Response<MateriResponse>) {
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val listMateri = response.body()?.data

                        if (!listMateri.isNullOrEmpty()) {

                            // --- SETUP ADAPTER ---
                            val adapter = MateriAdapter(listMateri) { materiTerpilih ->

                                // Debugging: Cek apakah URL terbaca (Bisa dihapus nanti)
                                // Toast.makeText(this@MateriActivity, "Link: ${materiTerpilih.file_url}", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@MateriActivity, DetailMateriActivity::class.java)

                                // Kirim data ke halaman Detail
                                intent.putExtra("EXTRA_ID", materiTerpilih.id)
                                intent.putExtra("EXTRA_JUDUL", materiTerpilih.judul_materi)
                                intent.putExtra("EXTRA_DESKRIPSI", materiTerpilih.deskripsi)

                                // PENTING: Mengirim 'file_url' (Link Lengkap HTTP)
                                intent.putExtra("EXTRA_FILE_PATH", materiTerpilih.file_url)

                                startActivity(intent)
                            }

                            binding.rvMateri.adapter = adapter

                        } else {
                            binding.tvError.visibility = View.VISIBLE
                            binding.tvError.text = "Belum ada materi di mapel ini."
                        }
                    } else {
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = "Gagal memuat materi."
                    }
                }

                override fun onFailure(call: Call<MateriResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = "Koneksi bermasalah: ${t.message}"
                }
            })
        }
    }
}