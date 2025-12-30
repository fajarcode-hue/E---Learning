<script setup>
import { ref, reactive, onMounted } from 'vue';
import api from '../../api'; // Pastikan path import ini benar

// STATE VARIABLES
const listSiswa = ref([]);
const listKelas = ref([]); // Data untuk Dropdown Kelas
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

// 1. FETCH DATA (SISWA & KELAS)
const fetchData = async () => {
    isLoading.value = true;
    try {
        console.log("🔵 Memulai Fetch Data Siswa & Kelas...");
        
        // Gunakan Promise.all agar request berjalan paralel (lebih cepat)
        const [resSiswa, resKelas] = await Promise.all([
            api.get('/admin/siswa'),
            api.get('/admin/kelas')
        ]);

        console.log("🟢 Respon Siswa Mentah:", resSiswa.data);
        console.log("🟢 Respon Kelas Mentah:", resKelas.data);

        // --- VALIDASI DATA SISWA ---
        if (Array.isArray(resSiswa.data)) {
            listSiswa.value = resSiswa.data;
        } else if (resSiswa.data && Array.isArray(resSiswa.data.data)) {
            listSiswa.value = resSiswa.data.data;
        } else {
            console.warn("⚠️ Format data Siswa tidak dikenali");
            listSiswa.value = [];
        }

        // --- VALIDASI DATA KELAS (PENTING UNTUK DROPDOWN) ---
        if (Array.isArray(resKelas.data)) {
            listKelas.value = resKelas.data;
        } else if (resKelas.data && Array.isArray(resKelas.data.data)) {
            listKelas.value = resKelas.data.data;
        } else {
            console.warn("⚠️ Format data Kelas tidak dikenali");
            listKelas.value = [];
        }

    } catch (error) {
        console.error("🔴 Gagal Fetch Data:", error);
        alert("Gagal memuat data. Cek koneksi atau console log.");
    } finally {
        isLoading.value = false;
    }
};

// 2. MODAL CONTROLS
const openAdd = () => {
    isEdit.value = false;
    // Reset form bersih
    form.id = null;
    form.name = '';
    form.nis = '';
    form.email = '';
    form.password = '';
    form.kelas_id = ''; // Reset dropdown ke default
    showModal.value = true;
};

const openEdit = (item) => {
    console.log("🟡 Item yang diedit:", item);
    isEdit.value = true;

    // Mapping Data (Jaga-jaga nama field beda)
    form.id = item.id;
    form.name = item.name || item.nama || item.nama_siswa || '';
    form.nis = item.nis || item.nomor_induk || '';
    form.email = item.email || '';
    
    // Mapping Kelas ID (Cek apakah backend kirim 'kelas_id' atau nested object 'kelas.id')
    if (item.kelas_id) {
        form.kelas_id = item.kelas_id;
    } else if (item.kelas && item.kelas.id) {
        form.kelas_id = item.kelas.id;
    } else {
        form.kelas_id = '';
    }

    form.password = ''; // Password selalu kosong saat edit
    showModal.value = true;
};

// 3. SIMPAN DATA
const simpanData = async () => {
    // Validasi Sederhana
    if (!form.name || !form.kelas_id) {
        alert("Nama dan Kelas wajib diisi!");
        return;
    }

    try {
        if (isEdit.value) {
            console.log("Mengirim Update:", form);
            await api.put(`/admin/siswa/${form.id}`, form);
            alert("Berhasil memperbarui data siswa!");
        } else {
            console.log("Mengirim Data Baru:", form);
            await api.post('/admin/siswa', form);
            alert("Berhasil menambah siswa baru!");
        }
        
        showModal.value = false;
        fetchData(); // Refresh data tanpa reload page

    } catch (error) {
        console.error("🔴 Gagal Simpan:", error.response || error);
        const msg = error.response?.data?.message || "Gagal menyimpan. Cek NIS/Email (mungkin duplikat).";
        alert(msg);
    }
};

// 4. HAPUS DATA
const hapusData = async (id) => {
    if (!confirm("Yakin ingin menghapus data siswa ini?")) return;
    try {
        await api.delete(`/admin/siswa/${id}`);
        fetchData();
        alert("Data siswa dihapus.");
    } catch (error) {
        console.error("🔴 Gagal Hapus:", error);
        alert("Gagal menghapus data.");
    }
};

onMounted(fetchData);
</script>

<template>
<div class="page-container">
    <!-- Header -->
    <div class="header">
        <div>
            <h2>👨‍🎓 Data Siswa</h2>
            <p class="subtitle">Daftar siswa dan pembagian kelas.</p>
        </div>
        <button @click="openAdd" class="btn btn-primary">+ Tambah Siswa</button>
    </div>

    <!-- Loading -->
    <div v-if="isLoading" class="loading-state">
        <div class="spinner"></div>
        <p>Memuat data siswa & kelas...</p>
    </div>

    <!-- Tabel -->
    <div v-else class="table-wrapper">
        <table class="custom-table">
            <thead>
                <tr>
                    <th>No</th>
                    <th>NIS</th>
                    <th>Nama Lengkap</th>
                    <th>Kelas</th>
                    <th>Email</th>
                    <th>Aksi</th>
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
                    
                    <!-- Menampilkan nama kelas dengan aman (null check) -->
                    <td>
                        <span v-if="siswa.kelas" class="badge-kelas">{{ siswa.kelas.nama_kelas || siswa.kelas.name }}</span>
                        <span v-else class="text-muted">-</span>
                    </td>
                    
                    <td>{{ siswa.email }}</td>
                    <td>
                        <button @click="openEdit(siswa)" class="btn-icon edit" title="Edit">✏️</button>
                        <button @click="hapusData(siswa.id)" class="btn-icon delete" title="Hapus">🗑️</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal-content">
            <div class="modal-header">
                <h3>{{ isEdit ? 'Edit Siswa' : 'Tambah Siswa Baru' }}</h3>
                <button @click="showModal = false" class="close-btn">&times;</button>
            </div>
            
            <div class="modal-body">
                <div class="form-group">
                    <label>NIS (Nomor Induk Siswa)</label>
                    <input v-model="form.nis" type="text" placeholder="Contoh: 12345" class="input-form">
                </div>

                <div class="form-group">
                    <label>Nama Lengkap</label>
                    <input v-model="form.name" type="text" placeholder="Nama Siswa" class="input-form">
                </div>

                <div class="form-group">
                    <label>Kelas</label>
                    <select v-model="form.kelas_id" class="input-form">
                        <option value="" disabled>-- Pilih Kelas --</option>
                        <!-- Loop Kelas untuk Dropdown -->
                        <option v-for="k in listKelas" :key="k.id" :value="k.id">
                            {{ k.nama_kelas || k.name || k.nama }}
                        </option>
                    </select>
                    <small v-if="listKelas.length === 0" style="color:red">Data Kelas kosong / Gagal dimuat</small>
                </div>

                <div class="form-group">
                    <label>Email Login</label>
                    <input v-model="form.email" type="email" placeholder="email@sekolah.sch.id" class="input-form">
                </div>

                <div class="form-group">
                    <label>Password</label>
                    <input v-model="form.password" type="password" placeholder="Isi jika ingin ubah password" class="input-form">
                    <small v-if="isEdit" class="text-muted">*Biarkan kosong jika tidak ingin mengganti password</small>
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
/* Styling Konsisten dengan GuruIndex */
.page-container { max-width: 1000px; margin: 2rem auto; padding: 0 1rem; font-family: 'Segoe UI', sans-serif; color: #333; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; }
.header h2 { margin: 0; color: #2c3e50; }
.subtitle { margin: 5px 0 0; color: #7f8c8d; font-size: 0.9rem; }

/* Table */
.table-wrapper { background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); overflow: hidden; }
.custom-table { width: 100%; border-collapse: collapse; }
.custom-table th, .custom-table td { padding: 15px; text-align: left; border-bottom: 1px solid #eee; }
.custom-table th { background-color: #f8f9fa; color: #2c3e50; font-weight: 600; }
.text-center { text-align: center; color: #999; padding: 2rem; }

/* Badges */
.badge-nis { background: #eee; padding: 3px 8px; border-radius: 4px; font-family: monospace; font-weight: bold; }
.badge-kelas { background: #e8f4fd; color: #3498db; padding: 3px 8px; border-radius: 4px; font-weight: 600; font-size: 0.85rem; }

/* Buttons */
.btn { padding: 10px 20px; border: none; border-radius: 6px; cursor: pointer; font-weight: 600; transition: 0.2s; }
.btn-primary { background: #3498db; color: white; }
.btn-primary:hover { background: #2980b9; }
.btn-secondary { background: #95a5a6; color: white; margin-right: 10px; }
.btn-icon { width: 32px; height: 32px; border: none; border-radius: 4px; cursor: pointer; font-size: 1.1rem; margin-right: 5px; }
.edit { background: #f1c40f40; color: #f39c12; }
.delete { background: #e74c3c40; color: #c0392b; }

/* Modal */
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; backdrop-filter: blur(2px); }
.modal-content { background: white; width: 450px; border-radius: 10px; box-shadow: 0 10px 25px rgba(0,0,0,0.2); animation: slideDown 0.3s ease; overflow: hidden; }
.modal-header { background: #f8f9fa; padding: 15px 20px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; }
.close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #aaa; }
.modal-body { padding: 20px; }
.modal-footer { padding: 15px 20px; border-top: 1px solid #eee; display: flex; justify-content: flex-end; }

/* Form */
.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 5px; font-weight: 500; font-size: 0.9rem; }
.input-form { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box; }
.input-form:focus { outline: none; border-color: #3498db; }
.text-muted { font-size: 0.8rem; color: #888; }

/* Loading */
.loading-state { text-align: center; padding: 3rem; color: #7f8c8d; }
.spinner { border: 4px solid #f3f3f3; border-top: 4px solid #3498db; border-radius: 50%; width: 30px; height: 30px; animation: spin 1s linear infinite; margin: 0 auto 10px; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
@keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
</style>