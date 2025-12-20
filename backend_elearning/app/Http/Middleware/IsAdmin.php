<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;
use App\Models\Admin; 

class IsAdmin
{
    public function handle(Request $request, Closure $next): Response
    {
        if (! $request->user() || ! $request->user() instanceof Admin) {
            return response()->json([
                'success' => false,
                'message' => 'Akses Ditolak. Halaman ini khusus Admin.'
            ], 403);
        }

        return $next($request);
    }
}