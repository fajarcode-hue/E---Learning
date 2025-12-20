<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Mapel extends Model
{
    use HasFactory;

    protected $guarded = ['id'];

    // Relasi ke Guru (Milik siapa mapel ini?)
    public function guru()
    {
        return $this->belongsTo(Guru::class);
    }

    // Relasi ke Kelas (Untuk kelas mana mapel ini?)
    public function kelas()
    {
        return $this->belongsTo(Kelas::class);
    }

    // Relasi: Satu Mapel punya banyak Materi
    public function materis()
    {
        return $this->hasMany(Materi::class);
    }
}