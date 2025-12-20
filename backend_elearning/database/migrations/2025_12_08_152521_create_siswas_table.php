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
       Schema::create('siswas', function (Blueprint $table) {
            $table->id();
            $table->string('nis')->unique()->nullable();
            $table->string('name');
            $table->string('email')->unique();
            $table->string('password');
            
            // Relasi ke tabel Kelas
            // onDelete('cascade') berarti jika kelas dihapus, siswa di dalamnya ikut terhapus (opsional)
            // atau gunakan onDelete('set null') jika ingin data siswa tetap ada tapi tanpa kelas.
            $table->foreignId('kelas_id')->constrained('kelas')->onDelete('cascade');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('siswas');
    }
};
