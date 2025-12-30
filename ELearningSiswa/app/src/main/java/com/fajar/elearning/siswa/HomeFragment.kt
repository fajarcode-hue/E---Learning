package com.fajar.elearning.siswa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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

class HomeFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var rvMapel: RecyclerView
    private lateinit var progressBar: View
    private lateinit var tvError: TextView
    private lateinit var tvNamaSiswa: TextView
    private lateinit var btnLogout: ImageButton

    // Fragment butuh onCreateView untuk menghubungkan dengan Layout XML
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan dengan fragment_home.xml
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    // Logika utama dijalankan di onViewCreated (setelah layout jadi)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi SessionManager (Pakai requireContext() sebagai pengganti 'this')
        sessionManager = SessionManager(requireContext())

        // 1. Hubungkan ID (Sama seperti di Activity, tapi pakai view.findViewById)
        rvMapel = view.findViewById(R.id.rvMapel)
        progressBar = view.findViewById(R.id.progressBar)
        tvError = view.findViewById(R.id.tvError)
        tvNamaSiswa = view.findViewById(R.id.tvNamaSiswa)
        btnLogout = view.findViewById(R.id.btnLogout)

        // 2. Set Nama User
        val nama = sessionManager.getNama()
        tvNamaSiswa.text = nama ?: "Siswa"

        // 3. Logika Logout
        btnLogout.setOnClickListener {
            sessionManager.logout()
            // Pindah ke Login
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

        // 4. Load Data Mapel
        rvMapel.layoutManager = LinearLayoutManager(requireContext())
        loadDataMapel()
    }

    private fun loadDataMapel() {
        progressBar.visibility = View.VISIBLE
        tvError.visibility = View.GONE

        val token = sessionManager.getToken()

        if (token != null) {
            RetrofitClient.instance.getMapel(token).enqueue(object : Callback<MapelResponse> {
                override fun onResponse(call: Call<MapelResponse>, response: Response<MapelResponse>) {
                    // Cek jika fragment masih aktif (Penting agar tidak crash saat pindah tab cepat)
                    if (!isAdded) return

                    progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val listMapel = response.body()?.data
                        if (!listMapel.isNullOrEmpty()) {
                            val adapter = MapelAdapter(listMapel) { mapelTerpilih ->
                                // Pindah ke MateriActivity
                                val intent = Intent(requireContext(), MateriActivity::class.java)
                                intent.putExtra("EXTRA_MAPEL_ID", mapelTerpilih.id)
                                intent.putExtra("EXTRA_NAMA_MAPEL", mapelTerpilih.nama_mapel)
                                startActivity(intent)
                            }
                            rvMapel.adapter = adapter
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
                    showError("Error koneksi: ${t.message}")
                }
            })
        }
    }

    private fun showError(message: String) {
        tvError.visibility = View.VISIBLE
        tvError.text = message
    }
}