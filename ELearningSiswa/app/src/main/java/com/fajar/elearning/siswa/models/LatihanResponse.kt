package com.fajar.elearning.siswa.models

import com.google.gson.annotations.SerializedName

// --- 1. RESPONSE SAAT GET SOAL ---
data class LatihanResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: LatihanData?
)

data class LatihanData(
    @SerializedName("id") val id: Int, // ID Latihan
    @SerializedName("materi_id") val materiId: Int,
    @SerializedName("soal") val listSoal: List<SoalModel>
)

data class SoalModel(
    @SerializedName("id") val id: Int, // ID Soal
    @SerializedName("pertanyaan") val pertanyaan: String,
    @SerializedName("pilihan") val pilihan: Map<String, String> // Map karena isinya "A": "Jawabannya"
)

// --- 2. REQUEST BODY SAAT SUBMIT ---


// --- 3. RESPONSE SETELAH SUBMIT ---
data class SubmitResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("nilai") val nilai: Double, // Pakai Double jaga-jaga kalau ada koma
    @SerializedName("benar") val benar: Int,
    @SerializedName("total_soal") val totalSoal: Int
)