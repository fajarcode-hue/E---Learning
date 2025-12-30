package com.fajar.elearning.siswa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fajar.elearning.siswa.adapters.MapelAdapter
import com.fajar.elearning.siswa.api.RetrofitClient
import com.fajar.elearning.siswa.models.MapelResponse
import com.fajar.elearning.siswa.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapelFragment : Fragment() {

    private lateinit var rvMapel: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan dengan layout XML fragment_mapel.xml
        return inflater.inflate(R.layout.fragment_mapel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        // 1. Hubungkan ID
        rvMapel = view.findViewById(R.id.rvMapel)
        progressBar = view.findViewById(R.id.progressBar)
        tvError = view.findViewById(R.id.tvError)

        // 2. Setup RecyclerView
        rvMapel.layoutManager = LinearLayoutManager(requireContext())

        // 3. Load Data
        loadDataMapel()
    }

    private fun loadDataMapel() {
        progressBar.visibility = View.VISIBLE
        tvError.visibility = View.GONE

        val token = sessionManager.getToken()

        // Cek Token Aman
        if (!token.isNullOrEmpty()) {
            RetrofitClient.instance.getMapel(token).enqueue(object : Callback<MapelResponse> {
                override fun onResponse(call: Call<MapelResponse>, response: Response<MapelResponse>) {
                    // Cek Fragment masih nempel
                    if (!isAdded) return

                    progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val listMapel = response.body()?.data

                        if (!listMapel.isNullOrEmpty()) {
                            // Pasang Adapter
                            val adapter = MapelAdapter(listMapel) { mapelTerpilih ->
                                // Aksi saat diklik: Pindah ke MateriActivity
                                val intent = Intent(requireContext(), MateriActivity::class.java)
                                intent.putExtra("EXTRA_MAPEL_ID", mapelTerpilih.id)
                                intent.putExtra("EXTRA_NAMA_MAPEL", mapelTerpilih.nama_mapel)
                                startActivity(intent)
                            }
                            rvMapel.adapter = adapter
                            rvMapel.visibility = View.VISIBLE
                        } else {
                            showError("Belum ada mata pelajaran.")
                        }
                    } else {
                        showError("Gagal memuat: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<MapelResponse>, t: Throwable) {
                    if (!isAdded) return
                    progressBar.visibility = View.GONE
                    showError("Koneksi bermasalah")
                }
            })
        } else {
            if (isAdded) showError("Sesi habis, silakan login ulang")
        }
    }

    private fun showError(message: String) {
        tvError.visibility = View.VISIBLE
        tvError.text = message
        rvMapel.visibility = View.GONE
    }
}