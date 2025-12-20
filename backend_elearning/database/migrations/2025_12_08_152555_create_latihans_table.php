<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
       Schema::create('latihans', function (Blueprint $table) {
            $table->id();
            // Relasi One-to-One ke Materi
            // Kita tambahkan ->unique() agar satu materi hanya bisa punya satu latihan
            $table->foreignId('materi_id')->unique()->constrained('materis')->onDelete('cascade');
            
            $table->longText('soal'); // Gunakan longText agar bisa menampung banyak karakter/JSON
            $table->text('kunci_jawaban')->nullable();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('latihans');
    }
};
