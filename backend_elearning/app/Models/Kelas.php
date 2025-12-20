<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Kelas extends Model
{
    use HasFactory;

    protected $table = 'kelas'; // Explicit define table name (karena plural 'kelas' kadang ambigu)
    protected $guarded = ['id'];

    // Relasi: Satu Kelas punya banyak Siswa
    public function siswas()
    {
        return $this->hasMany(Siswa::class);
    }

    // Relasi: Satu Kelas punya banyak Mapel
    public function mapels()
    {
        return $this->hasMany(Mapel::class);
    }
}