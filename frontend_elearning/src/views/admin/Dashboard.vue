<script setup>
import { useRouter } from 'vue-router';
import api from '../../api';

const router = useRouter();

// --- LOGOUT ---
const handleLogout = async () => {
    if (!confirm("Yakin ingin logout?")) return;
    try {
        await api.post('/logout');
    } catch (e) { console.log(e); }
    localStorage.clear();
    router.push('/');
};

// --- MENU NAVIGASI ---
const menus = [
    { title: 'Manajemen Guru', icon: '👨‍🏫', route: '/admin/guru', desc: 'Kelola data pengajar' },
    { title: 'Manajemen Siswa', icon: '👨‍🎓', route: '/admin/siswa', desc: 'Kelola data siswa & kelas' },
    { title: 'Manajemen Mapel', icon: '📚', route: '/admin/mapel', desc: 'Kelola mata pelajaran' },
    // Tambahan untuk Kelas karena ada di route API kamu
    { title: 'Manajemen Kelas', icon: '🏫', route: '/admin/kelas', desc: 'Tambah/Edit kelas' }, 
];
</script>

<template>
<div class="container">
    <div class="welcome-banner">
        <div class="banner-content">
            <h2>🛡️ Panel Admin</h2>
            <p>Selamat datang, Administrator. Silakan pilih menu untuk mengelola data sekolah.</p>
        </div>
        <button @click="handleLogout" class="btn-logout">🚪 Logout</button>
    </div>

    <div class="grid-menu">
        <div v-for="(menu, i) in menus" :key="i" class="card-menu" @click="router.push(menu.route)">
            <div class="icon">{{ menu.icon }}</div>
            <div>
                <h3>{{ menu.title }}</h3>
                <p>{{ menu.desc }}</p>
            </div>
            <span class="arrow">➜</span>
        </div>
    </div>
</div>
</template>

<style scoped>
/* Gunakan Style yang sama dengan Dashboard Guru sebelumnya */
.container { max-width: 1000px; margin: 0 auto; padding: 2rem; font-family: 'Segoe UI', sans-serif; }
.welcome-banner { background: linear-gradient(135deg, #2c3e50, #34495e); color: white; padding: 2rem; border-radius: 12px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; box-shadow: 0 10px 20px rgba(0,0,0,0.1); }
.btn-logout { background: #e74c3c; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: bold; }
.grid-menu { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem; }
.card-menu { background: white; padding: 1.5rem; border-radius: 12px; border: 1px solid #eee; cursor: pointer; display: flex; align-items: center; gap: 15px; transition: 0.2s; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.card-menu:hover { transform: translateY(-5px); border-color: #34495e; box-shadow: 0 5px 15px rgba(0,0,0,0.1); }
.icon { font-size: 2rem; background: #ecf0f1; width: 50px; height: 50px; display: flex; justify-content: center; align-items: center; border-radius: 50%; }
.arrow { margin-left: auto; color: #bdc3c7; font-weight: bold; }
</style>