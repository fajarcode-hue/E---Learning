<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;
use App\Models\User; // <--- BACA CATATAN DI BAWAH

class AdminSeeder extends Seeder
{
    public function run(): void
    {

        User::updateOrCreate(
            ['email' => 'contoh@sekolah.id'],
            [
                'name'     => 'Administrator',
                'password' => Hash::make('password123'),

            ]
        );
    }
}
