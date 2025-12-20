<?php

namespace App\Http\Controllers\Siswa;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Mapel;
use Illuminate\Support\Facades\Auth;

class MapelController extends Controller
{
    public function index()
    {
        /** @var Siswa $siswa */
        $siswa = Auth::user();

        $siswa->load('kelas'); 

        $kelas_id = $siswa->kelas_id;

        $mapel = Mapel::where('kelas_id', $kelas_id)
                    ->with('guru') 
                    ->get();

        return response()->json([
            'success' => true,
            'info_siswa' => $siswa, 
            'data' => $mapel
        ]);
    }
}