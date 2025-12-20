package com.fajar.elearning.siswa.api

import com.fajar.elearning.siswa.models.HistoryResponse
import com.fajar.elearning.siswa.models.LoginResponse
import com.fajar.elearning.siswa.models.MapelResponse
import com.fajar.elearning.siswa.models.MateriResponse
import com.fajar.elearning.siswa.models.LatihanResponse
import com.fajar.elearning.siswa.models.SubmitLatihanRequest
import com.fajar.elearning.siswa.models.SubmitResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Login menggunakan x-www-form-urlencoded
    @FormUrlEncoded
    @POST("login/siswa")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    // Ambil Data Dashboard (Butuh Token)
    @GET("siswa/mapel")
    fun getMapel(
        @Header("Authorization") token: String
    ): Call<MapelResponse>

    // ... kodingan login dan mapel ...

    // GET Materi berdasarkan ID Mapel
    @GET("siswa/materi/mapel/{mapel_id}")
    fun getMateriByMapel(
        @Path("mapel_id") mapelId: Int,
        @Header("Authorization") token: String
    ): Call<MateriResponse>

    // ... kodingan lama ...

    // 1. Ambil Soal berdasarkan Materi ID
    @GET("siswa/latihan/materi/{materi_id}")
    fun getLatihanByMateri(
        @Path("materi_id") materiId: Int,
        @Header("Authorization") token: String
    ): Call<LatihanResponse>

    // 2. Kirim Jawaban
    @POST("siswa/latihan/submit")
    fun submitLatihan(
        @Body request: SubmitLatihanRequest,
        @Header("Authorization") token: String
    ): Call<SubmitResponse>

    @GET("siswa/history")
    fun getHistory(
        @Header("Authorization") token: String
    ): Call<HistoryResponse>


}