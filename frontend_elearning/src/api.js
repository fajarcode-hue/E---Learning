// src/api.js
import axios from 'axios';

const api = axios.create({
    // Ganti URL ini jika nanti upload ke hosting
    baseURL: 'http://172.27.204.185:8000/api/',
});

// INTERCEPTOR: "Satpam" yang menyisipkan Token di setiap request
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default api;