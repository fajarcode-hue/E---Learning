package com.fajar.elearning.siswa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.fajar.elearning.siswa.R
import com.fajar.elearning.siswa.databinding.ItemSoalBinding
import com.fajar.elearning.siswa.models.SoalModel

class SoalAdapter(
    private val listSoal: List<SoalModel>,
    // Callback ini mengirim data jawaban ke Activity setiap kali user klik
    private val onJawabanChanged: (String, String) -> Unit
) : RecyclerView.Adapter<SoalAdapter.SoalViewHolder>() {

    // Simpan jawaban sementara disini (Key: ID Soal, Value: Jawaban "A"/"B")
    private val jawabanSementara = mutableMapOf<String, String>()

    inner class SoalViewHolder(val binding: ItemSoalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoalViewHolder {
        val binding = ItemSoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SoalViewHolder, position: Int) {
        val soal = listSoal[position]
        val soalId = soal.id.toString()

        holder.binding.apply {
            // 1. Set Teks Pertanyaan
            tvPertanyaan.text = "${position + 1}. ${soal.pertanyaan}"

            // 2. Set Teks Pilihan (Ambil dari Map "pilihan")
            // Gunakan elvis operator ?: "" jaga-jaga kalau null
            rbA.text = "A. ${soal.pilihan["A"] ?: "-"}"
            rbB.text = "B. ${soal.pilihan["B"] ?: "-"}"
            rbC.text = "C. ${soal.pilihan["C"] ?: "-"}"
            rbD.text = "D. ${soal.pilihan["D"] ?: "-"}"

            // 3. PENTING: Hapus listener lama dulu sebelum mengubah state check
            // Ini untuk mencegah bug "looping" atau salah centang saat scroll
            rgPilihan.setOnCheckedChangeListener(null)

            // 4. Cek apakah soal ini sudah dijawab sebelumnya?
            // Kalau sudah ada di map jawabanSementara, kita centang otomatis
            rgPilihan.clearCheck() // Reset dulu
            val jawabanKamu = jawabanSementara[soalId]

            when (jawabanKamu) {
                "A" -> rbA.isChecked = true
                "B" -> rbB.isChecked = true
                "C" -> rbC.isChecked = true
                "D" -> rbD.isChecked = true
            }

            // 5. Pasang listener baru untuk menyimpan jawaban saat diklik
            rgPilihan.setOnCheckedChangeListener { group, checkedId ->
                val jawabanDipilih = when (checkedId) {
                    R.id.rbA -> "A"
                    R.id.rbB -> "B"
                    R.id.rbC -> "C"
                    R.id.rbD -> "D"
                    else -> ""
                }

                // Simpan ke map lokal adapter
                jawabanSementara[soalId] = jawabanDipilih

                // Kirim info ke Activity
                onJawabanChanged(soalId, jawabanDipilih)
            }
        }
    }

    override fun getItemCount() = listSoal.size
}