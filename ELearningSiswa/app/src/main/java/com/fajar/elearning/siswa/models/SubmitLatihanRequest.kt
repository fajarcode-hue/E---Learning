package com.fajar.elearning.siswa.models

import com.google.gson.annotations.SerializedName

data class SubmitLatihanRequest(
    // Menggunakan @SerializedName agar cocok dengan JSON "latihan_id"
    @SerializedName("latihan_id")
    val latihanId: Int,

    // Menggunakan Map karena format di JSON berupa object { "id_soal": "jawaban" }
    @SerializedName("jawaban_siswa")
    val jawabanSiswa: Map<String, String>
)