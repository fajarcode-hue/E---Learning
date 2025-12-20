package com.fajar.elearning.siswa.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        const val KEY_TOKEN = "token"
        const val KEY_IS_LOGIN = "is_login"
        const val KEY_NAMA = "nama"
    }

    // Simpan data login
    fun saveSession(token: String, nama: String) {
        editor.putString(KEY_TOKEN, "Bearer $token") // Simpan format Bearer langsung
        editor.putString(KEY_NAMA, nama)
        editor.putBoolean(KEY_IS_LOGIN, true)
        editor.apply()
    }

    // Ambil Token
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    // Cek status login
    fun isLogin(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGIN, false)
    }

    // Ambil Nama User
    fun getNama(): String? {
        return prefs.getString(KEY_NAMA, "Siswa")
    }

    // Logout (Hapus data)
    fun logout() {
        editor.clear()
        editor.apply()
    }
}