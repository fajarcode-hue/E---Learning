<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Mapel;
use Illuminate\Http\Request;

class MapelController extends Controller
{
    // LIHAT SEMUA MAPEL
    public function index()
    {
        // Kita load relasi 'guru' dan 'kelas' supaya yang muncul bukan cuma ID angka,
        // tapi juga nama guru dan nama kelasnya (agar Admin mudah baca json-nya)
        $mapels = Mapel::with(['guru', 'kelas'])->get();

        return response()->json([
            'success' => true,
            'data' => $mapels
        ]);
    }

    // TAMBAH MAPEL (Penugasan Guru ke Kelas)
    public function store(Request $request)
    {
        $request->validate([
            'nama_mapel' => 'required|string',
            'guru_id' => 'required|exists:gurus,id', // Guru harus valid
            'kelas_id' => 'required|exists:kelas,id', // Kelas harus valid
        ]);

        // Opsional: Cek apakah guru ini sudah mengajar mapel yg sama di kelas yg sama?
        // Agar tidak duplikat data.
        $exists = Mapel::where('nama_mapel', $request->nama_mapel)
                       ->where('kelas_id', $request->kelas_id)
                       ->exists();
        
        if($exists) {
            return response()->json(['message' => 'Mapel ini sudah ada di kelas tersebut'], 400);
        }

        $mapel = Mapel::create([
            'nama_mapel' => $request->nama_mapel,
            'guru_id' => $request->guru_id,
            'kelas_id' => $request->kelas_id
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Mapel berhasil ditambahkan',
            'data' => $mapel
        ]);
    }

    // UPDATE MAPEL
    public function update(Request $request, $id)
    {
        $mapel = Mapel::find($id);

        if(!$mapel) return response()->json(['message' => 'Mapel tidak ditemukan'], 404);

        $request->validate([
            'nama_mapel' => 'required|string',
            'guru_id' => 'required|exists:gurus,id',
            'kelas_id' => 'required|exists:kelas,id',
        ]);

        $mapel->update([
            'nama_mapel' => $request->nama_mapel,
            'guru_id' => $request->guru_id,
            'kelas_id' => $request->kelas_id
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Mapel berhasil diupdate',
            'data' => $mapel
        ]);
    }

    // HAPUS MAPEL
    public function destroy($id)
    {
        $mapel = Mapel::find($id);
        if(!$mapel) return response()->json(['message' => 'Mapel tidak ditemukan'], 404);

        $mapel->delete();

        return response()->json(['success' => true, 'message' => 'Mapel berhasil dihapus']);
    }
}