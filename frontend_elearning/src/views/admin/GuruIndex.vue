<script setup>
import { ref, reactive, onMounted } from 'vue';
import api from '../../api'; // Pastikan path ini sesuai dengan lokasi file api.js kamu

// STATE VARIABLES
const listGuru = ref([]);
const isLoading = ref(false);
const showModal = ref(false);
const isEdit = ref(false);

// FORM STATE
// Kita inisialisasi dengan nilai default kosong
const form = reactive({
    id: null,
    name: '',
    nip: '',
    email: '',
    password: ''
});

// 1. FETCH DATA (DENGAN PENGECEKAN STRUKTUR)
const fetchData = async () => {
    isLoading.value = true;
    try {
        console.log("🔵 Memulai Fetch Data...");
        const res = await api.get('/admin/guru');
        
        console.log("🟢 Respon Mentah dari API:", res.data); // Cek ini di Console Browser!

        // LOGIKA DETEKSI STRUKTUR DATA
        if (Array.isArray(res.data)) {
            // Kasus 1: Backend kirim langsung array [ {...}, {...} ]
            listGuru.value = res.data;
        } else if (res.data && Array.isArray(res.data.data)) {
            // Kasus 2: Backend kirim wrapper { data: [ {...}, {...} ], message: "..." }
            listGuru.value = res.data.data;
        } else {
            console.error("🔴 Struktur data tidak dikenali. Cek network tab.");
            alert("Format data API berbeda dari perkiraan. Cek Console.");
        }

    } catch (error) {
        console.error("🔴 Gagal Fetch:", error);
        alert("Gagal mengambil data guru.");
    } finally {
        isLoading.value = false;
    }
};

// 2. BUKA MODAL TAMBAH
const openAdd = () => {
    isEdit.value = false;
    // Reset Form Manual
    form.id = null;
    form.name = '';
    form.nip = '';
    form.email = '';
    form.password = '';
    showModal.value = true;
};

// 3. BUKA MODAL EDIT (DENGAN MAPPING EKSPLISIT)
const openEdit = (item) => {
    console.log("🟡 Item yang diklik untuk Edit:", item); // Cek nama field di sini!
    
    isEdit.value = true;
    
    // MAPPING DATA KE FORM
    // Di sini kita pastikan data masuk ke form yang benar.
    // Jika di database namanya 'nama_guru' tapi di form 'name', sesuaikan di baris ini.
    form.id = item.id;
    
    // Gunakan 'OR' logic (||) untuk jaga-jaga beda penamaan field dari API
    form.name = item.name || item.nama || item.full_name || ''; 
    form.nip = item.nip || item.nomor_induk || item.nip_guru || '';
    form.email = item.email || '';
    
    form.password = ''; // Password selalu dikosongkan saat edit untuk keamanan
    
    showModal.value = true;
};

// 4. SIMPAN DATA (CREATE / UPDATE)
const simpanData = async () => {
    // Validasi sederhana
    if (!form.name || !form.email) {
        alert("Nama dan Email wajib diisi!");
        return;
    }

    try {
        if (isEdit.value) {
            // Mode EDIT (PUT)
            console.log("Mengirim Update:", form);
            await api.put(`/admin/guru/${form.id}`, form);
            alert("Data berhasil diperbarui!");
        } else {
            // Mode TAMBAH (POST)
            console.log("Mengirim Data Baru:", form);
            await api.post('/admin/guru', form);
            alert("Guru berhasil ditambahkan!");
        }
        
        showModal.value = false;
        fetchData(); // Refresh tabel

    } catch (error) {
        console.error("🔴 Gagal Simpan:", error.response || error);
        // Tampilkan pesan error dari backend jika ada
        const msg = error.response?.data?.message || "Terjadi kesalahan saat menyimpan.";
        alert(msg);
    }
};

// 5. HAPUS DATA
const hapusData = async (id) => {
    if (!confirm("Yakin ingin menghapus guru ini? Data tidak bisa dikembalikan.")) return;

    try {
        await api.delete(`/admin/guru/${id}`);
        alert("Data berhasil dihapus.");
        fetchData();
    } catch (error) {
        console.error("🔴 Gagal Hapus:", error);
        alert("Gagal menghapus data.");
    }
};

onMounted(() => {
    fetchData();
});
</script>

<template>
<div class="page-container">
    <!-- Header Section -->
    <div class="header">
        <div>
            <h2>👨‍🏫 Manajemen Data Guru</h2>
            <p class="subtitle">Kelola daftar pengajar di sistem.</p>
        </div>
        <button @click="openAdd" class="btn btn-primary">
            + Tambah Guru Baru
        </button>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="loading-state">
        <div class="spinner"></div>
        <p>Sedang memuat data...</p>
    </div>

    <!-- Table Section -->
    <div v-else class="table-wrapper">
        <table class="custom-table">
            <thead>
                <tr>
                    <th style="width: 50px">No</th>
                    <th>Nama Lengkap</th>
                    <th>NIP</th>
                    <th>Email</th>
                    <th style="width: 150px">Aksi</th>
                </tr>
            </thead>
            <tbody>
                <tr v-if="listGuru.length === 0">
                    <td colspan="5" class="text-center">Belum ada data guru. Silakan tambah data.</td>
                </tr>
                <tr v-for="(guru, index) in listGuru" :key="guru.id || index">
                    <td>{{ index + 1 }}</td>
                    <!-- Perhatikan field di bawah: pastikan sesuai dgn console log -->
                    <td>
                        <span class="fw-bold">{{ guru.name || guru.nama }}</span>
                    </td>
                    <td>
                        <span class="badge">{{ guru.nip || '-' }}</span>
                    </td>
                    <td>{{ guru.email }}</td>
                    <td>
                        <div class="action-buttons">
                            <button @click="openEdit(guru)" class="btn-icon edit" title="Edit">
                                ✏️
                            </button>
                            <button @click="hapusData(guru.id)" class="btn-icon delete" title="Hapus">
                                🗑️
                            </button>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <!-- Modal Form -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal-content">
            <div class="modal-header">
                <h3>{{ isEdit ? 'Edit Data Guru' : 'Tambah Guru Baru' }}</h3>
                <button @click="showModal = false" class="close-btn">&times;</button>
            </div>
            
            <div class="modal-body">
                <div class="form-group">
                    <label>Nama Lengkap</label>
                    <input v-model="form.name" type="text" placeholder="Contoh: Budi Santoso, S.Pd">
                </div>
                
                <div class="form-group">
                    <label>NIP (Nomor Induk Pegawai)</label>
                    <input v-model="form.nip" type="text" placeholder="1980xxxx...">
                </div>

                <div class="form-group">
                    <label>Email</label>
                    <input v-model="form.email" type="email" placeholder="email@sekolah.sch.id">
                </div>

                <div class="form-group">
                    <label>Password</label>
                    <input v-model="form.password" type="password" placeholder="Isi hanya jika ingin mengubah password">
                    <small v-if="isEdit" class="text-muted">*Kosongkan jika tidak ingin mengganti password</small>
                </div>
            </div>

            <div class="modal-footer">
                <button @click="showModal = false" class="btn btn-secondary">Batal</button>
                <button @click="simpanData" class="btn btn-primary">Simpan Data</button>
            </div>
        </div>
    </div>
</div>
</template>

<style scoped>
/* Styling Modern & Bersih */
.page-container {
    max-width: 1100px;
    margin: 2rem auto;
    padding: 0 1rem;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    color: #333;
}

/* Header */
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}
.header h2 { margin: 0; color: #2c3e50; }
.subtitle { margin: 5px 0 0; color: #7f8c8d; font-size: 0.9rem; }

/* Buttons */
.btn {
    padding: 10px 20px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-weight: 600;
    transition: all 0.2s;
}
.btn-primary { background-color: #3498db; color: white; }
.btn-primary:hover { background-color: #2980b9; }
.btn-secondary { background-color: #95a5a6; color: white; }
.btn-secondary:hover { background-color: #7f8c8d; }

.btn-icon {
    width: 32px;
    height: 32px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.1rem;
    transition: transform 0.1s;
}
.btn-icon:hover { transform: scale(1.1); }
.edit { background-color: #f1c40f40; color: #f39c12; margin-right: 5px; }
.delete { background-color: #e74c3c40; color: #c0392b; }

/* Table */
.table-wrapper {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.05);
    overflow: hidden;
}
.custom-table {
    width: 100%;
    border-collapse: collapse;
}
.custom-table th, .custom-table td {
    padding: 15px;
    text-align: left;
    border-bottom: 1px solid #eee;
}
.custom-table th {
    background-color: #f8f9fa;
    color: #2c3e50;
    font-weight: 600;
}
.custom-table tr:last-child td { border-bottom: none; }
.custom-table tr:hover { background-color: #fbfbfb; }

.badge {
    background: #e8f4fd;
    color: #3498db;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 0.85rem;
    font-family: monospace;
}
.action-buttons { display: flex; }
.text-center { text-align: center; color: #999; padding: 2rem; }

/* Modal */
.modal-overlay {
    position: fixed; top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0,0,0,0.5);
    display: flex; justify-content: center; align-items: center;
    z-index: 1000;
    backdrop-filter: blur(2px);
}
.modal-content {
    background: white;
    width: 450px;
    border-radius: 10px;
    box-shadow: 0 10px 25px rgba(0,0,0,0.2);
    overflow: hidden;
    animation: slideDown 0.3s ease;
}
.modal-header {
    background: #f8f9fa;
    padding: 15px 20px;
    border-bottom: 1px solid #eee;
    display: flex; justify-content: space-between; align-items: center;
}
.modal-header h3 { margin: 0; font-size: 1.1rem; }
.close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #aaa; }

.modal-body { padding: 20px; }
.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 5px; font-weight: 500; font-size: 0.9rem; }
.form-group input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 6px;
    box-sizing: border-box; /* Penting agar padding tidak melebarkan input */
}
.form-group input:focus { outline: none; border-color: #3498db; }
.text-muted { font-size: 0.8rem; color: #888; margin-top: 4px; display: block; }

.modal-footer {
    padding: 15px 20px;
    border-top: 1px solid #eee;
    display: flex; justify-content: flex-end;
    gap: 10px;
}

/* Loading */
.loading-state { text-align: center; padding: 3rem; color: #7f8c8d; }
.spinner {
    border: 4px solid #f3f3f3; border-top: 4px solid #3498db;
    border-radius: 50%; width: 30px; height: 30px;
    animation: spin 1s linear infinite; margin: 0 auto 10px;
}
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
@keyframes slideDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
</style>
```

### Langkah Penting untuk Solving Masalah Kamu:

1.  **Jalankan Kode di atas.**
2.  Buka Browser kamu, tekan **F12** atau klik kanan -> **Inspect**.
3.  Pindah ke tab **Console**.
4.  Lakukan **Refresh Halaman**.
5.  Lihat pesan **"🟢 Respon Mentah dari API:"**:
    * Jika kamu melihat struktur seperti: `{ id: 1, nama_lengkap: "Budi", ... }`
    * Berarti field di database kamu adalah `nama_lengkap`, bukan `name`.
6.  Lakukan **Klik tombol Edit** pada salah satu guru.
7.  Lihat pesan **"🟡 Item yang diklik untuk Edit:"**:
    * Sesuaikan baris kode di dalam fungsi `openEdit` pada bagian:
        ```javascript
        form.name = item.name || item.nama || item.nama_lengkap; // Sesuaikan di sini