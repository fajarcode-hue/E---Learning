package com.fajar.elearning.siswa.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    /**
     * BASE_URL: Alamat IP Server Laravel.
     * * PENTING:
     * 1. 192.168.123.102 adalah IP MacBook kamu saat ini.
     * 2. Pastikan Laravel jalan dengan: php artisan serve --host=0.0.0.0 --port=8000
     * 3. Jika IP berubah (pindah wifi), ganti IP di bawah ini sesuai hasil 'ifconfig' baru.
     */
    private const val BASE_URL = "http://172.26.205.43:8000/api/" // diganti terus jir

    val instance: ApiService by lazy {

        // 1. Logger: Berguna buat ngintip data yang dikirim/diterima di Logcat
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        // 2. Client dengan TIMEOUT yang diperpanjang (Solusi Error Time Out)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS) // Perpanjang jadi 60 detik
            .readTimeout(60, TimeUnit.SECONDS)    // Perpanjang jadi 60 detik
            .writeTimeout(60, TimeUnit.SECONDS)   // Perpanjang jadi 60 detik
            .build()

        // 3. Build Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        retrofit.create(ApiService::class.java)
    }
}