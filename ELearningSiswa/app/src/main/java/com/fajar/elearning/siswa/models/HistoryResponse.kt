package com.fajar.elearning.siswa.models

import com.google.gson.annotations.SerializedName

data class HistoryResponse(
    val success: Boolean,
    val data: List<HistoryModel>
)

data class HistoryModel(
    val id: Int,
    val skor: Int,

    @SerializedName("tanggal_dikerjakan")
    val tanggalDikerjakan: String,

    val latihan: HistoryLatihan
)

data class HistoryLatihan(
    val id: Int,
    val materi: HistoryMateri
)

data class HistoryMateri(
    @SerializedName("judul_materi")
    val judulMateri: String,

    val mapel: HistoryMapel
)

data class HistoryMapel(
    @SerializedName("nama_mapel")
    val namaMapel: String
)