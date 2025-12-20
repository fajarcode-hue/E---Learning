<script setup>
import { ref, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../../api';

const route = useRoute();
const router = useRouter();

// PERBAIKAN 1: Sesuaikan dengan router index.js (:id)
const materiId = route.params.materi_id; 

const isLoading = ref(true);
const soalList = ref([]);
const latihanId = ref(null); 
const judulMateri = ref('Latihan Soal'); 

// State jawaban
const jawabanSiswa = reactive({});

// 1. Ambil Soal
const fetchSoal = async () => {
    isLoading.value = true;
    try {
        // Panggil API
        const response = await api.get(`/siswa/latihan/materi/${materiId}`);
        
        const data = response.data.data;
        
        latihanId.value = data.id;      
        soalList.value = data.soal;     
        
        // Reset jawaban saat load
        Object.keys(jawabanSiswa).forEach(key => delete jawabanSiswa[key]);

    } catch (error) {
        console.error(error);
        alert(error.response?.data?.message || "Gagal memuat soal.");
        router.push('/siswa/dashboard'); 
    } finally {
        isLoading.value = false;
    }
};

// 2. Submit Jawaban
const submitJawaban = async () => {
    // Validasi sederhana
    if (Object.keys(jawabanSiswa).length < soalList.value.length) {
        if(!confirm("Masih ada soal yang belum dijawab. Yakin ingin mengumpulkan?")) return;
    } else {
        if(!confirm("Yakin ingin mengumpulkan jawaban?")) return;
    }

    try {
        const payload = {
            latihan_id: latihanId.value,
            jawaban_siswa: jawabanSiswa 
        };

        const response = await api.post('/siswa/latihan/submit', payload);

        const nilai = response.data.nilai;
        const benar = response.data.benar;
        const total = response.data.total_soal;

        alert(`Latihan Selesai!\n\nNilai Kamu: ${nilai}\nBenar: ${benar} dari ${total} soal.`);
        
        router.push('/siswa/dashboard');

    } catch (error) {
        console.error(error);
        alert("Terjadi kesalahan saat mengirim jawaban.");
    }
};

onMounted(() => {
    fetchSoal();
});
</script>

<template>
  <div class="scroll-wrapper">
      <div class="exam-container">
        
        <div class="exam-header">
            <h2>📝 Pengerjaan Latihan</h2>
            <router-link to="/siswa/dashboard" class="btn-quit">Batal</router-link>
        </div>

        <div v-if="isLoading" class="loading">
            <div class="spinner"></div>
            <p>Sedang menyiapkan soal...</p>
        </div>
        
        <div v-else class="soal-wrapper">
            <div v-for="(item, index) in soalList" :key="item.id" class="soal-card">
                <div class="soal-number">Pertanyaan {{ index + 1 }}</div>
                
                <p class="pertanyaan-text">{{ item.pertanyaan }}</p>
                
                <div class="opsi-list">
                    <label 
                        v-for="(text, key) in item.pilihan" 
                        :key="key" 
                        class="opsi-item" 
                        :class="{ selected: jawabanSiswa[item.id] === key }"
                    >
                        <input 
                            type="radio" 
                            :name="'soal-' + item.id" 
                            :value="key" 
                            v-model="jawabanSiswa[item.id]"
                        >
                        
                        <span class="huruf-badge">{{ key }}</span>
                        <span class="teks">{{ text }}</span>
                    </label>
                </div>
            </div>

            <div class="action-footer">
                <button @click="submitJawaban" class="btn-submit">
                    ✅ Kirim Jawaban
                </button>
            </div>
        </div>
      </div>
  </div>
</template>

<style scoped>
/* --- PERBAIKAN SCROLL --- */
/* Wrapper luar dipaksa setinggi layar dan boleh scroll sendiri */
.scroll-wrapper {
    height: 100vh;
    width: 100%;
    overflow-y: auto; /* KUNCI AGAR BISA SCROLL */
    background-color: #f8f9fa;
    position: fixed; /* Opsional: Memastikan dia menimpa layout parent jika perlu */
    top: 0;
    left: 0;
    z-index: 10; /* Pastikan di atas layer lain */
}

/* Container Tengah */
.exam-container { 
    max-width: 800px; 
    margin: 0 auto; 
    padding: 2rem; 
    padding-bottom: 150px; /* Tambahan padding bawah agar tombol tidak ketutup */
    font-family: 'Segoe UI', sans-serif; 
}

/* Header */
.exam-header { 
    display: flex; 
    justify-content: space-between; 
    align-items: center; 
    background: white; 
    padding: 1rem 2rem; 
    border-radius: 12px; 
    box-shadow: 0 4px 6px rgba(0,0,0,0.05); 
    margin-bottom: 2rem; 
    /* Sticky Header agar tetap terlihat saat scroll */
    position: sticky; 
    top: 0; 
    z-index: 100; 
}

.exam-header h2 { margin: 0; font-size: 1.4rem; color: #2c3e50; }
.btn-quit { color: #e74c3c; text-decoration: none; font-weight: bold; border: 2px solid #e74c3c; padding: 6px 16px; border-radius: 20px; transition: 0.2s; font-size: 0.9rem; }
.btn-quit:hover { background: #e74c3c; color: white; }

/* Soal Card */
.soal-card { background: white; padding: 2rem; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); margin-bottom: 2rem; border-left: 5px solid #3498db; }
.soal-number { font-weight: bold; color: #3498db; margin-bottom: 1rem; text-transform: uppercase; letter-spacing: 1px; font-size: 0.85rem; }
.pertanyaan-text { font-size: 1.15rem; color: #2d3436; margin-bottom: 1.5rem; line-height: 1.6; }

/* Pilihan Jawaban */
.opsi-list { display: flex; flex-direction: column; gap: 12px; }
.opsi-item { display: flex; align-items: center; gap: 15px; padding: 12px 15px; border: 2px solid #f1f2f6; border-radius: 8px; cursor: pointer; transition: all 0.2s ease; position: relative; }
.opsi-item:hover { background-color: #f8f9fa; border-color: #d1d8e0; }
.opsi-item.selected { background-color: #e3f2fd; border-color: #2196f3; box-shadow: 0 0 0 1px #2196f3; }

.opsi-item input[type="radio"] { position: absolute; opacity: 0; cursor: pointer; }

/* Custom Badge Huruf (A, B, C, D) */
.huruf-badge { width: 30px; height: 30px; background: #dfe6e9; color: #636e72; border-radius: 50%; display: flex; justify-content: center; align-items: center; font-weight: bold; font-size: 0.9rem; transition: 0.2s; flex-shrink: 0; }
.opsi-item.selected .huruf-badge { background: #2196f3; color: white; }

.teks { font-size: 1rem; color: #444; }

/* Footer & Button */
.action-footer { margin-top: 3rem; text-align: center; }
.btn-submit { background: linear-gradient(135deg, #27ae60, #2ecc71); color: white; border: none; padding: 16px 40px; border-radius: 50px; font-size: 1.1rem; font-weight: bold; cursor: pointer; box-shadow: 0 4px 15px rgba(46, 204, 113, 0.4); transition: transform 0.2s, box-shadow 0.2s; }
.btn-submit:hover { transform: translateY(-3px); box-shadow: 0 6px 20px rgba(46, 204, 113, 0.5); }
.btn-submit:active { transform: translateY(1px); }

/* Loading */
.loading { text-align: center; padding: 4rem; color: #b2bec3; }
.spinner { width: 40px; height: 40px; border: 4px solid #f3f3f3; border-top: 4px solid #3498db; border-radius: 50%; animation: spin 1s linear infinite; margin: 0 auto 1rem; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
</style>