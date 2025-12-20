<script setup>
import { ref, reactive, onMounted } from 'vue';
import api from '../../api';

const listMapel = ref([]);
const listGuru = ref([]);
const listKelas = ref([]);
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive({ id: null, nama_mapel: '', guru_id: '', kelas_id: '' });

const fetchData = async () => {
    const [resMapel, resGuru, resKelas] = await Promise.all([
        api.get('/admin/mapel'),
        api.get('/admin/guru'),
        api.get('/admin/kelas')
    ]);
    listMapel.value = resMapel.data.data;
    listGuru.value = resGuru.data.data;
    listKelas.value = resKelas.data.data;
};

const openAdd = () => { isEdit.value = false; Object.assign(form, { id:null, nama_mapel:'', guru_id:'', kelas_id:'' }); showModal.value = true; };
const openEdit = (item) => { isEdit.value = true; Object.assign(form, item); showModal.value = true; };
const closeModal = () => showModal.value = false;

const simpanData = async () => {
    try {
        if(isEdit.value) await api.put(`/admin/mapel/${form.id}`, form);
        else await api.post('/admin/mapel', form);
        alert("Berhasil!"); closeModal(); fetchData();
    } catch (e) { alert("Gagal menyimpan."); }
};

const hapusData = async (id) => {
    if(confirm("Hapus Mapel?")) { await api.delete(`/admin/mapel/${id}`); fetchData(); }
};

onMounted(fetchData);
</script>

<template>
<div class="container">
    <div class="header">
        <h2>📚 Data Mata Pelajaran</h2>
        <button @click="openAdd" class="btn-add">+ Tambah Mapel</button>
    </div>

    <table class="table">
        <thead><tr><th>Mata Pelajaran</th><th>Guru Pengampu</th><th>Kelas</th><th>Aksi</th></tr></thead>
        <tbody>
            <tr v-for="m in listMapel" :key="m.id">
                <td>{{ m.nama_mapel }}</td>
                <td>{{ m.guru ? m.guru.name : 'Belum ada' }}</td>
                <td>{{ m.kelas ? m.kelas.nama_kelas : '-' }}</td>
                <td>
                    <button @click="openEdit(m)" class="btn-edit">Edit</button>
                    <button @click="hapusData(m.id)" class="btn-del">Hapus</button>
                </td>
            </tr>
        </tbody>
    </table>

    <div v-if="showModal" class="modal-overlay">
        <div class="modal">
            <h3>{{ isEdit ? 'Edit Mapel' : 'Tambah Mapel' }}</h3>
            <input v-model="form.nama_mapel" placeholder="Nama Mapel" class="input">
            
            <select v-model="form.guru_id" class="input">
                <option value="" disabled>Pilih Guru</option>
                <option v-for="g in listGuru" :key="g.id" :value="g.id">{{ g.name }}</option>
            </select>

            <select v-model="form.kelas_id" class="input">
                <option value="" disabled>Pilih Kelas</option>
                <option v-for="k in listKelas" :key="k.id" :value="k.id">{{ k.nama_kelas }}</option>
            </select>

            <div class="modal-actions">
                <button @click="closeModal" class="btn-cancel">Batal</button>
                <button @click="simpanData" class="btn-save">Simpan</button>
            </div>
        </div>
    </div>
</div>
</template>

<style scoped>
/* Gunakan Style yang sama */
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
.input { padding: 10px; border: 1px solid #ddd; border-radius: 4px; width: 100%; box-sizing: border-box; }
.modal-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 10px; }
</style>