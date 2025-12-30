package com.fajar.elearning.siswa

import android.content.Intent
import android.os.Bundle
import android.util.Log // Tambahkan Log untuk debugging
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fajar.elearning.siswa.adapters.HistoryAdapter
import com.fajar.elearning.siswa.api.RetrofitClient
import com.fajar.elearning.siswa.models.HistoryResponse
import com.fajar.elearning.siswa.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryFragment : Fragment() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan dengan layout fragment_history.xml
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi SessionManager dengan context yang aman
        if (context != null) {
            sessionManager = SessionManager(requireContext())
        }

        // Inisialisasi View
        rvHistory = view.findViewById(R.id.rvHistory)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)

        // Setup RecyclerView
        rvHistory.layoutManager = LinearLayoutManager(context)

        // Panggil fungsi load data
        loadHistory()
    }

    private fun loadHistory() {
        // Pastikan sessionManager sudah terinisialisasi
        if (!::sessionManager.isInitialized) {
            if (context != null) sessionManager = SessionManager(requireContext())
            else return // Jika context null, berhenti (jangan crash)
        }

        val token = sessionManager.getToken()

        // Debugging: Cek token di Logcat (Filter: "HistoryCheck")
        Log.d("HistoryCheck", "Token saat ini: $token")

        // Jika Token Kosong, jangan langsung logout paksa.
        // Tampilkan pesan saja agar user tidak kaget.
        if (token.isNullOrEmpty()) {
            progressBar.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
            tvEmpty.text = "Gagal memuat sesi. Silakan refresh atau login ulang."
            return
        }

        // Mulai Loading
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        rvHistory.visibility = View.GONE

        RetrofitClient.instance.getHistory(token).enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                // Cek apakah fragment masih nempel (Safe Check)
                if (!isAdded || context == null) return

                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val historyList = response.body()?.data ?: emptyList()

                    if (historyList.isNotEmpty()) {
                        val adapter = HistoryAdapter(historyList)
                        rvHistory.adapter = adapter
                        rvHistory.visibility = View.VISIBLE
                        tvEmpty.visibility = View.GONE
                    } else {
                        tvEmpty.visibility = View.VISIBLE
                        tvEmpty.text = "Belum ada riwayat pengerjaan."
                        rvHistory.visibility = View.GONE
                    }
                } else {
                    // Hanya logout jika server benar-benar menolak (401)
                    if (response.code() == 401) {
                        Toast.makeText(context, "Sesi kedaluwarsa, silakan login kembali.", Toast.LENGTH_LONG).show()
                        sessionManager.logout()
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        activity?.finish()
                    } else {
                        tvEmpty.visibility = View.VISIBLE
                        tvEmpty.text = "Gagal memuat data: ${response.message()}"
                    }
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                if (!isAdded) return
                progressBar.visibility = View.GONE
                tvEmpty.visibility = View.VISIBLE
                tvEmpty.text = "Gagal terhubung ke server.\nPeriksa koneksi internet Anda."
                // Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}