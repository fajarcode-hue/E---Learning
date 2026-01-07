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
        <button @click="handleLogout" class="btn-logout">
            <span>🚪</span> Logout
        </button>
    </div>

    <div class="grid-menu">
        <div v-for="(menu, i) in menus" :key="i" class="card-menu" @click="router.push(menu.route)">
            <div class="icon-wrapper">
                <div class="icon">{{ menu.icon }}</div>
            </div>
            <div class="text-content">
                <h3>{{ menu.title }}</h3>
                <p>{{ menu.desc }}</p>
            </div>
            <span class="arrow">➜</span>
        </div>
    </div>
</div>
</template>

<style scoped>
/* --- BASE STYLES (Mobile First Approach) --- */
* {
    box-sizing: border-box; /* Penting agar padding tidak merusak lebar elemen */
}

.container {
    width: 100%;
    max-width: 1100px;
    margin: 0 auto;
    padding: 1rem; /* Padding lebih kecil untuk mobile */
    font-family: 'Segoe UI', sans-serif;
}

/* Banner Styling */
.welcome-banner {
    background: linear-gradient(135deg, #2c3e50, #34495e);
    color: white;
    padding: 1.5rem;
    border-radius: 12px;
    display: flex;
    flex-direction: column; /* Default column untuk mobile */
    gap: 1.5rem;
    margin-bottom: 2rem;
    box-shadow: 0 10px 20px rgba(0,0,0,0.1);
    text-align: center; /* Center text di mobile */
}

.banner-content h2 {
    margin: 0 0 0.5rem 0;
    font-size: 1.5rem;
}

.banner-content p {
    margin: 0;
    opacity: 0.9;
    font-size: 0.95rem;
    line-height: 1.5;
}

.btn-logout {
    background: #e74c3c;
    color: white;
    border: none;
    padding: 12px 24px;
    border-radius: 8px;
    cursor: pointer;
    font-weight: bold;
    font-size: 0.9rem;
    transition: background 0.2s;
    width: 100%; /* Tombol full width di mobile */
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
}

.btn-logout:hover {
    background: #c0392b;
}

/* Grid Menu Styling */
.grid-menu {
    display: grid;
    /* Responsive Grid: Otomatis mengisi kolom, minimal lebar 280px */
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 1rem;
}

.card-menu {
    background: white;
    padding: 1.25rem;
    border-radius: 12px;
    border: 1px solid #eee;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 15px;
    transition: all 0.2s ease;
    box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}

.card-menu:active {
    transform: scale(0.98); /* Efek klik di HP */
}

.icon {
    font-size: 1.8rem;
    background: #ecf0f1;
    width: 50px;
    height: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 50%;
    flex-shrink: 0; /* Mencegah icon mengecil jika teks panjang */
}

.text-content {
    flex: 1; /* Mengisi sisa ruang */
}

.text-content h3 {
    margin: 0 0 5px 0;
    font-size: 1.1rem;
    color: #2c3e50;
}

.text-content p {
    margin: 0;
    font-size: 0.85rem;
    color: #7f8c8d;
}

.arrow {
    color: #bdc3c7;
    font-weight: bold;
    transition: transform 0.2s;
}

/* --- MEDIA QUERIES (Untuk Tablet & PC) --- */
@media (min-width: 768px) {
    .container {
        padding: 2rem;
    }

    .welcome-banner {
        flex-direction: row; /* Kembali ke baris untuk layar lebar */
        justify-content: space-between;
        align-items: center;
        text-align: left;
        padding: 2.5rem;
    }

    .btn-logout {
        width: auto; /* Tombol menyesuaikan konten di PC */
        margin-left: 20px;
    }

    .grid-menu {
        gap: 1.5rem;
    }

    .card-menu {
        padding: 1.5rem;
    }

    .card-menu:hover {
        transform: translateY(-5px);
        border-color: #34495e;
        box-shadow: 0 5px 15px rgba(0,0,0,0.1);
    }

    .card-menu:hover .arrow {
        transform: translateX(5px);
        color: #2c3e50;
    }
}
</style>