<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Siswa;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class SiswaController extends Controller
{
    public function index()
    {
        $siswas = Siswa::with('kelas') -> get();
        return response()->json([
            'success' => true,
            'data' => $siswas
        ]);
    }

    public function store(Request $request)
    {
        $request->validate([
            'nis' => 'nullable|unique:siswas,nis',
            'name' => 'required',
            'email' => 'required|email|unique:siswas,email',
            'password' => 'required|min:6',
            'kelas_id' => 'required|exists:kelas,id'

        ]);

        $siswa = Siswa::create([
            'nis' => $request->nis,
            'name' => $request->name,
            'email' => $request->email,
            'password' => Hash::make($request->password),
            'kelas_id' => $request->kelas_id
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Akun Siswa berhasil dibuat',
            'data' => $siswa
        ]);
    }

    public function update(Request $request, $id)
    {
        $siswa = Siswa::find($id);

        if(!$siswa) {
            return response()->json(['success' => false, 'message' => 'siswa tidak ditemukan'], 404);
        }

        $request->validate([
            'nis' => 'nullable|unique:siswas,nis,'.$id,
            'name' => 'required',
            'email' => 'required|email|unique:siswas,email,'.$id,
            'kelas_id' => 'required|exists:kelas,id'
        ]);

        $dataToUpdate = [
            'nis' => $request->nis,
            'name' => $request->name,
            'email' => $request->email,
            'kelas_id'=> $request->kelas_id,
        ];

        if($request->password) {
            $dataToUpdate['password'] = Hash::make($request->password);
        }

        $siswa->update($dataToUpdate);

        return response()->json([
            'success' => true,
            'message' => 'Siswa Berhasil disimpan',
            'data' => $siswa
        ]);
    }

    public function destroy($id)
    {
        $siswa = Siswa::find($id);

        if(!$siswa) return response()->json(['message' => 'Siswa tidak ditemukan'], 404);

        $siswa->delete();

        return response()->json(['succes' => true, 'message' => 'Siswa Berhasil dihapus']);
    }
}
