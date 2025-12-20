package com.fajar.elearning.siswa.models

import com.google.gson.annotations.SerializedName

// 1. Wadah Utama Response
data class MateriResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("mapel")
    val mapel: Any?,

    @SerializedName("data")
    val data: List<Materi>
)

// 2. Model Data Materi
data class Materi(
    @SerializedName("id")
    val id: Int,

    @SerializedName("judul_materi")
    val judul_materi: String,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    // --- BAGIAN PENTING YANG BIKIN ERROR ---
    // Pastikan namanya 'file_url' agar sesuai dengan MateriActivity
    @SerializedName("file_url")
    val file_url: String?,
    // ----------------------------------------

    @SerializedName("latihan")
    val latihan: Any?,

    @SerializedName("created_at")
    val created_at: String?
)