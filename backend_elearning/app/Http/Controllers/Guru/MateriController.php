<?php

namespace App\Http\Controllers\Guru;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Materi;
use App\Models\Mapel;
// 1. Import Facade Auth
use Illuminate\Support\Facades\Auth; 
use Illuminate\Support\Facades\Storage; 

class MateriController extends Controller
{
    public function index($mapel_id)
    {
        $mapel = Mapel::where('id', $mapel_id)
                      ->where('guru_id', Auth::id()) 
                      ->first();

        if (!$mapel) {
            return response()->json(['message' => 'Mapel tidak ditemukan atau Anda bukan pengajarnya'], 403);
        }

        $materis = Materi::where('mapel_id', $mapel_id)->with('latihan')->get();

        return response()->json([
            'success' => true,
            'mapel' => $mapel,
            'data' => $materis
        ]);
    }

    public function store(Request $request)
    {
        $request->validate([
            'mapel_id' => 'required|exists:mapels,id',
            'judul_materi' => 'required|string',
            'deskripsi' => 'required|string',
            'file_path' => 'nullable|file|mimes:pdf,doc,docx,ppt,pptx|max:10240' 
        ]);

        $isMyMapel = Mapel::where('id', $request->mapel_id)
                          ->where('guru_id', Auth::id()) 
                          ->exists();

        if (!$isMyMapel) {
            return response()->json(['message' => 'Anda tidak berhak menambahkan materi di mapel ini'], 403);
        }
        $path = null;
        if ($request->hasFile('file_path')) {
            $path = $request->file('file_path')->store('materi', 'public');
        }

        $materi = Materi::create([
            'mapel_id' => $request->mapel_id,
            'judul_materi' => $request->judul_materi,
            'deskripsi' => $request->deskripsi,
            'file_path' => $path 
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Materi berhasil dibuat',
            'data' => $materi
        ]);
    }

    public function update(Request $request, $id)
    {
        $materi = Materi::find($id);

        if (!$materi) {
            return response()->json(['message' => 'Materi tidak ditemukan'], 404);
        }

        if ($materi->mapel->guru_id !== Auth::id()) {
            return response()->json(['message' => 'Anda tidak berhak mengedit materi ini'], 403);
        }

        $request->validate([
            'judul_materi' => 'required|string',
            'deskripsi'    => 'required|string',
            'file_path'    => 'nullable|file|mimes:pdf,doc,docx,ppt,pptx,png,jpg,jpeg|max:2048' 
        ]);

        if ($request->hasFile('file_path')) {
            if ($materi->file_path && Storage::exists($materi->file_path)) {
                Storage::delete($materi->file_path);
            }
            
            $path = $request->file('file_path')->store('uploads/materi', 'public');
            $materi->file_path = $path;
        }

        $materi->judul_materi = $request->judul_materi;
        $materi->deskripsi = $request->deskripsi;
        $materi->save();

        return response()->json([
            'success' => true,
            'message' => 'Materi berhasil diperbarui',
            'data' => $materi
        ]);
    }

    public function destroy($id)
    {
        $materi = Materi::find($id);

        if (!$materi) return response()->json(['message' => 'Materi tidak ditemukan'], 404);

        if ($materi->mapel->guru_id !== Auth::id()) {
            return response()->json(['message' => 'Anda tidak berhak menghapus materi ini'], 403);
        }

        if ($materi->file_path) {
            Storage::disk('public')->delete($materi->file_path);
        }

        $materi->delete();

        return response()->json(['success' => true, 'message' => 'Materi berhasil dihapus']);
    }
}