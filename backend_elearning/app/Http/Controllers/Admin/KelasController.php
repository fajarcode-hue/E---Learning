<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Kelas;

class KelasController extends Controller
{
    // 1. LIHAT SEMUA KELAS
    public function index()
    {
        $kelas = Kelas::all();
        return response()->json([
            'success' => true,
            'data' => $kelas
        ]);
    }

    // 2. TAMBAH KELAS BARU
    public function store(Request $request)
    {
        $request->validate([
            'nama_kelas' => 'required|string|unique:kelas,nama_kelas'
        ]);

        $kelas = Kelas::create([
            'nama_kelas' => $request->nama_kelas
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Kelas berhasil dibuat',
            'data' => $kelas
        ]);
    }

    // 3. LIHAT DETAIL 1 KELAS (Opsional)
    public function show($id)
    {
        $kelas = Kelas::find($id);
        
        if(!$kelas) {
            return response()->json(['success' => false, 'message' => 'Kelas tidak ditemukan'], 404);
        }

        return response()->json(['success' => true, 'data' => $kelas]);
    }

    // 4. UPDATE KELAS
    public function update(Request $request, $id)
    {
        $kelas = Kelas::find($id);

        if(!$kelas) {
            return response()->json(['success' => false, 'message' => 'Kelas tidak ditemukan'], 404);
        }

        $request->validate([
            'nama_kelas' => 'required|string|unique:kelas,nama_kelas,'.$id // Ignore unique cek untuk ID ini
        ]);

        $kelas->update([
            'nama_kelas' => $request->nama_kelas
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Kelas berhasil diupdate',
            'data' => $kelas
        ]);
    }

    // 5. HAPUS KELAS
    public function destroy($id)
    {
        $kelas = Kelas::find($id);

        if(!$kelas) {
            return response()->json(['success' => false, 'message' => 'Kelas tidak ditemukan'], 404);
        }

        // Ingat! Karena kita pakai onDelete('cascade') di migrasi, 
        // menghapus kelas akan menghapus semua siswa di dalamnya.
        $kelas->delete();

        return response()->json([
            'success' => true,
            'message' => 'Kelas berhasil dihapus'
        ]);
    }
}