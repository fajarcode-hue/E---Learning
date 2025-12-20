<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class HistoryLatihan extends Model
{
    use HasFactory;

    protected $guarded = ['id'];

    // Relasi: Milik Siswa siapa?
    public function siswa()
    {
        return $this->belongsTo(Siswa::class);
    }

    // Relasi: Mengerjakan Latihan yang mana?
    public function latihan()
    {
        return $this->belongsTo(Latihan::class);
    }
}