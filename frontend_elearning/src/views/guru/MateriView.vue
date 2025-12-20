<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import api from '../../api'; // Pastikan path api sesuai

const route = useRoute();
const mapelId = route.params.id; 
const namaMapel = route.query.nama || 'Mata Pelajaran';

// --- STATE UTAMA ---
const listMateri = ref([]);
const isLoadingMateri = ref(true);

// --- STATE MODAL SOAL & NILAI ---
const showModalSoal = ref(false);
const activeMateri = ref(null); 
const activeTab = ref('soal'); // 'soal' atau 'nilai'
const formSoal = ref([]);
const listNilai = ref([]);
const isLoadingModal = ref(false);

// --- STATE MODAL MATERI (TAMBAH/EDIT) ---
const showModalFormMateri = ref(false);
const isEditing = ref(false); 

// Form Materi
const formMateri = reactive({
    id: null,
    judul_materi: '', 
    deskripsi: '',
    file: null 
});

// ==========================================
// 1. HELPER & FUNGSI LOAD DATA UTAMA
// ==========================================
const getFileUrl = (filePath) => {
    if (!filePath) return '#';
    let cleanPath = filePath.replace('public/', '');
    return `http://localhost:8000/storage/${cleanPath}`;
};

const fetchMateri = async () => {
    isLoadingMateri.value = true;
    try {
        const response = await api.get(`/guru/materi/mapel/${mapelId}`);
        listMateri.value = response.data.data; 
    } catch (error) {
        console.error("Gagal ambil materi:", error);
    } finally {
        isLoadingMateri.value = false;
    }
};

// ==========================================
// 2. FUNGSI HAPUS MATERI
// ==========================================
const hapusMateri = async (id) => {
    if (!confirm("Yakin ingin menghapus materi ini beserta soalnya?")) return;
    try {
        await api.delete(`/guru/materi/${id}`);
        alert("Materi berhasil dihapus");
        fetchMateri(); 
    } catch (error) {
        console.error(error);
        alert("Gagal menghapus materi.");
    }
};

// ==========================================
// 3. FUNGSI TAMBAH / EDIT MATERI
// ==========================================
const openModalTambahMateri = () => {
    isEditing.value = false;
    formMateri.id = null;
    formMateri.judul_materi = ''; 
    formMateri.deskripsi = '';
    formMateri.file = null;
    showModalFormMateri.value = true;
};

const openModalEditMateri = (materi) => {
    isEditing.value = true;
    formMateri.id = materi.id;
    formMateri.judul_materi = materi.judul_materi; 
    formMateri.deskripsi = materi.deskripsi;
    formMateri.file = null; 
    showModalFormMateri.value = true;
};

const handleFileUpload = (event) => {
    formMateri.file = event.target.files[0];
};

const submitMateri = async () => {
    if(!formMateri.judul_materi) return alert("Judul materi wajib diisi");

    const btnSimpan = document.getElementById('btn-simpan-materi');
    if(btnSimpan) {
        btnSimpan.innerText = 'Menyimpan...';
        btnSimpan.disabled = true;
    }

    try {
        const formData = new FormData();
        formData.append('mapel_id', mapelId);
        formData.append('judul_materi', formMateri.judul_materi); 
        formData.append('deskripsi', formMateri.deskripsi || '-'); 

        if (formMateri.file) {
            formData.append('file_materi', formMateri.file);
        }
        
        const config = { headers: { 'Content-Type': 'multipart/form-data' } };

        if (isEditing.value) {
            formData.append('_method', 'PUT'); 
            await api.post(`/guru/materi/${formMateri.id}`, formData, config);
            alert("Materi berhasil diperbarui!");
        } else {
            await api.post(`/guru/materi`, formData, config);
            alert("Materi berhasil ditambahkan!");
        }

        showModalFormMateri.value = false;
        fetchMateri();

    } catch (error) {
        console.error("Error submit materi:", error.response);
        const msg = error.response?.data?.message || "Gagal menyimpan materi.";
        alert(msg);
    } finally {
        if(btnSimpan) {
            btnSimpan.innerText = 'Simpan';
            btnSimpan.disabled = false;
        }
    }
};

// ==========================================
// 4. LOGIKA MODAL SOAL (LATIHAN) & NILAI
// ==========================================

const openKelolaSoal = async (materi) => {
    activeMateri.value = materi;
    showModalSoal.value = true;
    activeTab.value = 'soal'; // Default tab
    formSoal.value = [{ pertanyaan: '', pilihan: { A: '', B: '', C: '', D: '' }, kunci: 'A' }];
    listNilai.value = [];
    
    await loadDataSoal(materi.id);
};

const switchTab = (tabName) => {
    activeTab.value = tabName;
    if (tabName === 'nilai' && activeMateri.value) {
        loadHistoryNilai(activeMateri.value.id);
    }
};

// -- LOAD DATA SOAL --
const loadDataSoal = async (materiId) => {
    isLoadingModal.value = true;
    try {
        const resSoal = await api.get(`/guru/latihan/materi/${materiId}`);
        const data = resSoal.data.data; 
        
        if (data && data.soal && data.soal.length > 0) {
            formSoal.value = data.soal.map(item => ({
                pertanyaan: item.pertanyaan,
                pilihan: item.pilihan, 
                kunci: data.kunci_jawaban[item.id] || 'A'
            }));
        } else {
             formSoal.value = [{ pertanyaan: '', pilihan: { A: '', B: '', C: '', D: '' }, kunci: 'A' }];
        }
    } catch (e) {
        console.log("Belum ada soal tersimpan.");
    } finally {
        isLoadingModal.value = false;
    }
};

// -- LOAD HISTORY NILAI --
const loadHistoryNilai = async (materiId) => {
    isLoadingModal.value = true;
    try {
        const response = await api.get(`/guru/latihan/nilai/${materiId}`);
        // API kamu mengembalikan { success: true, data: [...] }
        listNilai.value = response.data.data; 
    } catch (error) {
        console.error("Gagal memuat nilai:", error);
        listNilai.value = [];
    } finally {
        isLoadingModal.value = false;
    }
};

// -- OPERASI SOAL --
const tambahBarisSoal = () => {
    formSoal.value.push({ pertanyaan: '', pilihan: { A: '', B: '', C: '', D: '' }, kunci: 'A' });
};

const hapusBarisSoal = (index) => {
    if (formSoal.value.length > 1) formSoal.value.splice(index, 1);
};

const simpanSoal = async () => {
    if (!confirm("Simpan perubahan soal?")) return;
    try {
        let payloadSoal = [];
        let payloadKunci = {};

        formSoal.value.forEach((item, index) => {
            const idBaru = index + 1;
            payloadSoal.push({
                id: idBaru,
                pertanyaan: item.pertanyaan,
                pilihan: item.pilihan
            });
            payloadKunci[idBaru] = item.kunci;
        });

        const payload = {
            materi_id: activeMateri.value.id,
            soal: payloadSoal,
            kunci_jawaban: payloadKunci
        };

        await api.post('/guru/latihan', payload);
        alert("Berhasil menyimpan soal!");
        showModalSoal.value = false;
    } catch (error) {
        console.error("Error Simpan Soal:", error);
        alert(error.response?.data?.message || "Gagal menyimpan soal.");
    }
};

onMounted(() => {
    fetchMateri();
});
</script>

<template>
<div class="container">
    
    <div class="header-wrapper">
        <router-link to="/guru/dashboard" class="btn-back" title="Kembali ke Dashboard">
            <span class="icon">➜</span>
        </router-link>
        <div class="header-text">
            <h2 class="title">{{ namaMapel }}</h2>
            <p class="subtitle">Kelola Materi & Soal Latihan</p>
        </div>
        <button @click="openModalTambahMateri" class="btn-add-materi">+ Tambah Materi</button>
    </div>

    <div v-if="isLoadingMateri" class="loading">
        <div class="spinner"></div>
        <p>Memuat materi...</p>
    </div>

    <div v-else-if="listMateri.length === 0" class="empty-state">
        <p>Belum ada materi. Silakan klik tombol "Tambah Materi".</p>
    </div>

    <div v-else class="timeline-container">
        <div class="timeline-line"></div>
        <div v-for="(materi, index) in listMateri" :key="materi.id" class="timeline-item">
            <div class="timeline-marker">{{ index + 1 }}</div>
            <div class="timeline-content">
                <div class="content-header">
                    <h3>{{ materi.judul_materi }}</h3>
                    <span class="date">{{ new Date(materi.created_at).toLocaleDateString('id-ID') }}</span>
                </div>
                <div class="study-section">
                    <p class="deskripsi">{{ materi.deskripsi || 'Tidak ada deskripsi.' }}</p>
                    <a v-if="materi.file_path" :href="getFileUrl(materi.file_path)" target="_blank" class="btn-file">
                        📄 Lihat File Materi
                    </a>
                </div>
                <div class="action-section">
                    <div class="divider"></div>
                    <div class="admin-controls">
                        <div class="control-info">
                            <strong>Panel Guru</strong>
                            <small>Kelola soal atau edit materi ini.</small>
                        </div>
                        <div class="btn-group">
                            <button @click="openKelolaSoal(materi)" class="btn-action btn-soal">📝 Kelola Soal</button>
                            <button @click="openModalEditMateri(materi)" class="btn-action btn-edit">✏️ Edit</button>
                            <button @click="hapusMateri(materi.id)" class="btn-action btn-delete">🗑️ Hapus</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div v-if="showModalFormMateri" class="modal-overlay">
        <div class="modal-content small-modal">
            <div class="modal-header">
                <h3>{{ isEditing ? 'Edit Materi' : 'Tambah Materi Baru' }}</h3>
                <button @click="showModalFormMateri = false" class="close-btn">×</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label>Judul Materi</label>
                    <input v-model="formMateri.judul_materi" type="text" placeholder="Contoh: Aljabar Linear">
                </div>
                <div class="form-group">
                    <label>Deskripsi</label>
                    <textarea v-model="formMateri.deskripsi" rows="3" placeholder="Deskripsi singkat..."></textarea>
                </div>
                <div class="form-group">
                    <label>File Materi (PDF/Doc)</label>
                    <input @change="handleFileUpload" type="file">
                    <small v-if="isEditing" class="text-muted">*Kosongkan jika tidak ingin mengubah file</small>
                </div>
            </div>
            <div class="modal-footer">
                <button @click="showModalFormMateri = false" class="btn-cancel">Batal</button>
                <button id="btn-simpan-materi" @click="submitMateri" class="btn-save">Simpan</button>
            </div>
        </div>
    </div>

    <div v-if="showModalSoal" class="modal-overlay">
        <div class="modal-content large-modal">
            <div class="modal-header">
                <h3>Kelola Soal: {{ activeMateri?.judul_materi }}</h3>
                <button @click="showModalSoal = false" class="close-btn">×</button>
            </div>
            
            <div class="modal-tabs">
                <button :class="{ active: activeTab === 'soal' }" @click="switchTab('soal')">📝 Editor Soal</button>
                <button :class="{ active: activeTab === 'nilai' }" @click="switchTab('nilai')">📊 Hasil Siswa</button>
            </div>

            <div class="modal-body">
                <div v-if="isLoadingModal" class="loading-modal">
                    <div class="spinner-small"></div> Memuat data...
                </div>
                
                <div v-else-if="activeTab === 'soal'">
                    <div class="editor-scroll">
                        <div v-for="(item, index) in formSoal" :key="index" class="soal-box">
                            <div class="box-header">
                                <span class="badge">No. {{ index + 1 }}</span>
                                <button @click="hapusBarisSoal(index)" class="text-danger">Hapus</button>
                            </div>
                            <textarea v-model="item.pertanyaan" class="input-pertanyaan" placeholder="Tulis pertanyaan..."></textarea>
                            <div class="grid-opsi">
                                <div v-for="(val, key) in item.pilihan" :key="key" class="opsi-row">
                                    <span class="opsi-label">{{ key }}</span>
                                    <input v-model="item.pilihan[key]" :placeholder="'Jawaban ' + key">
                                </div>
                            </div>
                            <div class="kunci-row">
                                <label>Kunci Jawaban:</label>
                                <select v-model="item.kunci">
                                    <option value="A">A</option>
                                    <option value="B">B</option>
                                    <option value="C">C</option>
                                    <option value="D">D</option>
                                </select>
                            </div>
                        </div>
                        <button @click="tambahBarisSoal" class="btn-add-row">+ Tambah Soal Lain</button>
                    </div>
                    <div class="modal-footer">
                        <button @click="showModalSoal = false" class="btn-cancel">Tutup</button>
                        <button @click="simpanSoal" class="btn-save">Simpan Semua Soal</button>
                    </div>
                </div>

                <div v-else-if="activeTab === 'nilai'">
                    <div class="table-container">
                        <table class="custom-table">
                            <thead>
                                <tr>
                                    <th width="5%">No</th>
                                    <th>Nama Siswa</th>
                                    <th width="15%">Nilai</th>
                                    <th width="25%">Waktu Mengerjakan</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-if="listNilai.length === 0">
                                    <td colspan="4" class="text-center">Belum ada siswa yang mengerjakan.</td>
                                </tr>
                                <tr v-for="(item, index) in listNilai" :key="index">
                                    <td>{{ index + 1 }}</td>
                                    <td>
                                        <strong>{{ item.siswa ? item.siswa.name : 'Siswa Tanpa Nama' }}</strong>
                                    </td> 
                                    <td>
                                        <span class="badge-nilai" :class="item.skor >= 70 ? 'lulus' : 'gagal'">
                                            {{ item.skor }}
                                        </span>
                                    </td>
                                    <td>{{ item.tanggal_dikerjakan || item.created_at }}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    
                    <div class="modal-footer">
                        <button @click="showModalSoal = false" class="btn-cancel">Tutup</button>
                        <button @click="loadHistoryNilai(activeMateri.id)" class="btn-save">Refresh Data</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</template>

<style scoped>
/* CSS SAMA SEPERTI SEBELUMNYA */
.container { max-width: 1000px; margin: 0 auto; padding: 2rem; font-family: 'Segoe UI', sans-serif; }
.header-wrapper { display: flex; align-items: center; gap: 20px; margin-bottom: 3rem; padding-bottom: 1.5rem; border-bottom: 1px solid #eee; }
.btn-back { width: 45px; height: 45px; border-radius: 50%; background: white; border: 1px solid #dfe6e9; display: flex; justify-content: center; align-items: center; text-decoration: none; transition: all 0.2s; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.btn-back .icon { transform: rotate(180deg); font-size: 1.2rem; color: #636e72; font-weight: bold; }
.btn-back:hover { background: #f1f2f6; transform: translateX(-3px); }
.header-text { flex: 1; }
.header-text .title { margin: 0; color: #2d3436; font-size: 1.8rem; }
.header-text .subtitle { margin: 5px 0 0; color: #636e72; font-size: 0.95rem; }

.btn-add-materi { background: #3498db; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: bold; box-shadow: 0 4px 10px rgba(52, 152, 219, 0.2); transition: 0.2s; }
.btn-add-materi:hover { background: #2980b9; transform: translateY(-2px); }

.timeline-container { position: relative; padding-left: 10px; }
.timeline-line { position: absolute; left: 30px; top: 20px; bottom: 0; width: 3px; background-color: #dfe6e9; transform: translateX(-50%); z-index: 0; }
.timeline-item { display: flex; gap: 25px; position: relative; margin-bottom: 2.5rem; z-index: 1; }
.timeline-marker { width: 40px; height: 40px; background: #3498db; color: white; border-radius: 50%; display: flex; justify-content: center; align-items: center; font-weight: bold; font-size: 1.1rem; flex-shrink: 0; border: 4px solid #f8f9fa; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
.timeline-content { flex: 1; background: white; border-radius: 12px; padding: 1.5rem; box-shadow: 0 4px 15px rgba(0,0,0,0.05); border: 1px solid #f1f2f6; transition: transform 0.2s; }
.timeline-content:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(0,0,0,0.08); }

.content-header { display: flex; justify-content: space-between; margin-bottom: 1rem; flex-wrap: wrap; gap: 10px; }
.content-header h3 { margin: 0; color: #2c3e50; font-size: 1.2rem; }
.date { font-size: 0.8rem; color: #b2bec3; background: #f8f9fa; padding: 2px 8px; border-radius: 4px; height: fit-content; }
.deskripsi { color: #555; line-height: 1.6; margin-bottom: 1.2rem; font-size: 0.95rem; }
.btn-file { background-color: #fff; color: #3498db; border: 1px solid #3498db; padding: 8px 16px; border-radius: 6px; cursor: pointer; font-weight: 600; transition: 0.2s; display: inline-flex; align-items: center; gap: 8px; font-size: 0.9rem; text-decoration: none; }
.btn-file:hover { background-color: #3498db; color: white; }

.divider { border-top: 1px dashed #dfe6e9; margin: 1.5rem 0 1rem 0; }
.admin-controls { background: #f8f9fa; padding: 12px 20px; border-radius: 8px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 15px; border-left: 4px solid #95a5a6; }
.control-info { display: flex; flex-direction: column; }
.control-info strong { color: #2c3e50; font-size: 0.95rem; }
.control-info small { color: #7f8c8d; }
.btn-group { display: flex; gap: 8px; }
.btn-action { padding: 8px 14px; border-radius: 4px; border: none; cursor: pointer; font-size: 0.85rem; font-weight: 600; color: white; transition: 0.2s; }
.btn-soal { background: #3498db; } .btn-soal:hover { background: #2980b9; }
.btn-edit { background: #f1c40f; color: #333; } .btn-edit:hover { background: #f39c12; }
.btn-delete { background: #e74c3c; } .btn-delete:hover { background: #c0392b; }

.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; border-radius: 10px; display: flex; flex-direction: column; box-shadow: 0 10px 25px rgba(0,0,0,0.2); max-height: 90vh; }
.large-modal { width: 800px; } .small-modal { width: 500px; }
.modal-header { padding: 1rem 1.5rem; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; background: #f8f9fa; border-radius: 10px 10px 0 0; }
.close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #aaa; }
.modal-tabs { display: flex; border-bottom: 1px solid #eee; background: #f8f9fa; padding: 0 1.5rem; gap: 1rem; }
.modal-tabs button { background: none; border: none; padding: 10px 0; cursor: pointer; font-weight: 600; color: #777; border-bottom: 3px solid transparent; }
.modal-tabs button.active { color: #3498db; border-bottom-color: #3498db; }
.modal-body { padding: 1.5rem; overflow-y: auto; flex: 1; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 1rem; padding-top: 1rem; border-top: 1px solid #eee; }
.form-group { margin-bottom: 15px; } .form-group label { display: block; margin-bottom: 5px; font-weight: bold; color: #555; }
.form-group input[type="text"], .form-group textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; box-sizing: border-box; }
.btn-save { background: #3498db; color: white; border: none; padding: 10px 20px; border-radius: 6px; font-weight: bold; cursor: pointer; }
.btn-save:hover { background: #2980b9; }
.btn-cancel { background: #95a5a6; color: white; border: none; padding: 10px 20px; border-radius: 6px; font-weight: bold; cursor: pointer; }

.editor-scroll { max-height: 50vh; overflow-y: auto; padding-right: 5px; }
.soal-box { border: 1px solid #e0e0e0; padding: 1rem; border-radius: 8px; margin-bottom: 1rem; background: #fff; }
.box-header { display: flex; justify-content: space-between; margin-bottom: 0.5rem; }
.input-pertanyaan { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; min-height: 60px; margin-bottom: 10px; }
.grid-opsi { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 10px; }
.opsi-row { display: flex; align-items: center; gap: 5px; } .opsi-row input { width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 4px; }
.kunci-row { background: #e8f4fd; padding: 8px; border-radius: 4px; display: flex; align-items: center; gap: 10px; font-size: 0.9rem; border: 1px solid #d6eaf8; }
.btn-add-row { width: 100%; padding: 10px; border: 2px dashed #ddd; background: #fafafa; color: #777; font-weight: bold; cursor: pointer; border-radius: 6px; }
.text-danger { color: #e74c3c; background: none; border: none; cursor: pointer; font-size: 0.85rem; }

/* TABLE STYLES */
.table-container { overflow-x: auto; }
.custom-table { width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 0.95rem; }
.custom-table thead { background-color: #3498db; color: white; }
.custom-table th, .custom-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #ddd; }
.custom-table tbody tr:hover { background-color: #f1f9fe; }
.custom-table tbody tr:nth-child(even) { background-color: #fafafa; }
.text-center { text-align: center !important; color: #7f8c8d; font-style: italic; }

.badge-nilai { padding: 5px 10px; border-radius: 15px; font-weight: bold; font-size: 0.85rem; color: white; }
.badge-nilai.lulus { background-color: #2ecc71; }
.badge-nilai.gagal { background-color: #e74c3c; }

.loading, .empty-state { text-align: center; padding: 4rem; color: #b2bec3; }
.spinner { width: 30px; height: 30px; border: 3px solid #eee; border-top: 3px solid #3498db; border-radius: 50%; margin: 0 auto 10px; animation: spin 1s linear infinite; }
.loading-modal { display: flex; justify-content: center; align-items: center; height: 200px; gap: 10px; color: #3498db; font-weight: bold; }
.spinner-small { width: 20px; height: 20px; border: 3px solid #e1e1e1; border-top: 3px solid #3498db; border-radius: 50%; animation: spin 1s linear infinite; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
</style>