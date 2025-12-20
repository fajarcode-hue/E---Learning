<script setup>
import { ref, reactive, onMounted } from 'vue';
import api from '../../api';

const listSiswa = ref([]);
const listKelas = ref([]); // Kita butuh data kelas untuk Dropdown
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive({
    id: null,
    name: '',
    nis: '',
    email: '',
    password: '',
    kelas_id: ''
});

// 1. AMBIL DATA SISWA & KELAS
const fetchData = async () => {
    try {
        // Kita butuh 2 data: List Siswa (untuk tabel) & List Kelas (untuk pilihan saat tambah siswa)
        const [resSiswa, resKelas] = await Promise.all([
            api.get('/admin/siswa'),
            api.get('/admin/kelas')
        ]);
        
        listSiswa.value = resSiswa.data.data;
        listKelas.value = resKelas.data.data;
    } catch (error) {
        console.error("Error fetching data:", error);
    }
};

// 2. MODAL CONTROL
const openAdd = () => {
    isEdit.value = false;
    // Reset form
    Object.assign(form, { id: null, name: '', nis: '', email: '', password: '', kelas_id: '' });
    showModal.value = true;
};

const openEdit = (item) => {
    isEdit.value = true;
    // Isi form dengan data yang mau diedit
    Object.assign(form, {
        id: item.id,
        name: item.name,
        nis: item.nis,
        email: item.email,
        kelas_id: item.kelas_id,
        password: '' // Password kosongkan saja, diisi user jika ingin ubah
    });
    showModal.value = true;
};

const closeModal = () => showModal.value = false;

// 3. SIMPAN DATA
const simpanData = async () => {
    try {
        if (isEdit.value) {
            await api.put(`/admin/siswa/${form.id}`, form);
        } else {
            await api.post('/admin/siswa', form);
        }
        alert("Berhasil menyimpan data siswa!");
        closeModal();
        fetchData();
    } catch (error) {
        console.error(error);
        alert("Gagal menyimpan. Cek apakah Email/NIS sudah dipakai.");
    }
};

// 4. HAPUS DATA
const hapusData = async (id) => {
    if (!confirm("Yakin hapus siswa ini?")) return;
    try {
        await api.delete(`/admin/siswa/${id}`);
        fetchData();
    } catch (error) {
        alert("Gagal menghapus.");
    }
};

onMounted(fetchData);
</script>

<template>
<div class="container">
    <div class="header">
        <h2>👨‍🎓 Data Siswa</h2>
        <button @click="openAdd" class="btn-add">+ Tambah Siswa</button>
    </div>

    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Nama</th>
                    <th>NIS</th>
                    <th>Email</th>
                    <th>Kelas</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody>
                <tr v-if="listSiswa.length === 0">
                    <td colspan="5" style="text-align:center;">Belum ada data siswa.</td>
                </tr>
                <tr v-for="siswa in listSiswa" :key="siswa.id">
                    <td>{{ siswa.name }}</td>
                    <td>{{ siswa.nis }}</td>
                    <td>{{ siswa.email }}</td>
                    <td>{{ siswa.kelas ? siswa.kelas.nama_kelas : '-' }}</td>
                    <td>
                        <button @click="openEdit(siswa)" class="btn-edit">Edit</button>
                        <button @click="hapusData(siswa.id)" class="btn-del">Hapus</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <div v-if="showModal" class="modal-overlay">
        <div class="modal">
            <div class="modal-header">
                <h3>{{ isEdit ? 'Edit Siswa' : 'Tambah Siswa Baru' }}</h3>
                <span @click="closeModal" class="close-btn">&times;</span>
            </div>
            
            <div class="modal-body">
                <input v-model="form.name" type="text" placeholder="Nama Lengkap" class="input-form">
                <input v-model="form.nis" type="text" placeholder="Nomor Induk Siswa (NIS)" class="input-form">
                <input v-model="form.email" type="email" placeholder="Email Login" class="input-form">
                
                <select v-model="form.kelas_id" class="input-form">
                    <option value="" disabled>-- Pilih Kelas --</option>
                    <option v-for="k in listKelas" :key="k.id" :value="k.id">
                        {{ k.nama_kelas }}
                    </option>
                </select>

                <input v-model="form.password" type="password" placeholder="Password (Isi jika ingin ubah/baru)" class="input-form">
            </div>

            <div class="modal-actions">
                <button @click="closeModal" class="btn-cancel">Batal</button>
                <button @click="simpanData" class="btn-save">Simpan</button>
            </div>
        </div>
    </div>
</div>
</template>

<style scoped>
/* Gunakan style yang sama persis dengan KelasIndex agar rapi */
.container { max-width: 900px; margin: 2rem auto; padding: 1rem; font-family: sans-serif; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.table { width: 100%; border-collapse: collapse; margin-top: 10px; background: white; }
.table th, .table td { border: 1px solid #eee; padding: 12px; text-align: left; }
.table th { background: #f8f9fa; font-weight: bold; }
.btn-add, .btn-save { background: #3498db; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; }
.btn-edit { background: #f1c40f; padding: 6px 12px; border: none; margin-right: 5px; cursor: pointer; border-radius: 4px; color: white; }
.btn-del, .btn-cancel { background: #e74c3c; color: white; padding: 6px 12px; border: none; cursor: pointer; border-radius: 4px; }
.btn-cancel { background: #95a5a6; margin-right: 10px; }
.modal-overlay { position: fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); display:flex; justify-content:center; align-items:center; z-index: 99; }
.modal { background: white; padding: 25px; border-radius: 8px; width: 400px; display: flex; flex-direction: column; gap: 15px; }
.modal-header { display: flex; justify-content: space-between; font-weight: bold; }
.close-btn { cursor: pointer; font-size: 1.5rem; }
.input-form { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
.modal-actions { display: flex; justify-content: flex-end; }
</style>