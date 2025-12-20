<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../../api';

const route = useRoute();
const router = useRouter();
const mapelId = route.params.id;

const materiList = ref([]);
const mapelInfo = ref(null);
const isLoading = ref(true);

const fetchData = async () => {
    isLoading.value = true;
    try {
        const response = await api.get(`/siswa/materi/mapel/${mapelId}`);
        if (response.data.success) {
            materiList.value = response.data.data;
            mapelInfo.value = response.data.mapel;
        }
    } catch (error) {
        console.error("Gagal load materi:", error);
        if(error.response?.status === 403) {
            alert("Anda tidak memiliki akses ke mapel ini.");
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
    window.open(url, '_blank');
};

onMounted(() => {
    fetchData();
});
</script>

<template>
  <div class="container">
    <div class="header-wrapper">
        <button @click="router.push('/siswa/dashboard')" class="btn-back" title="Kembali ke Dashboard">
            <span class="icon">➜</span> </button>
        <div class="header-text">
            <h2 class="title">{{ mapelInfo || 'Mata Pelajaran' }}</h2>
            <p class="subtitle">Daftar materi dan latihan yang tersedia.</p>
        </div>
    </div>

    <div v-if="isLoading" class="loading">
        <div class="spinner"></div>
        <p>Sedang memuat materi...</p>
    </div>

    <div v-else-if="materiList.length === 0" class="empty-state">
        <p>Belum ada materi yang diupload oleh guru.</p>
    </div>

    <div v-else class="timeline-container">
        <div class="timeline-line"></div>

        <div v-for="(item, index) in materiList" :key="item.id" class="timeline-item">
            
            <div class="timeline-marker">{{ index + 1 }}</div>
            
            <div class="timeline-content">
                <div class="content-header">
                    <h3>{{ item.judul_materi }}</h3>
                    <span class="date">{{ new Date(item.created_at).toLocaleDateString('id-ID') }}</span>
                </div>

                <div class="study-section">
                    <p class="deskripsi">{{ item.deskripsi || 'Tidak ada deskripsi tambahan.' }}</p>
                    
                    <button v-if="item.file_url" @click="openFile(item.file_url)" class="btn-file">
                        📄 Lihat Materi
                    </button>
                </div>

                <div v-if="item.latihan" class="action-section">
                    <div class="divider"></div>
                    <div class="latihan-box">
                        <div class="latihan-info">
                            <strong>Latihan Soal Tersedia</strong>
                            <small>Uji pemahamanmu sekarang.</small>
                        </div>
                        <button @click="kerjakanLatihan(item.id)" class="btn-kerjakan">
                            Mulai ➜
                        </button>
                    </div>
                </div>
                
                <div v-else class="footer-empty">
                    <small>Belum ada tugas.</small>
                </div>

            </div>
        </div>
    </div>
  </div>
</template>

<style scoped>
.container { max-width: 900px; margin: 0 auto; padding: 2rem; font-family: 'Segoe UI', sans-serif; }

/* --- HEADER YANG DIPERBAIKI --- */
.header-wrapper {
    display: flex;
    align-items: center; /* Vertically center items */
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
    transform: rotate(180deg); /* Memutar panah ke kiri */
    font-size: 1.2rem;
    color: #636e72;
    font-weight: bold;
}
.btn-back:hover {
    background: #f1f2f6;
    border-color: #b2bec3;
    transform: translateX(-3px); /* Efek gerak sedikit ke kiri */
}

.header-text .title { margin: 0; color: #2d3436; font-size: 1.8rem; }
.header-text .subtitle { margin: 5px 0 0; color: #636e72; font-size: 0.95rem; }


/* --- TIMELINE YANG LEBIH RAPI --- */
.timeline-container {
    position: relative;
    padding-left: 10px; /* Space agar tidak mepet kiri layar */
}

/* Garis Timeline Absolute */
.timeline-line {
    position: absolute;
    left: 30px; /* (Width Marker / 2) + padding-container */
    top: 20px; /* Mulai dari tengah marker pertama */
    bottom: 0;
    width: 3px;
    background-color: #dfe6e9;
    transform: translateX(-50%); /* Trik jitu centering */
    z-index: 0;
}

.timeline-item {
    display: flex;
    gap: 25px;
    position: relative;
    margin-bottom: 2.5rem;
    z-index: 1; /* Di atas garis */
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
    border: 4px solid #f8f9fa; /* Border tebal warna background halaman agar terlihat terpisah dari garis */
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

/* Card Content */
.timeline-content {
    flex: 1;
    background: white;
    border-radius: 12px;
    padding: 1.5rem;
    box-shadow: 0 4px 15px rgba(0,0,0,0.05);
    border: 1px solid #f1f2f6;
    transition: transform 0.2s;
}
.timeline-content:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0,0,0,0.08);
}

.content-header { display: flex; justify-content: space-between; margin-bottom: 1rem; flex-wrap: wrap; gap: 10px; }
.content-header h3 { margin: 0; color: #2c3e50; font-size: 1.2rem; }
.date { font-size: 0.8rem; color: #b2bec3; background: #f8f9fa; padding: 2px 8px; border-radius: 4px; height: fit-content; }

.deskripsi { color: #555; line-height: 1.6; margin-bottom: 1.2rem; font-size: 0.95rem; }

.btn-file {
    background-color: #fff; color: #3498db; 
    border: 1px solid #3498db;
    padding: 8px 16px; border-radius: 6px; cursor: pointer; font-weight: 600;
    transition: 0.2s; display: inline-flex; align-items: center; gap: 8px; font-size: 0.9rem;
}
.btn-file:hover { background-color: #3498db; color: white; }

/* Bagian Bawah Card (Latihan) */
.divider { border-top: 1px dashed #dfe6e9; margin: 1.5rem 0 1rem 0; }

.latihan-box {
    background: linear-gradient(to right, #e8f8f5, #fff);
    border-left: 4px solid #2ecc71;
    padding: 12px 20px;
    border-radius: 8px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 15px;
}

.latihan-info { display: flex; flex-direction: column; }
.latihan-info strong { color: #27ae60; font-size: 1rem; }
.latihan-info small { color: #7f8c8d; }

.btn-kerjakan {
    background: #2ecc71; color: white; border: none; padding: 10px 24px;
    border-radius: 30px; cursor: pointer; font-weight: bold;
    box-shadow: 0 4px 10px rgba(46, 204, 113, 0.3); transition: 0.2s;
}
.btn-kerjakan:hover { background: #27ae60; transform: translateY(-2px); box-shadow: 0 6px 15px rgba(46, 204, 113, 0.4); }

.footer-empty { margin-top: 1.5rem; text-align: right; border-top: 1px solid #f1f2f6; padding-top: 10px; color: #b2bec3; }

/* Loading & Empty */
.loading, .empty-state { text-align: center; padding: 4rem; color: #b2bec3; }
.spinner { width: 30px; height: 30px; border: 3px solid #eee; border-top: 3px solid #3498db; border-radius: 50%; margin: 0 auto 10px; animation: spin 1s linear infinite; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

/* Mobile Responsive */
@media (max-width: 600px) {
    .header-wrapper { flex-direction: column; align-items: flex-start; gap: 10px; }
    .timeline-item { gap: 15px; }
    .timeline-marker { width: 30px; height: 30px; font-size: 0.9rem; }
    .timeline-line { left: 25px; /* Sesuaikan mobile */ }
    .btn-kerjakan { width: 100%; }
}
</style>