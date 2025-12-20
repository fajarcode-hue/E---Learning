<?php

namespace App\Http\Controllers\Siswa;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Materi;
use App\Models\Mapel;
use Illuminate\Support\Facades\Storage;

class MateriController extends Controller
{
    public function index($mapel_id)
    {
        $mapel = Mapel::find($mapel_id);
        if (!$mapel) {
            return response()->json(['message' => 'Mapel tidak ditemukan'], 404);
        }

        // PERUBAHAN 1: Tambahkan ->with('latihan')
        // Ini biar database mengambil data soal yang nyambung dengan materi ini
        $materiList = Materi::where('mapel_id', $mapel_id)
                        ->with('latihan') // <--- WAJIB ADA
                        ->orderBy('created_at', 'desc')
                        ->get();

        $data = $materiList->map(function ($item) {
            return [
                'id' => $item->id,
                'judul_materi' => $item->judul_materi,
                'deskripsi' => $item->deskripsi,
                'file_url' => $item->file_path ? url('storage/' . $item->file_path) : null,
                'created_at' => $item->created_at,
                
                // PERUBAHAN 2: Masukkan data latihan ke response JSON
                // Agar di Vue nanti bisa dicek: v-if="item.latihan"
                'latihan' => $item->latihan 
            ];
        });

        return response()->json([
            'success' => true,
            'mapel' => $mapel, 
            'data' => $data
        ]);
    }

    public function show($id)
    {
        $materi = Materi::find($id);
        
        if (!$materi) {
            return response()->json(['message' => 'Materi tidak ditemukan'], 404);
        }

        return response()->json([
            'success' => true,
            'data' => [
                'id' => $materi->id,
                'judul' => $materi->judul_materi,
                'deskripsi' => $materi->deskripsi,
                'file_url' => $materi->file_path ? url('storage/' . $materi->file_path) : null,
            ]
        ]);
    }
}