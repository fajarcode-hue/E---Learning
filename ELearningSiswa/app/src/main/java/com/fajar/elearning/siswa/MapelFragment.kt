package com.fajar.elearning.siswa

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fajar.elearning.siswa.adapters.MapelAdapter
import com.fajar.elearning.siswa.api.RetrofitClient
import com.fajar.elearning.siswa.models.Mapel
import com.fajar.elearning.siswa.models.MapelResponse
import com.fajar.elearning.siswa.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MapelFragment : Fragment() {

    private lateinit var rvMapel: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var etSearch: EditText // Tambahan untuk search
    private lateinit var sessionManager: SessionManager

    // Simpan data master (semua mapel) dan adapter
    private var fullListMapel: List<Mapel> = ArrayList()
    private lateinit var adapter: MapelAdapter

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
        etSearch = view.findViewById(R.id.etSearch) // ID EditText Search

        // 2. Setup RecyclerView
        rvMapel.layoutManager = LinearLayoutManager(requireContext())

        // 3. Load Data
        loadDataMapel()

        // 4. Pasang Listener Pencarian (TextWatcher)
        setupSearchListener()
    }

    private fun setupSearchListener() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Panggil fungsi filter setiap kali teks berubah
                filterMapel(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterMapel(query: String) {
        // Pastikan adapter sudah ada isinya
        if (!::adapter.isInitialized) return

        if (query.isEmpty()) {
            // Jika kosong, kembalikan ke list penuh
            adapter.updateList(fullListMapel)
            tvError.visibility = View.GONE
            rvMapel.visibility = View.VISIBLE
        } else {
            // Filter list berdasarkan nama mapel (case insensitive)
            val filteredList = fullListMapel.filter { item ->
                item.nama_mapel.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault()))
            }

            if (filteredList.isEmpty()) {
                // Tidak ada hasil
                adapter.updateList(emptyList())
                tvError.visibility = View.VISIBLE
                tvError.text = "Pelajaran \"$query\" tidak ditemukan"
                rvMapel.visibility = View.GONE
            } else {
                // Ada hasil
                adapter.updateList(filteredList)
                tvError.visibility = View.GONE
                rvMapel.visibility = View.VISIBLE
            }
        }
    }

    private fun loadDataMapel() {
        progressBar.visibility = View.VISIBLE
        tvError.visibility = View.GONE

        val token = sessionManager.getToken()

        // Cek Token Aman
        if (!token.isNullOrEmpty()) {
            RetrofitClient.instance.getMapel(token).enqueue(object : Callback<MapelResponse> {
                override fun onResponse(call: Call<MapelResponse>, response: Response<MapelResponse>) {
                    if (!isAdded) return
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val listMapel = response.body()?.data

                        if (!listMapel.isNullOrEmpty()) {
                            // Simpan ke Master Data
                            fullListMapel = listMapel

                            // Pasang Adapter
                            adapter = MapelAdapter(fullListMapel) { mapelTerpilih ->
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