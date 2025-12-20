<?php

namespace App\Http\Controllers\Siswa;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Latihan;
use App\Models\HistoryLatihan;
use Illuminate\Support\Facades\Auth;

class PengerjaanController extends Controller
{
    public function kerjakan($materi_id)
    {
        $latihan = Latihan::where('materi_id', $materi_id)->first();

        if (!$latihan) {
            return response()->json(['message' => 'Latihan belum tersedia untuk materi ini'], 404);
        }
        $latihan->makeHidden(['kunci_jawaban']);

        return response()->json([
            'success' => true,
            'data' => $latihan
        ]);
    }

    public function submit(Request $request)
    {
        $request->validate([
            'latihan_id' => 'required|exists:latihans,id',
            'jawaban_siswa' => 'required|array' 
        ]);

        $latihan = Latihan::find($request->latihan_id);
        $kunci_jawaban = $latihan->kunci_jawaban; 
        $jawaban_siswa = $request->jawaban_siswa; 

        $benar = 0;
        $total_soal = count($kunci_jawaban);

        foreach ($kunci_jawaban as $id_soal => $kunci) {
            if (isset($jawaban_siswa[$id_soal])) {
                if ($jawaban_siswa[$id_soal] == $kunci) {
                    $benar++;
                }
            }
        }

        $score = ($benar / $total_soal) * 100;

        $history = HistoryLatihan::create([
            'siswa_id' => Auth::id(),
            'latihan_id' => $latihan->id,
            'skor' => $score,
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Latihan selesai dikerjakan',
            'nilai' => $score,
            'benar' => $benar,
            'total_soal' => $total_soal
        ]);
    }

    public function history()
    {
        $histories = HistoryLatihan::where('siswa_id', Auth::id())
            ->with(['latihan.materi.mapel']) 
            ->orderBy('created_at', 'desc')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $histories
        ]);
    }
}