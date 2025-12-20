package com.fajar.elearning.siswa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fajar.elearning.siswa.databinding.ItemMateriBinding
import com.fajar.elearning.siswa.models.Materi

class MateriAdapter(
    private val listMateri: List<Materi>,
    private val onItemClick: (Materi) -> Unit
) : RecyclerView.Adapter<MateriAdapter.MateriViewHolder>() {

    class MateriViewHolder(val binding: ItemMateriBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriViewHolder {
        val binding = ItemMateriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MateriViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MateriViewHolder, position: Int) {
        val materi = listMateri[position]

        holder.binding.tvJudulMateri.text = materi.judul_materi
        holder.binding.tvDeskripsi.text = materi.deskripsi ?: "Tidak ada deskripsi"

        // Ubah label tipe file jika perlu (opsional)
        if (materi.file_url != null) {
            holder.binding.tvTipeFile.text = "📄 Lihat Materi"
        } else {
            holder.binding.tvTipeFile.text = "📝 Bacaan Teks"
        }

        holder.itemView.setOnClickListener {
            onItemClick(materi)
        }
    }

    override fun getItemCount(): Int = listMateri.size
}