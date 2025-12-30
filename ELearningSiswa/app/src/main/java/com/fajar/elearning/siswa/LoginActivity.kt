package com.fajar.elearning.siswa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fajar.elearning.siswa.api.RetrofitClient
import com.fajar.elearning.siswa.databinding.ActivityLoginBinding
import com.fajar.elearning.siswa.models.LoginResponse
import com.fajar.elearning.siswa.utils.SessionManager
import com.google.gson.Gson // Perlu import Gson untuk parsing error body
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Cek jika tombol diklik
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginProses(email, password)
        }
    }

    private fun loginProses(email: String, pass: String) {
        // Tampilkan loading, sembunyikan tombol
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.visibility = View.INVISIBLE

        // Panggil API
        RetrofitClient.instance.login(email, pass).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.visibility = View.VISIBLE

                if (response.isSuccessful) {
                    // --- STATUS 200 OK ---
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.success) {
                        // 1. Simpan Token
                        val token = loginResponse.token ?: ""
                        val nama = loginResponse.user?.name ?: "Siswa"
                        sessionManager.saveSession(token, nama)

                        // 2. Beri pesan sukses
                        Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                        // 3. Pindah ke Dashboard (Ke MainActivity sekarang)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Gagal: ${loginResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // --- STATUS ERROR (401, 404, 500) ---
                    // Baca pesan error dari server (bukan body biasa)
                    try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            // Parse JSON error manual
                            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                            Toast.makeText(this@LoginActivity, errorResponse.message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@LoginActivity, "Gagal: ${response.code()} ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@LoginActivity, "Terjadi kesalahan server (${response.code()})", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.visibility = View.VISIBLE
                // Ini baru benar-benar error koneksi (internet mati / server down)
                Toast.makeText(this@LoginActivity, "Koneksi Gagal: Pastikan Server Jalan", Toast.LENGTH_LONG).show()
            }
        })
    }
}