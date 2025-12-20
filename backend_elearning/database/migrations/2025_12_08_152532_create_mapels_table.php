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
       Schema::create('mapels', function (Blueprint $table) {
            $table->id();
            $table->string('nama_mapel');
            // Relasi ke Kelas
            $table->foreignId('kelas_id')->constrained('kelas')->onDelete('cascade');
            // Relasi ke Guru
            $table->foreignId('guru_id')->constrained('gurus')->onDelete('cascade');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('mapels');
    }
};
