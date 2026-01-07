<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router'; // 1. Import Router
import api from '../../api';

const router = useRouter(); // 2. Init Router

// STATE VARIABLES
const listSiswa = ref([]);
const listKelas = ref([]); 
const isLoading = ref(false);
const showModal = ref(false);
const isEdit = ref(false);

// FORM STATE
const form = reactive({
    id: null,
    name: '',
    nis: '',
    email: '',
    password: '',
    kelas_id: ''
});

// --- NAVIGASI (Tombol Back) ---
const handleBack = () => {
    router.push('/admin/dashboard'); 
};

// 1. FETCH DATA
const fetchData = async () => {
    isLoading.value = true;
    try {
        const [resSiswa, resKelas] = await Promise.all([
            api.get('/admin/siswa'),
            api.get('/admin/kelas')
        ]);

        // Validasi Siswa
        if (Array.isArray(resSiswa.data)) {
            listSiswa.value = resSiswa.data;
        } else if (resSiswa.data && Array.isArray(resSiswa.data.data)) {
            listSiswa.value = resSiswa.data.data;
        } else {
            listSiswa.value = [];
        }

        // Validasi Kelas
        if (Array.isArray(resKelas.data)) {
            listKelas.value = resKelas.data;
        } else if (resKelas.data && Array.isArray(resKelas.data.data)) {
            listKelas.value = resKelas.data.data;
        } else {
            listKelas.value = [];
        }

    } catch (error) {
        console.error("🔴 Gagal Fetch Data:", error);
    } finally {
        isLoading.value = false;
    }
};

// 2. MODAL CONTROLS
const openAdd = () => {
    isEdit.value = false;
    Object.assign(form, { id: null, name: '', nis: '', email: '', password: '', kelas_id: '' });
    showModal.value = true;
};

const openEdit = (item) => {
    isEdit.value = true;
    form.id = item.id;
    form.name = item.name || item.nama || item.nama_siswa || '';
    form.nis = item.nis || item.nomor_induk || '';
    form.email = item.email || '';
    
    // Mapping Kelas ID
    if (item.kelas_id) {
        form.kelas_id = item.kelas_id;
    } else if (item.kelas && item.kelas.id) {
        form.kelas_id = item.kelas.id;
    } else {
        form.kelas_id = '';
    }

    form.password = ''; 
    showModal.value = true;
};

// 3. SIMPAN DATA
const simpanData = async () => {
    if (!form.name || !form.kelas_id) {
        alert("Nama dan Kelas wajib diisi!");
        return;
    }

    try {
        if (isEdit.value) {
            await api.put(`/admin/siswa/${form.id}`, form);
            alert("Data siswa diperbarui!");
        } else {
            await api.post('/admin/siswa', form);
            alert("Siswa baru ditambahkan!");
        }
        showModal.value = false;
        fetchData(); 
    } catch (error) {
        const msg = error.response?.data?.message || "Gagal menyimpan data.";
        alert(msg);
    }
};

// 4. HAPUS DATA
const hapusData = async (id) => {
    if (!confirm("Yakin ingin menghapus siswa ini?")) return;
    try {
        await api.delete(`/admin/siswa/${id}`);
        fetchData();
        alert("Data siswa dihapus.");
    } catch (error) {
        alert("Gagal menghapus data.");
    }
};

onMounted(fetchData);
</script>

<template>
<div class="page-container">
    <div class="content-wrapper">
        <div class="header-wrapper">
            <button @click="handleBack" class="btn-back">
                <span>⬅</span> Kembali
            </button>

            <div class="header-content">
                <div class="title-section">
                    <h2>👨‍🎓 Data Siswa</h2>
                    <p class="subtitle">Kelola daftar siswa dan kelas.</p>
                </div>
                <button @click="openAdd" class="btn btn-primary btn-add">
                    + Tambah Siswa
                </button>
            </div>
        </div>

        <div v-if="isLoading" class="loading-state">
            <div class="spinner"></div>
            <p>Memuat data siswa & kelas...</p>
        </div>

        <div v-else class="table-container">
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th style="width: 50px">No</th>
                            <th>NIS</th>
                            <th>Nama Lengkap</th>
                            <th>Kelas</th>
                            <th>Email</th>
                            <th style="width: 120px">Aksi</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-if="listSiswa.length === 0">
                            <td colspan="6" class="text-center">Belum ada data siswa.</td>
                        </tr>
                        <tr v-for="(siswa, index) in listSiswa" :key="siswa.id || index">
                            <td>{{ index + 1 }}</td>
                            <td><span class="badge-nis">{{ siswa.nis || siswa.nomor_induk || '-' }}</span></td>
                            <td class="fw-bold">{{ siswa.name || siswa.nama }}</td>
                            <td>
                                <span v-if="siswa.kelas" class="badge-kelas">
                                    {{ siswa.kelas.nama_kelas || siswa.kelas.name }}
                                </span>
                                <span v-else class="text-muted">-</span>
                            </td>
                            <td>{{ siswa.email }}</td>
                            <td>
                                <div class="action-buttons">
                                    <button @click="openEdit(siswa)" class="btn-icon edit" title="Edit">✏️</button>
                                    <button @click="hapusData(siswa.id)" class="btn-icon delete" title="Hapus">🗑️</button>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal-content">
            <div class="modal-header">
                <h3>{{ isEdit ? 'Edit Siswa' : 'Tambah Siswa' }}</h3>
                <button @click="showModal = false" class="close-btn">&times;</button>
            </div>
            
            <div class="modal-body">
                <div class="form-group">
                    <label>NIS (Nomor Induk Siswa)</label>
                    <input v-model="form.nis" type="text" placeholder="Contoh: 12345">
                </div>

                <div class="form-group">
                    <label>Nama Lengkap</label>
                    <input v-model="form.name" type="text" placeholder="Nama Siswa">
                </div>

                <div class="form-group">
                    <label>Kelas</label>
                    <select v-model="form.kelas_id">
                        <option value="" disabled>-- Pilih Kelas --</option>
                        <option v-for="k in listKelas" :key="k.id" :value="k.id">
                            {{ k.nama_kelas || k.name || k.nama }}
                        </option>
                    </select>
                    <small v-if="listKelas.length === 0" style="color:red">Data Kelas kosong</small>
                </div>

                <div class="form-group">
                    <label>Email Login</label>
                    <input v-model="form.email" type="email" placeholder="email@sekolah.sch.id">
                </div>

                <div class="form-group">
                    <label>Password</label>
                    <input v-model="form.password" type="password" placeholder="***">
                    <small v-if="isEdit" class="text-muted">*Biarkan kosong jika tidak ingin ubah</small>
                </div>
            </div>

            <div class="modal-footer">
                <button @click="showModal = false" class="btn btn-secondary">Batal</button>
                <button @click="simpanData" class="btn btn-primary">Simpan</button>
            </div>
        </div>
    </div>
</div>
</template>

<style scoped>
/* --- BASE & SCROLL FIX --- */
.page-container {
    /* Ini yang bikin HALAMAN BISA SCROLL MANDIRI */
    height: 100vh;
    overflow-y: auto;
    background-color: #f4f6f9;
    box-sizing: border-box;
}

.content-wrapper {
    max-width: 1100px;
    margin: 0 auto;
    padding: 2rem 1.5rem 6rem 1.5rem; /* Padding bawah extra biar ga mentok */
    font-family: 'Segoe UI', sans-serif;
    color: #333;
}

/* Header Styling */
.header-wrapper { display: flex; flex-direction: column; gap: 1rem; margin-bottom: 2rem; }
.btn-back { background: none; border: none; color: #7f8c8d; cursor: pointer; font-weight: 600; display: flex; align-items: center; gap: 5px; width: fit-content; padding: 0; font-size: 0.95rem; }
.btn-back:hover { color: #2c3e50; }
.header-content { display: flex; justify-content: space-between; align-items: center; }
.title-section h2 { margin: 0; color: #2c3e50; font-size: 1.8rem; }
.subtitle { margin: 5px 0 0; color: #7f8c8d; font-size: 0.95rem; }

/* Buttons */
.btn { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-weight: 600; font-size: 0.9rem; transition: all 0.2s; }
.btn-primary { background-color: #3498db; color: white; }
.btn-primary:hover { background-color: #2980b9; }
.btn-secondary { background-color: #ecf0f1; color: #7f8c8d; }
.btn-icon { width: 34px; height: 34px; border: none; border-radius: 6px; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 1.1rem; }
.edit { background: #fff8e1; color: #f39c12; }
.delete { background: #ffebee; color: #e74c3c; margin-left: 5px; }

/* Table Styling */
.table-container { background: white; border-radius: 12px; box-shadow: 0 5px 20px rgba(0,0,0,0.05); border: 1px solid #eee; overflow: hidden; }
.table-responsive { width: 100%; overflow-x: auto; -webkit-overflow-scrolling: touch; }
.custom-table { width: 100%; border-collapse: collapse; white-space: nowrap; }
.custom-table th, .custom-table td { padding: 16px 20px; text-align: left; border-bottom: 1px solid #f0f0f0; }
.custom-table th { background-color: #f8f9fa; color: #34495e; font-weight: 700; font-size: 0.9rem; }
.text-center { text-align: center; }
.fw-bold { font-weight: 600; }

/* Badges khusus Siswa */
.badge-nis { background: #eee; padding: 4px 10px; border-radius: 6px; font-family: monospace; font-weight: bold; font-size: 0.85rem; color: #555; }
.badge-kelas { background: #e3f2fd; color: #1976d2; padding: 4px 10px; border-radius: 20px; font-size: 0.8rem; font-weight: 600; }

/* --- MODAL FIX (PENTING BUAT HP) --- */
.modal-overlay {
    position: fixed; top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0,0,0,0.6);
    display: flex; justify-content: center; align-items: center;
    z-index: 1000;
    padding: 1rem;
}

.modal-content {
    background: white;
    width: 100%;
    max-width: 450px;
    border-radius: 12px;
    box-shadow: 0 15px 35px rgba(0,0,0,0.2);
    animation: slideUp 0.3s ease;
    
    /* FIX SCROLL MODAL */
    display: flex;
    flex-direction: column;
    max-height: 90vh; /* Modal tidak akan lebih tinggi dari 90% layar */
}

.modal-header { padding: 1.25rem; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; }
.modal-footer { padding: 1.25rem; background: #f8f9fa; border-top: 1px solid #eee; display: flex; justify-content: flex-end; gap: 10px; border-radius: 0 0 12px 12px; flex-shrink: 0; }

.modal-body { 
    padding: 1.5rem; 
    /* FIX SCROLL FORM */
    overflow-y: auto; /* Hanya body yang scroll, header/footer diam */
}

.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 600; font-size: 0.9rem; color: #34495e; }
.form-group input, .form-group select { width: 100%; padding: 10px 12px; border: 1px solid #dfe6e9; border-radius: 8px; font-size: 0.95rem; box-sizing: border-box; }
.form-group input:focus, .form-group select:focus { outline: none; border-color: #3498db; }
.close-btn { background: transparent; border: none; font-size: 1.5rem; cursor: pointer; color: #95a5a6; }
.text-muted { font-size: 0.8rem; color: #888; }

/* Loading & Animation */
.loading-state { text-align: center; padding: 4rem; color: #95a5a6; }
.spinner { border: 3px solid #f3f3f3; border-top: 3px solid #3498db; border-radius: 50%; width: 30px; height: 30px; animation: spin 1s linear infinite; margin: 0 auto 1rem; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

/* --- RESPONSIVE MEDIA QUERIES --- */
@media (max-width: 768px) {
    .content-wrapper {
        padding: 1rem;
        padding-bottom: 5rem;
    }

    /* Header di HP */
    .header-content {
        flex-direction: column;
        align-items: flex-start;
        gap: 1rem;
    }
    
    .btn-add { width: 100%; justify-content: center; display: flex; }

    /* Font tabel lebih kecil di HP */
    .custom-table th, .custom-table td { padding: 12px 15px; font-size: 0.9rem; }
}
</style>