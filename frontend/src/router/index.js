import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import UserDashboard from '@/views/UserDashboard.vue'
import AdminDashboard from '@/views/AdminDashbord.vue'  // note the typo in your filename

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: Login },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: UserDashboard,
    meta: { requiresAuth: true, role: 'USER' }
  },
  {
    path: '/admin-dashboard',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, role: 'ADMIN' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('invtr_token') || sessionStorage.getItem('invtr_token')
  if (to.meta.requiresAuth && !token) return '/login'

  if (to.meta.role === 'ADMIN' && token) {
    const payload = JSON.parse(atob(token.split('.')[1]))
    if (payload.role !== 'ADMIN') return '/dashboard'
  }
})

export default router