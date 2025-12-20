<script setup>
import { ref, reactive, onMounted } from 'vue';
import api from '../../api';

const listKelas = ref([]);
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive({ id: null, nama_kelas: '' });

// FETCH DATA
const fetchData = async () => {
    try {
        const response = await api.get('/admin/kelas');
        listKelas.value = response.data.data;
    } catch (error) {
        console.error("Gagal load kelas:", error);
    }
};

// MODAL CONTROLS
const openAdd = () => {
    isEdit.value = false;
    form.id = null;
    form.nama_kelas = '';
    showModal.value = true;
};

const openEdit = (item) => {
    isEdit.value = true;
    form.id = item.id;
    form.nama_kelas = item.nama_kelas;
    showModal.value = true;
};

const closeModal = () => showModal.value = false;

// CRUD ACTIONS
const simpanData = async () => {
    try {
        if (!form.nama_kelas) return alert("Nama kelas wajib diisi");
        
        if (isEdit.value) {
            await api.put(`/admin/kelas/${form.id}`, { nama_kelas: form.nama_kelas });
        } else {
            await api.post('/admin/kelas', { nama_kelas: form.nama_kelas });
        }
        alert("Berhasil!");
        closeModal();
        fetchData();
    } catch (error) {
        alert("Gagal menyimpan data.");
    }
};

const hapusData = async (id) => {
    if (!confirm("Yakin hapus kelas ini?")) return;
    try {
        await api.delete(`/admin/kelas/${id}`);
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
        <h2>🏫 Data Kelas</h2>
        <button @click="openAdd" class="btn-add">+ Tambah Kelas</button>
    </div>

    <table class="table">
        <thead>
            <tr>
                <th>No</th>
                <th>Nama Kelas</th>
                <th>Aksi</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="(item, index) in listKelas" :key="item.id">
                <td>{{ index + 1 }}</td>
                <td>{{ item.nama_kelas }}</td>
                <td>
                    <button @click="openEdit(item)" class="btn-edit">Edit</button>
                    <button @click="hapusData(item.id)" class="btn-del">Hapus</button>
                </td>
            </tr>
        </tbody>
    </table>

    <div v-if="showModal" class="modal-overlay">
        <div class="modal">
            <h3>{{ isEdit ? 'Edit Kelas' : 'Tambah Kelas' }}</h3>
            <input v-model="form.nama_kelas" placeholder="Nama Kelas (Contoh: X IPA 1)" class="input">
            <div class="modal-actions">
                <button @click="closeModal" class="btn-cancel">Batal</button>
                <button @click="simpanData" class="btn-save">Simpan</button>
            </div>
        </div>
    </div>
</div>
</template>

<style scoped>
/* Style sederhana disamakan dengan admin lain */
.container { max-width: 800px; margin: 2rem auto; padding: 1rem; font-family: sans-serif; }
.header { display: flex; justify-content: space-between; margin-bottom: 1rem; }
.table { width: 100%; border-collapse: collapse; margin-top: 10px; }
.table th, .table td { border: 1px solid #ddd; padding: 10px; text-align: left; }
.table th { background: #f4f4f4; }
.btn-add, .btn-save { background: #3498db; color: white; padding: 8px 15px; border: none; border-radius: 4px; cursor: pointer; }
.btn-edit { background: #f1c40f; padding: 5px 10px; border: none; margin-right: 5px; cursor: pointer; border-radius: 3px; }
.btn-del, .btn-cancel { background: #e74c3c; color: white; padding: 5px 10px; border: none; cursor: pointer; border-radius: 3px; }
.modal-overlay { position: fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); display:flex; justify-content:center; align-items:center; }
.modal { background: white; padding: 20px; border-radius: 8px; width: 400px; display: flex; flex-direction: column; gap: 10px; }
.input { padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
.modal-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 10px; }
</style>