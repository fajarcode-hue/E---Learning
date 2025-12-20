package com.fajar.elearning.siswa.models

data class MapelResponse(
    val success: Boolean,
    val data: List<Mapel>
)

data class Mapel(
    val id: Int,
    val nama_mapel: String,
    val guru: Guru? // Bisa null jika mapel belum ada gurunya
)

data class Guru(
    val id: Int,
    val name: String // PENTING: Di JSON tulisannya 'name', bukan 'nama'
)