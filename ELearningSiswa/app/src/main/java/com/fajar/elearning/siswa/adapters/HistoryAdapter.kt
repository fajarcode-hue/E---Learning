package com.fajar.elearning.siswa.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fajar.elearning.siswa.R
import com.fajar.elearning.siswa.models.HistoryModel

class HistoryAdapter(
    private val listHistory: List<HistoryModel>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMapel: TextView = view.findViewById(R.id.tvMapel)
        val tvMateri: TextView = view.findViewById(R.id.tvMateri)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvSkor: TextView = view.findViewById(R.id.tvSkor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = listHistory[position]

        // Mengambil data dari nested object
        val namaMapel = history.latihan.materi.mapel.namaMapel
        val judulMateri = history.latihan.materi.judulMateri

        holder.tvMapel.text = namaMapel
        holder.tvMateri.text = judulMateri
        holder.tvTanggal.text = history.tanggalDikerjakan
        holder.tvSkor.text = history.skor.toString()

        // (Opsional) Warnai skor: Hijau jika >= 75, Merah jika dibawahnya
        if (history.skor >= 75) {
            holder.tvSkor.setTextColor(Color.parseColor("#4CAF50")) // Hijau
        } else {
            holder.tvSkor.setTextColor(Color.parseColor("#F44336")) // Merah
        }
    }

    override fun getItemCount() = listHistory.size
}