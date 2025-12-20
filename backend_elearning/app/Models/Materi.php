<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Materi extends Model
{
    use HasFactory;

    protected $guarded = ['id'];

    // Relasi ke Mapel induk
    public function mapel()
    {
        return $this->belongsTo(Mapel::class);
    }

    // Relasi: Satu Materi punya SATU Latihan (One-to-One)
    public function latihan()
    {
        return $this->hasOne(Latihan::class);
    }
}