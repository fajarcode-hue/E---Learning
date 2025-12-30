package com.fajar.elearning.siswa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fajar.elearning.siswa.utils.SessionManager // Jangan lupa import ini
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager // Deklarasi variabel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inisialisasi SessionManager
        sessionManager = SessionManager(this)

        // 2. CEK LOGIN (Satpam)
        // Kalau belum login, langsung lempar ke LoginActivity
        if (!sessionManager.isLogin()) {
            moveToLogin()
            return // Stop eksekusi kode di bawahnya
        }

        // 3. Kalau sudah login, lanjut tampilkan Home
        loadFragment(HomeFragment())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_mapel -> {
                    loadFragment(MapelFragment())
                    true
                }
                R.id.nav_history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Fungsi untuk pindah ke Login
    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        // Hapus stack activity agar user tidak bisa tekan tombol Back kembali ke Main
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}