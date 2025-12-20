package com.fajar.elearning.siswa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton // Pastikan ini ter-import
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fajar.elearning.siswa.adapters.MapelAdapter
import com.fajar.elearning.siswa.api.RetrofitClient
import com.fajar.elearning.siswa.models.MapelResponse
import com.fajar.elearning.siswa.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    // Deklarasi Variabel Manual (Tanpa Binding)
    private lateinit var sessionManager: SessionManager
    private lateinit var rvMapel: RecyclerView
    private lateinit var progressBar: View
    private lateinit var tvError: TextView
    private lateinit var tvNamaSiswa: TextView
    private lateinit var btnLogout: ImageButton
    private lateinit var cvMenuHistory: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sessionManager = SessionManager(this)

        // 1. CEK LOGIN
        if (!sessionManager.isLogin()) {
            moveToLogin()
            return
        }

        // 2. HUBUNGKAN VIEW DENGAN ID (FINDVIEWBYID)
        rvMapel = findViewById(R.id.rvMapel)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
        tvNamaSiswa = findViewById(R.id.tvNamaSiswa)
        btnLogout = findViewById(R.id.btnLogout)      // <--- Tombol Logout
        cvMenuHistory = findViewById(R.id.cvMenuHistory)

        // 3. LOGIKA TOMBOL LOGOUT (Dengan Tes Toast)
        btnLogout.setOnClickListener {
            // Tampilkan pesan dulu untuk memastikan tombol terpencet
            Toast.makeText(this, "Logout diproses...", Toast.LENGTH_SHORT).show()

            // Proses Logout
            sessionManager.logout()
            moveToLogin()
        }

        // 4. LOGIKA TOMBOL HISTORY
        cvMenuHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // 5. SET NAMA SISWA
        val nama = sessionManager.getNama()
        tvNamaSiswa.text = nama ?: "Siswa"

        // 6. LOAD DATA
        rvMapel.layoutManager = LinearLayoutManager(this)
        loadDataMapel()
    }

    private fun loadDataMapel() {
        progressBar.visibility = View.VISIBLE
        tvError.visibility = View.GONE

        val token = sessionManager.getToken()

        if (token != null) {
            RetrofitClient.instance.getMapel(token).enqueue(object : Callback<MapelResponse> {
                override fun onResponse(call: Call<MapelResponse>, response: Response<MapelResponse>) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val listMapel = response.body()?.data
                        if (!listMapel.isNullOrEmpty()) {
                            val adapter = MapelAdapter(listMapel) { mapelTerpilih ->
                                val intent = Intent(this@DashboardActivity, MateriActivity::class.java)
                                intent.putExtra("EXTRA_MAPEL_ID", mapelTerpilih.id)
                                intent.putExtra("EXTRA_NAMA_MAPEL", mapelTerpilih.nama_mapel)
                                startActivity(intent)
                            }
                            rvMapel.adapter = adapter
                        } else {
                            showError("Belum ada mata pelajaran.")
                        }
                    } else {
                        if (response.code() == 401) {
                            sessionManager.logout()
                            moveToLogin()
                        } else {
                            showError("Gagal memuat: ${response.code()}")
                        }
                    }
                }

                override fun onFailure(call: Call<MapelResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    showError("Error koneksi: ${t.message}")
                }
            })
        }
    }

    private fun showError(message: String) {
        tvError.visibility = View.VISIBLE
        tvError.text = message
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}