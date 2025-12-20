<?php

namespace App\Http\Controllers\Guru;

use App\Http\Controllers\Controller;
use App\Models\HistoryLatihan;
use Illuminate\Http\Request;
use App\Models\Latihan;
use App\Models\Materi;
use Illuminate\Support\Facades\Auth;

class LatihanController extends Controller
{
    public function show($materi_id)
    {
        // Cek materi
        $materi = Materi::with('mapel')->find($materi_id);
        
        if (!$materi) {
            return response()->json(['message' => 'Materi tidak ditemukan'], 404);
        }

        // Security: Pastikan yang lihat adalah Guru pengajar mapel tersebut
        if ($materi->mapel->guru_id !== Auth::id()) {
            return response()->json(['message' => 'Anda tidak memiliki akses ke materi ini'], 403);
        }

        // Ambil latihan
        $latihan = Latihan::where('materi_id', $materi_id)->first();

        if (!$latihan) {
            return response()->json(['success' => true, 'data' => null, 'message' => 'Belum ada soal latihan']);
        }

        return response()->json([
            'success' => true,
            'data' => $latihan 
        ]);
    }

    // 2. BUAT ATAU UPDATE LATIHAN (Smart Save)
    public function store(Request $request)
    {
        $request->validate([
            'materi_id' => 'required|exists:materis,id',
            'soal' => 'required|array',          // Wajib Array JSON
            'kunci_jawaban' => 'required|array'  // Wajib Array JSON
        ]);

        // A. Validasi Kepemilikan (Security)
        $materi = Materi::with('mapel')->find($request->materi_id);
        
        // Apakah guru yang login adalah pemilik mapel materi ini?
        if ($materi->mapel->guru_id !== Auth::id()) {
            return response()->json(['message' => 'Anda bukan pengajar mapel ini, dilarang membuat soal!'], 403);
        }

        // B. Eksekusi Simpan (Create or Update)
        // Logika: Cari data berdasarkan 'materi_id'. 
        // Kalau ketemu -> Update kolom soal & kunci.
        // Kalau tidak ketemu -> Buat baris baru.
        
        $latihan = Latihan::updateOrCreate(
            ['materi_id' => $request->materi_id], // Kunci pencarian
            [
                'soal' => $request->soal,           
                'kunci_jawaban' => $request->kunci_jawaban
            ]
        );

        return response()->json([
            'success' => true,
            'message' => 'Latihan berhasil disimpan/diupdate',
            'data' => $latihan
        ]);
    }

    public function destroy($materi_id)
    {
        // 1. Cek Materi & Validasi Kepemilikan (Security)
        $materi = Materi::with('mapel')->find($materi_id);

        if (!$materi) {
            return response()->json(['message' => 'Materi tidak ditemukan'], 404);
        }

        if ($materi->mapel->guru_id !== Auth::id()) {
            return response()->json(['message' => 'Anda tidak berhak menghapus latihan ini'], 403);
        }

        // 2. Cari Latihannya
        $latihan = Latihan::where('materi_id', $materi_id)->first();

        if (!$latihan) {
            return response()->json(['message' => 'Latihan tidak ditemukan'], 404);
        }

        // 3. Hapus
        // Peringatan: Ini akan menghapus history nilai siswa juga jika relasi database di-set cascade
        $latihan->delete();

        return response()->json([
            'success' => true,
            'message' => 'Latihan berhasil dihapus'
        ]);
    }

    public function historySiswa($materi_id)
    {
        // 1. Validasi Materi & Guru (Security)
        $materi = Materi::with('mapel')->find($materi_id);
        if (!$materi) return response()->json(['message' => 'Materi tidak ditemukan'], 404);

        if ($materi->mapel->guru_id !== Auth::id()) {
            return response()->json(['message' => 'Anda tidak berhak melihat nilai ini'], 403);
        }

        // 2. Ambil Latihannya dulu
        $latihan = Latihan::where('materi_id', $materi_id)->first();
        if (!$latihan) return response()->json(['message' => 'Belum ada latihan di materi ini'], 404);

        // 3. Ambil History berdasarkan latihan_id
        // Load relasi 'siswa' agar nama siswanya muncul
        $nilaiSiswa = HistoryLatihan::where('latihan_id', $latihan->id)
                        ->with('siswa') 
                        ->orderBy('skor', 'desc') // Ranking nilai tertinggi
                        ->get();

        return response()->json([
            'success' => true,
            'data' => $nilaiSiswa
        ]);
    }
}