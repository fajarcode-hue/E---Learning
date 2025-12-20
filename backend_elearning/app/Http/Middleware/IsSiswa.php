<?php

namespace App\Http\Middleware;

use App\Models\Siswa;
use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class IsSiswa
{
    public function handle(Request $request, Closure $next): Response
    {
        if (! $request->user() || ! $request->user() instanceof Siswa) {
            return response()->json([
                'success' => false,
                'message' => 'Akses Ditolak. Halaman ini khusus Guru.'
            ], 403);
        }

        return $next($request);
    }
}
