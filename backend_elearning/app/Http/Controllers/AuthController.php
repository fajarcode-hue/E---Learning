<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use App\Models\Admin;
use App\Models\Guru;
use App\Models\Siswa;
use Illuminate\Support\Facades\Auth;

class AuthController extends Controller
{
    // =================================================================
    // LOGIN ADMIN
    // =================================================================
    public function loginAdmin(Request $request)
    {
        // 1. Validasi Input
        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        // 2. Cari data di tabel Admins
        $admin = Admin::where('email', $request->email)->first();

        // 3. Cek apakah user ada DAN password benar
        if (! $admin || ! Hash::check($request->password, $admin->password)) {
            return response()->json([
                'success' => false,
                'message' => 'Email atau Password salah',
            ], 401);
        }

        // 4. Jika sukses, buat Token
        // 'admin-token' adalah nama identitas tokennya (bebas)
        $token = $admin->createToken('admin-token')->plainTextToken;

        // 5. Return Response JSON
        return response()->json([
            'success' => true,
            'message' => 'Login Admin Berhasil',
            'role' => 'admin',
            'token' => $token, // Token ini yang nanti dipakai di Postman/Frontend
            'data' => $admin,
        ]);
    }

    // =================================================================
    // LOGIN GURU
    // =================================================================
    public function loginGuru(Request $request)
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        $guru = Guru::where('email', $request->email)->first();

        if (! $guru || ! Hash::check($request->password, $guru->password)) {
            return response()->json([
                'success' => false,
                'message' => 'Email atau Password salah',
            ], 401);
        }

        // Buat token khusus guru
        $token = $guru->createToken('guru-token')->plainTextToken;

        return response()->json([
            'success' => true,
            'message' => 'Login Guru Berhasil',
            'role' => 'guru',
            'token' => $token,
            'data' => $guru,
        ]);
    }

    // =================================================================
    // LOGIN SISWA
    // =================================================================
    public function loginSiswa(Request $request)
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        $siswa = Siswa::where('email', $request->email)->first();

        if (! $siswa || ! Hash::check($request->password, $siswa->password)) {
            return response()->json([
                'success' => false,
                'message' => 'Email atau Password salah',
            ], 401);
        }

        // Buat token khusus siswa
        $token = $siswa->createToken('siswa-token')->plainTextToken;

        return response()->json([
            'success' => true,
            'message' => 'Login Siswa Berhasil',
            'role' => 'siswa',
            'token' => $token,
            'data' => $siswa,
        ]);
    }

    // =================================================================
    // LOGOUT (Umum untuk semua role)
    // =================================================================
    public function logout(Request $request)
    {
        // Hapus token yang sedang digunakan saat ini
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'success' => true,
            'message' => 'Logout Berhasil',
        ]);
    }

    public function changePassword(Request $request)
    {
        // 1. Validasi Input
        $request->validate([
            'current_password' => 'required',
            'new_password'     => 'required|min:6|confirmed', // 'confirmed' berarti harus ada input 'new_password_confirmation'
        ]);

        /** @var \App\Models\Admin|\App\Models\Guru|\App\Models\Siswa $user */
        $user = Auth::user();

        // 3. Cek apakah Password Lama benar?
        if (!Hash::check($request->current_password, $user->password)) {
            return response()->json([
                'success' => false,
                'message' => 'Password lama tidak sesuai',
            ], 400);
        }

        // 4. Update Password Baru
        $user->password = Hash::make($request->new_password);
        $user->save();

        return response()->json([
            'success' => true,
            'message' => 'Password berhasil diperbarui',
        ]);
    }
}