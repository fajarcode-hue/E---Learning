<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../../api';

const router = useRouter();
const listMapel = ref([]);
const isLoading = ref(true);
const userName = ref('');
const userRole = ref('');

// --- 1. LOAD DATA ---
const fetchData = async () => {
    isLoading.value = true;
    
    // Ambil data user dari LocalStorage (atau bisa dari API profile jika ada)
    userName.value = localStorage.getItem('user_name') || 'Bapak/Ibu Guru';
    userRole.value = 'Pengajar'; 

    try {
        // Panggil API Mapel Guru
        const response = await api.get('/guru/mapel');
        
        if (response.data.success) {
            listMapel.value = response.data.data;
        }
    } catch (error) {
        console.error("Gagal ambil data:", error);
        if (error.response && error.response.status === 401) {
            router.push('/');
        }
    } finally {
        isLoading.value = false;
    }
};

// --- 2. FUNGSI LOGOUT ---
const handleLogout = async () => {
    if (!confirm("Apakah Anda yakin ingin keluar?")) return;

    try {
        await api.post('/logout'); 
    } catch (error) {
        console.warn("Logout error:", error);
    } finally {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('user_name');
        router.push('/'); 
    }
};

// --- 3. NAVIGASI KE MATERI ---
const goToMateri = (mapelId, namaMapel) => {
    router.push({ 
        name: 'guru-materi', // Pastikan route ini ada di router.js
        params: { id: mapelId },
        query: { nama: namaMapel } 
    });
};

onMounted(() => {
    fetchData();
});
</script>

<template>
  <div class="container">
    
    <div class="welcome-banner">
        <div class="banner-content">
            <h2>👋 Halo, {{ userName }}!</h2>
            
            <div class="user-badge">
                <span class="icon-badge">👨‍🏫</span>
                <span>{{ userRole }}</span>
            </div>
            
            <p class="mt-2">Selamat datang di Panel Guru. Kelola materi dan siswa Anda di sini.</p>
        </div>
        
        <div class="banner-actions">
            <button @click="handleLogout" class="btn-action btn-logout">
                🚪 Logout
            </button>
        </div>
    </div>

    <div v-if="isLoading" class="loading">
        <div class="spinner"></div>
        <p>Sedang memuat data pelajaran...</p>
    </div>

    <div v-else-if="listMapel.length === 0" class="empty-state">
        <p>Anda belum memiliki mata pelajaran yang diampu.</p>
    </div>

    <div v-else class="grid-mapel">
        <div 
            v-for="mapel in listMapel" 
            :key="mapel.id" 
            class="card-mapel"
            @click="goToMateri(mapel.id, mapel.nama_mapel)"
        >
            <div class="card-icon">📚</div>
            <div class="card-info">
                <h3>{{ mapel.nama_mapel }}</h3>
                <p class="text-muted">
                    {{ mapel.kelas ? 'Kelas: ' + mapel.kelas.nama_kelas : 'Semua Kelas' }}
                </p>
            </div>
            <div class="card-arrow">➜</div>
        </div>
    </div>
  </div>
</template>

<style scoped>
.container { max-width: 1000px; margin: 0 auto; padding: 2rem; font-family: 'Segoe UI', sans-serif; }

/* --- BANNER STYLING (BLUE THEME) --- */
.welcome-banner { 
    /* Gradient Biru Professional */
    background: linear-gradient(135deg, #3498db, #2980b9); 
    color: white; 
    padding: 2.5rem; 
    border-radius: 16px; 
    margin-bottom: 2.5rem; 
    box-shadow: 0 10px 25px rgba(52, 152, 219, 0.3); /* Shadow Biru */
    
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap; 
    gap: 1.5rem;
}

.banner-content h2 { margin: 0 0 0.8rem 0; font-size: 1.8rem; font-weight: 700; }
.banner-content p.mt-2 { margin: 15px 0 0; opacity: 0.9; font-size: 1rem; line-height: 1.5; }

/* Badge Role */
.user-badge {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    background: rgba(255, 255, 255, 0.2);
    padding: 6px 14px;
    border-radius: 20px;
    font-weight: 600;
    font-size: 0.9rem;
    backdrop-filter: blur(5px);
    border: 1px solid rgba(255,255,255,0.3);
}

/* Group Button Actions */
.banner-actions { display: flex; gap: 10px; }

/* Base Button Style */
.btn-action {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    border-radius: 8px;
    font-weight: bold;
    cursor: pointer;
    border: none;
    font-size: 0.95rem;
    transition: all 0.2s ease;
    box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

/* Logout Button (Red/White) */
.btn-logout {
    background-color: white; 
    color: #e74c3c; /* Merah Text */
}
.btn-logout:hover {
    background-color: #fceceb;
    transform: translateY(-3px);
    box-shadow: 0 6px 12px rgba(0,0,0,0.15);
}

/* --- GRID MAPEL --- */
.grid-mapel { 
    display: grid; 
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); 
    gap: 1.5rem; 
}

.card-mapel { 
    background: white; 
    border: 1px solid #f0f0f0; 
    border-radius: 12px; 
    padding: 1.5rem; 
    cursor: pointer; 
    transition: all 0.2s; 
    display: flex; 
    align-items: center; 
    gap: 1rem; 
    box-shadow: 0 2px 5px rgba(0,0,0,0.02);
}

.card-mapel:hover { 
    transform: translateY(-5px); 
    box-shadow: 0 10px 20px rgba(52, 152, 219, 0.15); /* Shadow Biru */
    border-color: #3498db; 
}

.card-icon { 
    font-size: 2rem; 
    background: #e8f4fd; /* Biru Sangat Muda */
    color: #3498db;
    width: 60px; 
    height: 60px; 
    display: flex; 
    justify-content: center; 
    align-items: center; 
    border-radius: 50%; 
}

.card-info { flex: 1; }
.card-info h3 { margin: 0 0 0.3rem 0; color: #2c3e50; font-size: 1.1rem; font-weight: 700; }
.text-muted { margin: 0; color: #7f8c8d; font-size: 0.9rem; }
.card-arrow { color: #bdc3c7; font-weight: bold; font-size: 1.2rem; transition: 0.2s; }

.card-mapel:hover .card-arrow {
    color: #3498db;
    transform: translateX(5px);
}

/* Loading & Empty */
.loading, .empty-state { text-align: center; padding: 4rem; color: #b2bec3; }
.spinner { width: 35px; height: 35px; border: 4px solid #eee; border-top: 4px solid #3498db; border-radius: 50%; margin: 0 auto 15px; animation: spin 1s linear infinite; }

@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

/* Responsif Mobile */
@media (max-width: 650px) {
    .welcome-banner {
        flex-direction: column;
        align-items: flex-start;
        padding: 2rem;
    }
    .banner-actions {
        width: 100%;
        margin-top: 10px;
    }
    .btn-action {
        width: 100%;
        justify-content: center;
    }
}
</style>