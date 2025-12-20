<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Guru;
use Illuminate\Support\Facades\Hash;

class GuruController extends Controller
{
    // LIHAT SEMUA GURU
    public function index()
    {
        $gurus = Guru::all();
        return response()->json([
            'success' => true,
            'data' => $gurus
        ]);
    }

    public function store(Request $request)
    {
        $request->validate([
            'name' => 'required',
            'email' => 'required|email|unique:gurus,email',
            'password' => 'required|min:6',
            'nip' => 'nullable|unique:gurus,nip'
        ]);

        $guru = Guru::create([
            'name' => $request->name,
            'email' => $request->email,
            'password' => Hash::make($request->password), 
            'nip' => $request->nip
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Akun Guru berhasil dibuat',
            'data' => $guru
        ]);
    }

    public function update(Request $request, $id)
    {
        $guru = Guru::find($id);

        if(!$guru) {
            return response()->json(['success' => false, 'message' => 'Guru tidak ditemukan'], 404);
        }

        $request->validate([
            'name' => 'required',
            'email' => 'required|email|unique:gurus,email,'.$id,
            'nip' => 'nullable|unique:gurus,nip,'.$id
        ]);

       $dataToUpdate = [
            'name' => $request->name,
            'email' => $request->email,
            'nip' => $request->nip,
        ];

        if($request->password) {
            $dataToUpdate['password'] = Hash::make($request->password);
        }

        $guru->update($dataToUpdate);

        return response()->json([
            'success' => true,
            'message' => 'Guru berhasil diupdate',
            'data' => $guru
        ]);
    }

    public function destroy($id)
    {
        $guru = Guru::find($id);
        
        if(!$guru) return response()->json(['message' => 'Guru tidak ditemukan'], 404);

        $guru->delete();

        return response()->json(['success' => true, 'message' => 'Guru berhasil dihapus']);
    }
}