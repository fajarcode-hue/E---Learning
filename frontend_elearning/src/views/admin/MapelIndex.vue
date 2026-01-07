<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router'; // 1. Import Router
import api from '../../api';

const router = useRouter(); // 2. Init Router

// STATE VARIABLES
const listMapel = ref([]);
const listGuru = ref([]);
const listKelas = ref([]);
const isLoading = ref(false);
const showModal = ref(false);
const isEdit = ref(false);

// FORM STATE
const form = reactive({
    id: null,
    nama_mapel: '',
    guru_id: '',
    kelas_id: ''
});

// --- NAVIGASI ---
const handleBack = () => {
    router.push('/admin/dashboard'); 
};

// 1. FETCH DATA (MAPEL, GURU, KELAS)
const fetchData = async () => {
    isLoading.value = true;
    try {
        const [resMapel, resGuru, resKelas] = await Promise.all([
            api.get('/admin/mapel'),
            api.get('/admin/guru'),
            api.get('/admin/kelas')
        ]);

        // Validasi Mapel
        if (Array.isArray(resMapel.data)) {
            listMapel.value = resMapel.data;
        } else if (resMapel.data && Array.isArray(resMapel.data.data)) {
            listMapel.value = resMapel.data.data;
        } else {
            listMapel.value = [];
        }

        // Validasi Guru (Untuk Dropdown)
        if (resGuru.data && (Array.isArray(resGuru.data) || Array.isArray(resGuru.data.data))) {
            listGuru.value = Array.isArray(resGuru.data) ? resGuru.data : resGuru.data.data;
        }

        // Validasi Kelas (Untuk Dropdown)
        if (resKelas.data && (Array.isArray(resKelas.data) || Array.isArray(resKelas.data.data))) {
            listKelas.value = Array.isArray(resKelas.data) ? resKelas.data : resKelas.data.data;
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
    Object.assign(form, { id: null, nama_mapel: '', guru_id: '', kelas_id: '' });
    showModal.value = true;
};

const openEdit = (item) => {
    isEdit.value = true;
    form.id = item.id;
    form.nama_mapel = item.nama_mapel;
    
    // Mapping ID Guru & Kelas (Handle jika data nested object atau flat ID)
    form.guru_id = item.guru_id || (item.guru ? item.guru.id : '');
    form.kelas_id = item.kelas_id || (item.kelas ? item.kelas.id : '');

    showModal.value = true;
};

// 3. SIMPAN DATA
const simpanData = async () => {
    if (!form.nama_mapel || !form.kelas_id) {
        alert("Nama Mapel dan Kelas wajib diisi!");
        return;
    }

    try {
        if (isEdit.value) {
            await api.put(`/admin/mapel/${form.id}`, form);
            alert("Data mapel diperbarui!");
        } else {
            await api.post('/admin/mapel', form);
            alert("Mapel baru ditambahkan!");
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
    if (!confirm("Yakin ingin menghapus mata pelajaran ini?")) return;
    try {
        await api.delete(`/admin/mapel/${id}`);
        fetchData();
        alert("Data berhasil dihapus.");
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
                    <h2>📚 Data Mata Pelajaran</h2>
                    <p class="subtitle">Kelola jadwal dan pengajar mata pelajaran.</p>
                </div>
                <button @click="openAdd" class="btn btn-primary btn-add">
                    + Tambah Mapel
                </button>
            </div>
        </div>

        <div v-if="isLoading" class="loading-state">
            <div class="spinner"></div>
            <p>Memuat data mapel...</p>
        </div>

        <div v-else class="table-container">
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th style="width: 50px">No</th>
                            <th>Mata Pelajaran</th>
                            <th>Guru Pengampu</th>
                            <th>Kelas</th>
                            <th style="width: 120px">Aksi</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-if="listMapel.length === 0">
                            <td colspan="5" class="text-center">Belum ada data mapel.</td>
                        </tr>
                        <tr v-for="(mapel, index) in listMapel" :key="mapel.id || index">
                            <td>{{ index + 1 }}</td>
                            <td><span class="fw-bold text-dark">{{ mapel.nama_mapel }}</span></td>
                            <td>
                                <span v-if="mapel.guru" class="badge-guru">
                                    👨‍🏫 {{ mapel.guru.name || mapel.guru.nama }}
                                </span>
                                <span v-else class="text-muted text-sm">Belum ditentukan</span>
                            </td>
                            <td>
                                <span v-if="mapel.kelas" class="badge-kelas">
                                    {{ mapel.kelas.nama_kelas || mapel.kelas.name }}
                                </span>
                                <span v-else class="text-muted">-</span>
                            </td>
                            <td>
                                <div class="action-buttons">
                                    <button @click="openEdit(mapel)" class="btn-icon edit" title="Edit">✏️</button>
                                    <button @click="hapusData(mapel.id)" class="btn-icon delete" title="Hapus">🗑️</button>
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
                <h3>{{ isEdit ? 'Edit Mapel' : 'Tambah Mapel' }}</h3>
                <button @click="showModal = false" class="close-btn">&times;</button>
            </div>
            
            <div class="modal-body">
                <div class="form-group">
                    <label>Nama Mata Pelajaran</label>
                    <input v-model="form.nama_mapel" type="text" placeholder="Contoh: Matematika Wajib">
                </div>

                <div class="form-group">
                    <label>Guru Pengampu</label>
                    <select v-model="form.guru_id">
                        <option value="" disabled>-- Pilih Guru --</option>
                        <option v-for="g in listGuru" :key="g.id" :value="g.id">
                            {{ g.name || g.nama }}
                        </option>
                    </select>
                    <small v-if="listGuru.length === 0" style="color:red">Data Guru Kosong</small>
                </div>

                <div class="form-group">
                    <label>Kelas</label>
                    <select v-model="form.kelas_id">
                        <option value="" disabled>-- Pilih Kelas --</option>
                        <option v-for="k in listKelas" :key="k.id" :value="k.id">
                            {{ k.nama_kelas || k.name }}
                        </option>
                    </select>
                    <small v-if="listKelas.length === 0" style="color:red">Data Kelas Kosong</small>
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
/* --- PAGE CONTAINER & SCROLL FIX --- */
.page-container {
    height: 100vh;
    overflow-y: auto; /* Scrollbar muncul di container ini */
    background-color: #f4f6f9;
    box-sizing: border-box;
}

.content-wrapper {
    max-width: 1100px;
    margin: 0 auto;
    padding: 2rem 1.5rem 6rem 1.5rem; /* Padding bawah extra */
    font-family: 'Segoe UI', sans-serif;
    color: #333;
}

/* Header */
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

/* Table */
.table-container { background: white; border-radius: 12px; box-shadow: 0 5px 20px rgba(0,0,0,0.05); border: 1px solid #eee; overflow: hidden; }
.table-responsive { width: 100%; overflow-x: auto; -webkit-overflow-scrolling: touch; }
.custom-table { width: 100%; border-collapse: collapse; white-space: nowrap; }
.custom-table th, .custom-table td { padding: 16px 20px; text-align: left; border-bottom: 1px solid #f0f0f0; }
.custom-table th { background-color: #f8f9fa; color: #34495e; font-weight: 700; font-size: 0.9rem; }
.fw-bold { font-weight: 600; }
.text-dark { color: #2c3e50; }
.text-center { text-align: center; }

/* Badges */
.badge-guru { background: #e8f5e9; color: #2e7d32; padding: 4px 10px; border-radius: 6px; font-size: 0.85rem; font-weight: 600; display: inline-flex; align-items: center; gap: 4px; }
.badge-kelas { background: #e3f2fd; color: #1976d2; padding: 4px 10px; border-radius: 20px; font-size: 0.8rem; font-weight: 600; }
.text-muted { color: #95a5a6; }
.text-sm { font-size: 0.85rem; font-style: italic; }

/* Modal & Form */
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.6); display: flex; justify-content: center; align-items: center; z-index: 1000; padding: 1rem; }
.modal-content { 
    background: white; width: 100%; max-width: 450px; border-radius: 12px; 
    box-shadow: 0 15px 35px rgba(0,0,0,0.2); animation: slideUp 0.3s ease;
    display: flex; flex-direction: column; max-height: 90vh; /* Fix Modal Scroll */
}
.modal-header { padding: 1.25rem; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; }
.modal-body { padding: 1.5rem; overflow-y: auto; }
.modal-footer { padding: 1.25rem; background: #f8f9fa; border-top: 1px solid #eee; display: flex; justify-content: flex-end; gap: 10px; border-radius: 0 0 12px 12px; flex-shrink: 0; }

.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 600; font-size: 0.9rem; color: #34495e; }
.form-group input, .form-group select { width: 100%; padding: 10px 12px; border: 1px solid #dfe6e9; border-radius: 8px; font-size: 0.95rem; box-sizing: border-box; }
.form-group input:focus, .form-group select:focus { outline: none; border-color: #3498db; }
.close-btn { background: transparent; border: none; font-size: 1.5rem; cursor: pointer; color: #95a5a6; }

/* Loading & Animation */
.loading-state { text-align: center; padding: 4rem; color: #95a5a6; }
.spinner { border: 3px solid #f3f3f3; border-top: 3px solid #3498db; border-radius: 50%; width: 30px; height: 30px; animation: spin 1s linear infinite; margin: 0 auto 1rem; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

/* --- RESPONSIVE --- */
@media (max-width: 768px) {
    .content-wrapper { padding: 1rem 1rem 5rem 1rem; }
    .header-content { flex-direction: column; align-items: flex-start; gap: 1rem; }
    .btn-add { width: 100%; justify-content: center; display: flex; }
    .custom-table th, .custom-table td { padding: 12px 15px; font-size: 0.9rem; }
}
</style>
