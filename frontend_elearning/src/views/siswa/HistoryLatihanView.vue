<script setup>
import { ref, onMounted } from 'vue';
import api from '../../api'; // Sesuaikan path axios instance Anda

const histories = ref([]);
const isLoading = ref(true);

// Fungsi Format Tanggal (Contoh: 16 Desember 2025, 10:30)
const formatDate = (dateString) => {
    if (!dateString) return '-';
    const options = { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric', 
        hour: '2-digit', 
        minute: '2-digit' 
    };
    return new Date(dateString).toLocaleDateString('id-ID', options);
};

// Fungsi Warna Badge Nilai
const getBadgeColor = (skor) => {
    if (skor >= 85) return 'bg-green-100 text-green-800'; // Sangat Baik
    if (skor >= 70) return 'bg-blue-100 text-blue-800';   // Baik
    if (skor >= 50) return 'bg-yellow-100 text-yellow-800'; // Cukup
    return 'bg-red-100 text-red-800'; // Kurang
};

const fetchHistory = async () => {
    isLoading.value = true;
    try {
        // Sesuai route yang Anda perbaiki: GET /siswa/history
        const response = await api.get('/siswa/history');
        
        if (response.data.success) {
            histories.value = response.data.data;
        }
    } catch (error) {
        console.error("Gagal mengambil history:", error);
    } finally {
        isLoading.value = false;
    }
};

onMounted(() => {
    fetchHistory();
});
</script>

<template>
  <div class="history-container">
    <div class="header">
        <div>
            <h2>📊 Riwayat Hasil Belajar</h2>
            <p class="subtitle">Daftar nilai latihan yang sudah kamu kerjakan.</p>
        </div>
        <router-link to="/siswa/dashboard" class="btn-back">
            &larr; Kembali ke Dashboard
        </router-link>
    </div>

    <div v-if="isLoading" class="loading-state">
        <div class="spinner"></div>
        <p>Memuat data...</p>
    </div>

    <div v-else-if="histories.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <h3>Belum ada riwayat</h3>
        <p>Kamu belum mengerjakan latihan soal apapun.</p>
        <router-link to="/siswa/dashboard" class="btn-primary">Mulai Belajar</router-link>
    </div>

    <div v-else class="table-wrapper">
        <table class="custom-table">
            <thead>
                <tr>
                    <th>No</th>
                    <th>Mata Pelajaran</th>
                    <th>Materi / Bab</th>
                    <th>Tanggal Pengerjaan</th>
                    <th class="text-center">Nilai</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(item, index) in histories" :key="item.id">
                    <td>{{ index + 1 }}</td>
                    
                    <td>
                        <span class="mapel-badge">
                            {{ item.latihan?.materi?.mapel?.nama_mapel || 'Mapel Dihapus' }}
                        </span>
                    </td>
                    
                    <td>
                        <span class="materi-text">
                            {{ item.latihan?.materi?.judul_materi || 'Materi Dihapus' }}
                        </span>
                    </td>
                    
                    <td class="date-col">
                        {{ formatDate(item.created_at) }}
                    </td>
                    
                    <td class="text-center">
                        <span class="score-badge" :class="getBadgeColor(item.skor)">
                            {{ item.skor }}
                        </span>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
  </div>
</template>

<style scoped>
/* Layout Utama */
.history-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 2rem;
    font-family: 'Segoe UI', sans-serif;
    min-height: 80vh;
}

/* Header */
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid #e0e0e0;
}
.header h2 { margin: 0; color: #2c3e50; font-size: 1.5rem; }
.subtitle { color: #7f8c8d; margin: 5px 0 0; font-size: 0.95rem; }

/* Buttons */
.btn-back {
    text-decoration: none;
    color: #3498db;
    font-weight: 600;
    padding: 8px 16px;
    border-radius: 6px;
    background: #eaf6ff;
    transition: 0.2s;
}
.btn-back:hover { background: #d6eaf8; }

.btn-primary {
    display: inline-block;
    background: #3498db;
    color: white;
    padding: 10px 20px;
    border-radius: 6px;
    text-decoration: none;
    font-weight: bold;
    margin-top: 1rem;
}

/* Table Styling */
.table-wrapper {
    background: white;
    border-radius: 10px;
    box-shadow: 0 4px 6px rgba(0,0,0,0.05);
    overflow: hidden;
    overflow-x: auto; /* Responsive */
}
.custom-table {
    width: 100%;
    border-collapse: collapse;
    min-width: 600px;
}
.custom-table th, .custom-table td {
    padding: 15px 20px;
    text-align: left;
    border-bottom: 1px solid #f0f0f0;
}
.custom-table th {
    background-color: #f8f9fa;
    color: #555;
    font-weight: 600;
    text-transform: uppercase;
    font-size: 0.85rem;
    letter-spacing: 0.5px;
}
.custom-table tr:hover { background-color: #fcfcfc; }
.text-center { text-align: center; }

/* Badges & Text */
.mapel-badge {
    background: #f1f2f6;
    color: #2f3542;
    padding: 4px 10px;
    border-radius: 4px;
    font-size: 0.9rem;
    font-weight: 500;
}
.materi-text { font-weight: 600; color: #2d3436; }
.date-col { color: #7f8c8d; font-size: 0.9rem; }

.score-badge {
    display: inline-block;
    padding: 6px 12px;
    border-radius: 20px;
    font-weight: bold;
    font-size: 0.95rem;
}
/* Tailwind-like utility classes for colors */
.bg-green-100 { background-color: #d1fae5; }
.text-green-800 { color: #065f46; }
.bg-blue-100 { background-color: #dbeafe; }
.text-blue-800 { color: #1e40af; }
.bg-yellow-100 { background-color: #fef3c7; }
.text-yellow-800 { color: #92400e; }
.bg-red-100 { background-color: #fee2e2; }
.text-red-800 { color: #991b1b; }

/* States */
.loading-state, .empty-state { text-align: center; padding: 4rem; color: #a4b0be; }
.spinner { 
    width: 40px; height: 40px; 
    border: 4px solid #f3f3f3; 
    border-top: 4px solid #3498db; 
    border-radius: 50%; 
    animation: spin 1s linear infinite; 
    margin: 0 auto 1rem; 
}
.empty-icon { font-size: 3rem; margin-bottom: 1rem; }

@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
</style>