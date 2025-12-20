<?php

namespace App\Http\Controllers\Guru;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Mapel;
use Illuminate\Support\Facades\Auth;

class MapelController extends Controller
{
    public function index()
    {
        $guru_id = Auth::id(); 

        $mapels = Mapel::where('guru_id', $guru_id)
                    ->with('kelas') 
                    ->get();

        return response()->json([
            'success' => true,
            'data' => $mapels
        ]);
    }
}