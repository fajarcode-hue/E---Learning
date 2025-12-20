<script setup>
import { ref, reactive, onMounted } from 'vue';
import api from '../../api';

const listGuru = ref([]);
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive({ id: null, name: '', email: '', password: '', nip: '' });

// FETCH DATA
const fetchData = async () => {
    const res = await api.get('/admin/guru');
    listGuru.value = res.data.data;
};

// MODAL CONTROLS
const openAdd = () => { isEdit.value = false; Object.assign(form, { id:null, name:'', email:'', password:'', nip:'' }); showModal.value = true; };
const openEdit = (item) => { isEdit.value = true; Object.assign(form, item); form.password = ''; showModal.value = true; }; // Password dikosongkan saat edit
const closeModal = () => showModal.value = false;

// CRUD ACTIONS
const simpanData = async () => {
    try {
        if(isEdit.value) {
            await api.put(`/admin/guru/${form.id}`, form);
        } else {
            await api.post('/admin/guru', form);
        }
        alert("Berhasil menyimpan data!");
        closeModal();
        fetchData();
    } catch (e) { alert("Gagal menyimpan. Cek inputan."); }
};

const hapusData = async (id) => {
    if(!confirm("Hapus Guru ini?")) return;
    await api.delete(`/admin/guru/${id}`);
    fetchData();
};

onMounted(fetchData);
</script>

<template>
<div class="container">
    <div class="header">
        <h2>👨‍🏫 Data Guru</h2>
        <button @click="openAdd" class="btn-add">+ Tambah Guru</button>
    </div>
    
    <table class="table">
        <thead><tr><th>Nama</th><th>NIP</th><th>Email</th><th>Aksi</th></tr></thead>
        <tbody>
            <tr v-for="guru in listGuru" :key="guru.id">
                <td>{{ guru.name }}</td>
                <td>{{ guru.nip }}</td>
                <td>{{ guru.email }}</td>
                <td>
                    <button @click="openEdit(guru)" class="btn-edit">Edit</button>
                    <button @click="hapusData(guru.id)" class="btn-del">Hapus</button>
                </td>
            </tr>
        </tbody>
    </table>

    <div v-if="showModal" class="modal-overlay">
        <div class="modal">
            <h3>{{ isEdit ? 'Edit Guru' : 'Tambah Guru' }}</h3>
            <input v-model="form.name" placeholder="Nama Lengkap" class="input">
            <input v-model="form.nip" placeholder="NIP" class="input">
            <input v-model="form.email" placeholder="Email" class="input">
            <input v-model="form.password" type="password" placeholder="Password (Isi jika ingin ubah)" class="input">
            <div class="modal-actions">
                <button @click="closeModal" class="btn-cancel">Batal</button>
                <button @click="simpanData" class="btn-save">Simpan</button>
            </div>
        </div>
    </div>
</div>
</template>

<style scoped>
/* CSS GLOBAL ADMIN SEDERHANA */
.container { max-width: 1000px; margin: 2rem auto; padding: 1rem; font-family: sans-serif; }
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