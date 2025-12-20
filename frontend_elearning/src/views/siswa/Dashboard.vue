<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../../api';

const router = useRouter();
const mapels = ref([]);
const isLoading = ref(true);
const user = ref({});

// Ambil data User & Mapel
const fetchData = async () => {
    isLoading.value = true;
    try {
        const response = await api.get('/siswa/mapel');
        
        if (response.data.success) {
            mapels.value = response.data.data; 
            
            // Ambil info siswa + kelas
            if (response.data.info_siswa) {
                user.value = response.data.info_siswa;
            }
        }
    } catch (error) {
        console.error("Gagal ambil data:", error);
        // Jika token expired/error auth, lempar ke login
        if (error.response && error.response.status === 401) {
            router.push('/');
        }
    } finally {
        isLoading.value = false;
    }
};

// Fungsi Logout
const handleLogout = async () => {
    if (!confirm("Apakah Anda yakin ingin keluar?")) return;

    try {
        await api.post('/logout'); // Hit endpoint logout API
        localStorage.removeItem('token'); // Hapus token dari storage
        localStorage.removeItem('role');
        router.push('/'); // Kembali ke halaman login
    } catch (error) {
        console.error("Logout error:", error);
        // Tetap paksa logout di frontend meski backend error
        localStorage.removeItem('token');
        router.push('/');
    }
};

const goToMapel = (id) => {
    router.push(`/siswa/mapel/${id}`);
};

onMounted(() => {
    fetchData();
});
</script>

<template>
  <div class="container">
    
    <div class="welcome-banner">
        <div class="banner-content">
            <h2>👋 Halo, {{ user.name || user.nama || 'Siswa' }}!</h2>
            
            <div class="user-class">
                <span class="icon-class">🎓</span>
                <span>
                    {{ user.kelas ? user.kelas.nama_kelas : 'Memuat Kelas...' }}
                </span>
            </div>
            
            <p class="mt-2">Selamat datang kembali. Pilih mata pelajaran untuk mulai belajar.</p>
        </div>
        
        <div class="banner-actions">
            <router-link to="/siswa/history" class="btn-action btn-history">
                🏆 Riwayat Nilai
            </router-link>

            <button @click="handleLogout" class="btn-action btn-logout">
                🚪 Logout
            </button>
        </div>
    </div>

    <div v-if="isLoading" class="loading">
        <div class="spinner"></div>
        <p>Sedang memuat data pelajaran...</p>
    </div>

    <div v-else-if="mapels.length === 0" class="empty-state">
        <p>Belum ada mata pelajaran yang tersedia.</p>
    </div>

    <div v-else class="grid-mapel">
        <div 
            v-for="mapel in mapels" 
            :key="mapel.id" 
            class="card-mapel"
            @click="goToMapel(mapel.id)"
        >
            <div class="card-icon">📚</div>
            <div class="card-info">
                <h3>{{ mapel.nama_mapel }}</h3>
                <p>Pengajar: {{ mapel.guru ? mapel.guru.name : '-' }}</p>
            </div>
            <div class="card-arrow">➜</div>
        </div>
    </div>
  </div>
</template>

<style scoped>
.container { max-width: 1000px; margin: 0 auto; padding: 2rem; font-family: 'Segoe UI', sans-serif; }

/* Banner Styling Updated */
.welcome-banner { 
    background: linear-gradient(135deg, #6c5ce7, #a29bfe); 
    color: white; 
    padding: 2rem; 
    border-radius: 16px; 
    margin-bottom: 2rem; 
    box-shadow: 0 8px 20px rgba(108, 92, 231, 0.3);
    
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap; 
    gap: 1.5rem;
}

.banner-content h2 { margin: 0 0 0.5rem 0; font-size: 1.8rem; font-weight: 700; }
.banner-content p.mt-2 { margin: 10px 0 0; opacity: 0.9; font-size: 0.95rem; }

/* Styling Info Kelas */
.user-class {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    background: rgba(255, 255, 255, 0.2);
    padding: 5px 12px;
    border-radius: 20px;
    font-weight: 600;
    font-size: 0.9rem;
    backdrop-filter: blur(5px);
}

/* Group Button Actions */
.banner-actions {
    display: flex;
    gap: 10px;
}

/* Base Button Style */
.btn-action {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    border-radius: 8px;
    font-weight: bold;
    text-decoration: none;
    cursor: pointer;
    border: none;
    font-size: 0.95rem;
    transition: all 0.2s ease;
    box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

/* Specific Button Colors */
.btn-history {
    background-color: #fdcb6e; 
    color: #2d3436;
}
.btn-history:hover {
    background-color: #ffeaa7;
    transform: translateY(-3px);
}

.btn-logout {
    background-color: #ff7675; /* Merah soft */
    color: white;
}
.btn-logout:hover {
    background-color: #d63031;
    transform: translateY(-3px);
}

/* Grid Mapel */
.grid-mapel { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 1.5rem; }

.card-mapel { background: white; border: 1px solid #eee; border-radius: 12px; padding: 1.5rem; cursor: pointer; transition: transform 0.2s, box-shadow 0.2s; display: flex; align-items: center; gap: 1rem; }
.card-mapel:hover { transform: translateY(-5px); box-shadow: 0 5px 15px rgba(0,0,0,0.08); border-color: #6c5ce7; }

.card-icon { font-size: 2.5rem; background: #f0f3ff; width: 60px; height: 60px; display: flex; justify-content: center; align-items: center; border-radius: 50%; }
.card-info { flex: 1; }
.card-info h3 { margin: 0 0 0.3rem 0; color: #2d3436; font-size: 1.1rem; }
.card-info p { margin: 0; color: #636e72; font-size: 0.9rem; }
.card-arrow { color: #b2bec3; font-weight: bold; }

/* Loading & Empty */
.loading, .empty-state { text-align: center; padding: 3rem; color: #b2bec3; }
.spinner { width: 30px; height: 30px; border: 3px solid #eee; border-top: 3px solid #6c5ce7; border-radius: 50%; margin: 0 auto 10px; animation: spin 1s linear infinite; }

@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

/* Responsif Mobile */
@media (max-width: 650px) {
    .welcome-banner {
        flex-direction: column;
        align-items: flex-start;
    }
    .banner-actions {
        width: 100%;
        flex-direction: column;
    }
    .btn-action {
        width: 100%;
        justify-content: center;
    }
}
</style>