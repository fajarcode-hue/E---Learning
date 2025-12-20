package com.fajar.elearning.siswa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fajar.elearning.siswa.adapters.HistoryAdapter
import com.fajar.elearning.siswa.api.RetrofitClient // Pastikan ini sesuai nama file API client Anda
import com.fajar.elearning.siswa.models.HistoryResponse
import com.fajar.elearning.siswa.utils.SessionManager // Import SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var sessionManager: SessionManager // Tambahkan variabel SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Inisialisasi SessionManager
        sessionManager = SessionManager(this)

        rvHistory = findViewById(R.id.rvHistory)
        rvHistory.layoutManager = LinearLayoutManager(this)

        loadHistory()
    }

    private fun loadHistory() {
        // 1. Ambil token langsung dari SessionManager
        val token = sessionManager.getToken()

        // 2. Cek apakah token ada
        if (token != null) {
            // PENTING: Di SessionManager, token sudah disimpan dengan format "Bearer ...".
            // Jadi di sini cukup masukkan variabel 'token' saja.
            RetrofitClient.instance.getHistory(token)
                .enqueue(object : Callback<HistoryResponse> {
                    override fun onResponse(
                        call: Call<HistoryResponse>,
                        response: Response<HistoryResponse>
                    ) {
                        if (response.isSuccessful) {
                            val historyList = response.body()?.data ?: emptyList()

                            if (historyList.isEmpty()) {
                                Toast.makeText(this@HistoryActivity, "Belum ada riwayat latihan", Toast.LENGTH_SHORT).show()
                            }

                            val adapter = HistoryAdapter(historyList)
                            rvHistory.adapter = adapter
                        } else {
                            Toast.makeText(this@HistoryActivity, "Gagal memuat: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                        Toast.makeText(this@HistoryActivity, "Error koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            // Jika token null, berarti sesi habis/belum login
            Toast.makeText(this, "Sesi habis, silakan login ulang", Toast.LENGTH_SHORT).show()

            // Opsional: Lempar balik ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}