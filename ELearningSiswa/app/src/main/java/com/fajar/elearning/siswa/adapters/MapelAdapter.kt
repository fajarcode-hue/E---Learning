package com.fajar.elearning.siswa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fajar.elearning.siswa.databinding.ItemMapelBinding
import com.fajar.elearning.siswa.models.Mapel

class MapelAdapter(
    private var listMapel: List<Mapel>, // Ubah 'val' menjadi 'var' agar bisa diupdate
    private val onItemClick: (Mapel) -> Unit // Listener untuk klik
) : RecyclerView.Adapter<MapelAdapter.MapelViewHolder>() {

    class MapelViewHolder(val binding: ItemMapelBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapelViewHolder {
        val binding = ItemMapelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MapelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MapelViewHolder, position: Int) {
        val mapel = listMapel[position]

        // 1. Set Nama Mapel
        holder.binding.tvNamaMapel.text = mapel.nama_mapel

        // 2. Set Nama Guru
        val namaGuru = mapel.guru?.name ?: "Belum ada pengajar"
        holder.binding.tvNamaGuru.text = "Pengajar: $namaGuru"

        // 3. Listener Klik: Kirim data mapel ke Activity saat diklik
        holder.itemView.setOnClickListener {
            onItemClick(mapel)
        }
    }

    override fun getItemCount(): Int = listMapel.size

    // --- FUNGSI BARU UNTUK SEARCH / FILTER ---
    fun updateList(newList: List<Mapel>) {
        listMapel = newList
        notifyDataSetChanged() // Refresh tampilan RecyclerView
    }
}