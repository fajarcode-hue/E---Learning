<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

// --- LOGIKA JAVASCRIPT (TIDAK DIUBAH) ---
const router = useRouter();

const email = ref('');
const password = ref('');
const role = ref('guru'); 
const errorMessage = ref('');
const isLoading = ref(false);

const handleLogin = async () => {
    isLoading.value = true;
    errorMessage.value = '';

    try {
        let endpoint = '';
        if (role.value === 'admin') endpoint = '/login/admin';
        else if (role.value === 'guru') endpoint = '/login/guru';
        else endpoint = '/login/siswa';

        const response = await axios.post(`http://127.0.0.1:8000/api${endpoint}`, {
            email: email.value,
            password: password.value
        });

        if (response.data.success) {
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('user', JSON.stringify(response.data.user));
            
            if (role.value === 'admin') router.push('/admin/dashboard');
            else if (role.value === 'guru') router.push('/guru/dashboard');
            else if (role.value === 'siswa') router.push('/siswa/dashboard');
        }

    } catch (error) {
        console.error(error);
        errorMessage.value = error.response?.data?.message || 'Login Gagal. Cek email/password.';
    } finally {
        isLoading.value = false;
    }
};
</script>

<template>
  <div class="login-wrapper">
    
    <div class="brand-section">
        <div class="brand-content">
            <h1>E-Learning System</h1>
            <p>Platform pembelajaran modern untuk masa depan yang lebih baik.</p>
        </div>
        <div class="shape-blob"></div>
    </div>

    <div class="form-section">
        <div class="login-card animated-fade-in">
            <div class="card-header">
                <h2>Selamat Datang Kembali! 👋</h2>
                <p class="subtitle">Silakan masuk untuk melanjutkan.</p>
            </div>
            
            <transition name="fade">
                <div v-if="errorMessage" class="error-alert">
                    <span class="error-icon">⚠️</span> {{ errorMessage }}
                </div>
            </transition>

            <form @submit.prevent="handleLogin">
                <div class="form-group">
                    <label for="role">Masuk Sebagai</label>
                    <div class="select-wrapper">
                        <select v-model="role" id="role" class="modern-input">
                            <option value="siswa">👨‍🎓 Siswa</option>
                            <option value="guru">👩‍🏫 Guru</option>
                            <option value="admin">🛠️ Administrator</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" v-model="email" required placeholder="contoh@sekolah.id" class="modern-input" />
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" v-model="password" required placeholder="Masukkan password..." class="modern-input" />
                </div>

                <button type="submit" class="btn-login" :disabled="isLoading">
                    <span v-if="isLoading" class="loader"></span>
                    <span v-else>Masuk Sekarang ➜</span>
                </button>
            </form>
            
            <p class="footer-text">Belum punya akun? Hubungi admin sekolah.</p>
        </div>
    </div>
  </div>
</template>

<style>
/* Ini PENTING untuk menghilangkan margin putih bawaan browser */
body {
    margin: 0 !important;
    padding: 0 !important;
    overflow: hidden; /* Mencegah scroll di level body */
}
</style>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap');

/* --- LAYOUT UTAMA --- */
.login-wrapper {
    display: flex;
    width: 100vw;  /* Lebar full layar */
    height: 100vh; /* Tinggi full layar (Viewport Height) */
    font-family: 'Poppins', sans-serif;
    background-color: #f8f9fa;
    overflow: hidden; /* Kunci scrollbar */
    margin: 0;
    padding: 0;
}

/* --- BAGIAN KIRI (BRANDING) --- */
.brand-section {
    flex: 1; /* Mengambil 50% lebar atau sisa ruang */
    background: linear-gradient(135deg, #6c5ce7, #a29bfe);
    color: white;
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 4rem;
    position: relative;
    /* Pastikan tidak ada margin/padding yang bocor */
    height: 100%; 
}

.brand-content {
    position: relative;
    z-index: 2;
    max-width: 500px;
}

.brand-content h1 { font-size: 3rem; font-weight: 700; margin-bottom: 1rem; line-height: 1.2; }
.brand-content p { font-size: 1.2rem; opacity: 0.9; }

.shape-blob {
    position: absolute;
    bottom: -10%; right: -10%;
    width: 300px; height: 300px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    filter: blur(60px);
    z-index: 1;
}

@media (max-width: 900px) {
    .brand-section { display: none; }
}

/* --- BAGIAN KANAN (FORM) --- */
.form-section {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 2rem;
    height: 100%; /* Full height agar centered vertikal */
    background: white; /* Jaga-jaga jika background wrapper bocor */
}

.login-card {
    width: 100%;
    max-width: 450px;
    /* Hilangkan background card jika ingin menyatu dengan putih, 
       atau beri sedikit shadow/border */
    background: white;
    padding: 2rem; 
    /* Hilangkan shadow besar jika ingin terlihat sangat flat, 
       tapi shadow membantu fokus */
    box-shadow: none; 
}

/* Responsif: Jika layar sangat pendek (misal landscape HP), izinkan scroll di dalam form saja */
@media (max-height: 700px) {
    .form-section {
        overflow-y: auto; /* Scroll hanya di bagian form jika mentok */
        align-items: flex-start; /* Mulai dari atas */
        padding-top: 2rem;
    }
}

/* Header Card */
.card-header { margin-bottom: 2rem; text-align: center; }
.card-header h2 { color: #2d3436; margin-bottom: 0.5rem; font-size: 1.8rem; }
.subtitle { color: #636e72; margin: 0; }

/* --- FORM STYLING --- */
.form-group { margin-bottom: 1.5rem; }
label { display: block; margin-bottom: 0.5rem; color: #2d3436; font-weight: 600; font-size: 0.9rem; }

.modern-input {
    width: 100%;
    padding: 1rem 1.2rem;
    border: 2px solid #f1f3f5;
    background-color: #f8f9fa;
    border-radius: 12px;
    font-size: 1rem;
    color: #2d3436;
    transition: all 0.3s ease;
    box-sizing: border-box; 
}

.modern-input:focus {
    border-color: #6c5ce7;
    background-color: white;
    outline: none;
    box-shadow: 0 0 0 4px rgba(108, 92, 231, 0.1);
}

.btn-login {
    width: 100%;
    padding: 1rem;
    background: linear-gradient(to right, #6c5ce7, #a29bfe);
    color: white;
    border: none;
    border-radius: 12px;
    font-size: 1.1rem;
    font-weight: 700;
    cursor: pointer;
    transition: transform 0.2s, box-shadow 0.2s;
    display: flex; justify-content: center; align-items: center;
}
.btn-login:hover:not(:disabled) { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(108, 92, 231, 0.3); }
.btn-login:disabled { opacity: 0.7; cursor: not-allowed; }

.error-alert {
    background: #ffebee; color: #c62828; padding: 1rem;
    border-radius: 10px; margin-bottom: 1.5rem; display: flex; align-items: center; gap: 10px;
    border-left: 4px solid #d32f2f; font-size: 0.9rem;
}

.footer-text { text-align: center; margin-top: 2rem; color: #b2bec3; font-size: 0.9rem; }

/* Animasi */
.animated-fade-in { animation: fadeInUp 0.5s ease-out; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

.loader { border: 3px solid rgba(255,255,255,0.3); border-radius: 50%; border-top: 3px solid white; width: 20px; height: 20px; animation: spin 1s linear infinite; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
</style>