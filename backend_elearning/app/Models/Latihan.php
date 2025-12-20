<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Latihan extends Model
{
    use HasFactory;

    protected $guarded = ['id'];


    protected $casts = [
        'soal' => 'array',           
        'kunci_jawaban' => 'array'
    ];

    public function materi()
    {
        return $this->belongsTo(Materi::class);
    }

    public function historyLatihans()
    {
        return $this->hasMany(HistoryLatihan::class);
    }
}