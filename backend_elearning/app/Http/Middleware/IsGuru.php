<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;
use App\Models\Guru; // Import Model Guru

class IsGuru
{
    public function handle(Request $request, Closure $next): Response
    {
        // Cek apakah user login DAN apakah dia instance dari Guru
        if (! $request->user() || ! $request->user() instanceof Guru) {
            return response()->json([
                'success' => false,
                'message' => 'Akses Ditolak. Halaman ini khusus Guru.'
            ], 403);
        }

        return $next($request);
    }
}