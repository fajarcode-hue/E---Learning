<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../../api';

const route = useRoute();
const router = useRouter();
const mapelId = route.params.id;

const materiList = ref([]);
const mapelInfo = ref(null); // Bisa object atau null
const isLoading = ref(true);

// COMPUTED PROPERTY UNTUK NAMA MAPEL
// Ini untuk mengatasi masalah "tampil kode JSON"
const namaMapel = computed(() => {
    if (!mapelInfo.value) return 'Mata Pelajaran';
    // Cek berbagai kemungkinan nama field dari database
    return mapelInfo.value.nama_mapel || 
           mapelInfo.value.name || 
           mapelInfo.value.nama || 
           mapelInfo.value.judul || 
           'Mata Pelajaran';
});

const fetchData = async () => {
    isLoading.value = true;
    try {
        console.log(`🔵 Fetching Materi untuk Mapel ID: ${mapelId}`);
        const response = await api.get(`/siswa/materi/mapel/${mapelId}`);
        console.log("🟢 Respon API Materi:", response.data);

        const resData = response.data;

        // 1. LOGIKA MENGAMBIL LIST MATERI (Auto-Detect)
        if (resData.data && Array.isArray(resData.data)) {
            // Struktur: { data: [...] }
            materiList.value = resData.data;
        } else if (Array.isArray(resData)) {
            // Struktur: [...] (Langsung Array)
            materiList.value = resData;
        } else if (resData.materi && Array.isArray(resData.materi)) {
             // Struktur: { materi: [...] } (Kadang backend pakai nama key spesifik)
            materiList.value = resData.materi;
        } else {
            console.warn("⚠️ Array materi tidak ditemukan di respon API");
            materiList.value = [];
        }

        // 2. LOGIKA MENGAMBIL INFO MAPEL
        if (resData.mapel) {
            mapelInfo.value = resData.mapel;
        } else if (resData.data && resData.data.mapel) {
            mapelInfo.value = resData.data.mapel;
        } else if (materiList.value.length > 0 && materiList.value[0].mapel) {
            // Fallback: Ambil info mapel dari item materi pertama jika ada relasi
            mapelInfo.value = materiList.value[0].mapel;
        }

    } catch (error) {
        console.error("🔴 Gagal load materi:", error);
        if(error.response?.status === 403 || error.response?.status === 404) {
            alert("Mata pelajaran tidak ditemukan atau akses ditolak.");
            router.push('/siswa/dashboard');
        }
    } finally {
        isLoading.value = false;
    }
};

const kerjakanLatihan = (materiId) => {
    router.push(`/siswa/latihan/materi/${materiId}`);
};

const openFile = (url) => {
    // Pastikan URL valid
    if (url) window.open(url, '_blank');
    else alert("Link file tidak valid.");
};

onMounted(() => {
    fetchData();
});
</script>

<template>
  <div class="container">
    <!-- Header -->
    <div class="header-wrapper">
        <button @click="router.push('/siswa/dashboard')" class="btn-back" title="Kembali ke Dashboard">
            <span class="icon">➜</span> 
        </button>
        <div class="header-text">
            <!-- GUNAKAN COMPUTED PROPERTY DI SINI -->
            <h2 class="title">{{ namaMapel }}</h2>
            <p class="subtitle">Daftar materi dan latihan yang tersedia.</p>
        </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="loading">
        <div class="spinner"></div>
        <p>Sedang memuat materi...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="materiList.length === 0" class="empty-state">
        <img src="https://img.icons8.com/ios/100/cccccc/empty-box.png" alt="Kosong" style="width: 60px; opacity: 0.5; margin-bottom: 10px;">
        <p>Belum ada materi yang diupload oleh guru untuk mata pelajaran ini.</p>
    </div>

    <!-- Timeline Content -->
    <div v-else class="timeline-container">
        <!-- Garis Vertikal -->
        <div class="timeline-line"></div>

        <div v-for="(item, index) in materiList" :key="item.id" class="timeline-item">
            
            <!-- Marker Nomor -->
            <div class="timeline-marker">{{ index + 1 }}</div>
            
            <!-- Card Isi -->
            <div class="timeline-content">
                <div class="content-header">
                    <!-- Gunakan logika OR (||) untuk jaga-jaga nama field beda -->
                    <h3>{{ item.judul_materi || item.judul || item.title }}</h3>
                    <span class="date">
                        {{ item.created_at ? new Date(item.created_at).toLocaleDateString('id-ID', { day: 'numeric', month: 'long', year: 'numeric' }) : '-' }}
                    </span>
                </div>

                <div class="study-section">
                    <p class="deskripsi">{{ item.deskripsi || item.isi || 'Tidak ada deskripsi tambahan.' }}</p>
                    
                    <!-- Tombol File Materi -->
                    <button v-if="item.file_url || item.link_file" 
                            @click="openFile(item.file_url || item.link_file)" 
                            class="btn-file">
                        📄 Lihat/Download Materi
                    </button>
                </div>

                <!-- Bagian Latihan / Quiz -->
                <!-- Cek apakah ada latihan (bisa boolean atau object/array) -->
                <div v-if="item.latihan || item.has_latihan" class="action-section">
                    <div class="divider"></div>
                    <div class="latihan-box">
                        <div class="latihan-info">
                            <strong>⚡ Latihan Soal Tersedia</strong>
                            <small>Uji pemahamanmu sekarang.</small>
                        </div>
                        <button @click="kerjakanLatihan(item.id)" class="btn-kerjakan">
                            Mulai Mengerjakan ➜
                        </button>
                    </div>
                </div>
                
                <div v-else class="footer-empty">
                    <small>📌 Hanya materi bacaan, tidak ada tugas.</small>
                </div>

            </div>
        </div>
    </div>
  </div>
</template>

<style scoped>
.container { max-width: 900px; margin: 0 auto; padding: 2rem; font-family: 'Segoe UI', sans-serif; }

/* --- HEADER --- */
.header-wrapper {
    display: flex;
    align-items: center; 
    gap: 20px;
    margin-bottom: 3rem;
    padding-bottom: 1.5rem;
    border-bottom: 1px solid #eee;
}

.btn-back {
    width: 45px; height: 45px;
    border-radius: 50%;
    background: white;
    border: 1px solid #dfe6e9;
    display: flex; justify-content: center; align-items: center;
    cursor: pointer;
    transition: all 0.2s;
    box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}
.btn-back .icon {
    display: inline-block;
    transform: rotate(180deg); /* Panah ke kiri */
    font-size: 1.2rem;
    color: #636e72;
    font-weight: bold;
}
.btn-back:hover {
    background: #f1f2f6;
    border-color: #b2bec3;
    transform: translateX(-3px);
}

.header-text .title { margin: 0; color: #2d3436; font-size: 1.8rem; font-weight: 700; }
.header-text .subtitle { margin: 5px 0 0; color: #636e72; font-size: 0.95rem; }


/* --- TIMELINE --- */
.timeline-container {
    position: relative;
    padding-left: 10px;
}

/* Garis Timeline */
.timeline-line {
    position: absolute;
    left: 30px; 
    top: 20px; 
    bottom: 0;
    width: 3px;
    background-color: #e9ecef;
    transform: translateX(-50%);
    z-index: 0;
}

.timeline-item {
    display: flex;
    gap: 25px;
    position: relative;
    margin-bottom: 2.5rem;
    z-index: 1; 
}

/* Bulatan Nomor */
.timeline-marker {
    width: 40px; height: 40px;
    background: #3498db;
    color: white;
    border-radius: 50%;
    display: flex; justify-content: center; align-items: center;
    font-weight: bold; font-size: 1.1rem;
    flex-shrink: 0;
    border: 4px solid #fff; 
    box-shadow: 0 0 0 2px #e9ecef; /* Double border effect */
}

/* Card Content */
.timeline-content {
    flex: 1;
    background: white;
    border-radius: 12px;
    padding: 1.5rem;
    box-shadow: 0 4px 20px rgba(0,0,0,0.06);
    border: 1px solid #f8f9fa;
    transition: transform 0.2s, box-shadow 0.2s;
}
.timeline-content:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

.content-header { display: flex; justify-content: space-between; margin-bottom: 1rem; flex-wrap: wrap; gap: 10px; border-bottom: 1px solid #f1f2f6; padding-bottom: 10px; }
.content-header h3 { margin: 0; color: #2c3e50; font-size: 1.25rem; font-weight: 600; }
.date { font-size: 0.8rem; color: #95a5a6; display: flex; align-items: center; }

.deskripsi { color: #555; line-height: 1.6; margin-bottom: 1.2rem; font-size: 0.95rem; }

.btn-file {
    background-color: #f0f9ff; color: #0984e3; 
    border: 1px solid #0984e3;
    padding: 8px 16px; border-radius: 6px; cursor: pointer; font-weight: 600;
    transition: 0.2s; display: inline-flex; align-items: center; gap: 8px; font-size: 0.9rem; text-decoration: none;
}
.btn-file:hover { background-color: #0984e3; color: white; }

/* Bagian Bawah Card (Latihan) */
.divider { border-top: 1px dashed #dfe6e9; margin: 1.5rem 0 1rem 0; }

.latihan-box {
    background: linear-gradient(to right, #f0fff4, #fff);
    border-left: 4px solid #2ecc71;
    padding: 15px;
    border-radius: 6px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 15px;
}

.latihan-info { display: flex; flex-direction: column; }
.latihan-info strong { color: #27ae60; font-size: 1rem; margin-bottom: 2px; }
.latihan-info small { color: #7f8c8d; }

.btn-kerjakan {
    background: #2ecc71; color: white; border: none; padding: 10px 24px;
    border-radius: 50px; cursor: pointer; font-weight: bold; letter-spacing: 0.5px;
    box-shadow: 0 4px 10px rgba(46, 204, 113, 0.3); transition: 0.2s;
}
.btn-kerjakan:hover { background: #27ae60; transform: translateY(-2px); box-shadow: 0 6px 15px rgba(46, 204, 113, 0.4); }

.footer-empty { margin-top: 1rem; text-align: right; color: #b2bec3; font-style: italic; }

/* Loading & Empty */
.loading, .empty-state { text-align: center; padding: 4rem 1rem; color: #b2bec3; }
.spinner { width: 40px; height: 40px; border: 4px solid #f3f3f3; border-top: 4px solid #3498db; border-radius: 50%; margin: 0 auto 15px; animation: spin 1s linear infinite; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

/* Mobile Responsive */
@media (max-width: 600px) {
    .container { padding: 1rem; }
    .header-wrapper { flex-direction: column; align-items: flex-start; gap: 15px; }
    .timeline-item { gap: 15px; }
    .timeline-marker { width: 32px; height: 32px; font-size: 0.9rem; }
    .timeline-line { left: 26px; }
    .content-header h3 { font-size: 1.1rem; }
    .latihan-box { flex-direction: column; align-items: flex-start; }
    .btn-kerjakan { width: 100%; }
}
</style>