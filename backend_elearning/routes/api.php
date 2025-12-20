<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

use App\Http\Controllers\AuthController;
use App\Http\Controllers\Admin\KelasController;
use App\Http\Controllers\Admin\GuruController;
use App\Http\Controllers\Admin\MapelController;
use App\Http\Controllers\Admin\SiswaController;
use App\Http\Controllers\Guru\MateriController; 
use App\Http\Controllers\Guru\LatihanController; 
use App\Http\Controllers\Guru\MapelController as GuruMapelController;
use App\Http\Controllers\Siswa\MapelController as SiswaMapelController;
use App\Http\Controllers\Siswa\MateriController as SiswaMateriController;
use App\Http\Controllers\Siswa\PengerjaanController;

use App\Http\Middleware\IsAdmin;
use App\Http\Middleware\IsGuru;
use App\Http\Middleware\IsSiswa;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
*/

Route::post('/login/admin', [AuthController::class, 'loginAdmin']);
Route::post('/login/guru', [AuthController::class, 'loginGuru']);
Route::post('/login/siswa', [AuthController::class, 'loginSiswa']);


Route::middleware('auth:sanctum')->group(function () {
    
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/me', function (Request $request) {
        return $request->user();
    });

    Route::middleware(['auth:sanctum'])->group(function () {
        Route::post('change-password', [AuthController::class, 'changePassword']);
        Route::post('logout', [AuthController::class, 'logout']); 
    });


    Route::middleware([IsAdmin::class])->prefix('admin')->group(function () {
        //crud kelas
        Route::apiResource('kelas', KelasController::class);
        //crud guru
        Route::apiResource('guru', GuruController::class);
        //crud siswa
        Route::apiResource('siswa', SiswaController::class);
        //crud mapel
        Route::apiResource('mapel', MapelController::class);
    });


    Route::middleware([IsGuru::class])->prefix('guru')->group(function () {
        //read all mapel
        Route::get('mapel', [GuruMapelController::class, 'index']);
        //read materi sesuai mapel
        Route::get('materi/mapel/{mapel_id}', [MateriController::class, 'index']);
        //create materi
        Route::post('materi', [MateriController::class, 'store']);
        //update materi
        Route::post('materi/{id}', [MateriController::class, 'update']);
        //delete materi
        Route::delete('materi/{id}', [MateriController::class, 'destroy']);

        //read latihan
        Route::get('latihan/materi/{materi_id}', [LatihanController::class, 'show']);
        //create latihan
        Route::post('latihan', [LatihanController::class, 'store']);
        //read history latihan per materi
        Route::get('latihan/nilai/{materi_id}', [LatihanController::class, 'historySiswa']);
        //delete latihan
        Route::delete('latihan/materi/{materi_id}', [LatihanController::class, 'destroy']);

    });

    Route::middleware([IsSiswa::class])->prefix('siswa')->group(function () {
        //read mapel
        Route::get('mapel', [SiswaMapelController::class, 'index']);
        //latihan siswa
        Route::get('latihan/materi/{materi_id}', [PengerjaanController::class, 'kerjakan']);
        //submit latihan
        Route::post('latihan/submit', [PengerjaanController::class, 'submit']); 
        //lihat history pengerjaan
        Route::get('history', [PengerjaanController::class, 'history']);
        //read materi sesuai mapel
        Route::get('materi/mapel/{mapel_id}', [SiswaMateriController::class, 'index']);
        //read materi sesuai id
        Route::get('materi/{id}', [SiswaMateriController::class, 'show']); 

    });

});