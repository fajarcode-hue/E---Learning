import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'

import AdminDashboard from '../views/admin/Dashboard.vue'
import GuruDashboard from '../views/guru/Dashboard.vue'
import SiswaDashboard from '../views/siswa/Dashboard.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: LoginView
    },
    {
      path: '/guru/dashboard',
      name: 'guru-dashboard',
      component: GuruDashboard
    },
    {
      path: '/guru/mapel/:id',
      name: 'guru-materi',
      component: () => import('../views/guru/MateriView.vue') 
    },
    {
        path: '/siswa/dashboard',
        name: 'siswa-dashboard',
        component: () => import('../views/siswa/Dashboard.vue'),
    },
    {
        path: '/siswa/mapel/:id',
        name: 'siswa-materi',
        component: () => import('../views/siswa/MateriSiswaView.vue'),
    },
    {
        path: '/siswa/latihan/materi/:materi_id',
        name: 'siswa-kerjakan',
        component: () => import('../views/siswa/KerjakanLatihanView.vue'),
    },
    {
        path: '/siswa/history',
        name: 'siswa-history',
        component: () => import('../views/siswa/HistoryLatihanView.vue'),
    },
    // --- ADMIN ROUTES ---
    {
        path: '/admin/dashboard',
        name: 'admin-dashboard',
        component: AdminDashboard
    },
    {
        path: '/admin/guru',
        name: 'admin-guru',
        component: () => import('../views/admin/GuruIndex.vue')
    },
    {
        path: '/admin/siswa',
        name: 'admin-siswa',
        component: () => import('../views/admin/SiswaIndex.vue')
    },
    {
        path: '/admin/mapel',
        name: 'admin-mapel',
        component: () => import('../views/admin/MapelIndex.vue')
    },
    {
        path: '/admin/kelas', 
        name: 'admin-kelas',
        component: () => import('../views/admin/KelasIndex.vue')
    },


  ]
})


router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  
  if (to.name !== 'login' && !token) {
    next({ name: 'login' });
  } else {
    next();
  }
});

export default router