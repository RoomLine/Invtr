import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import UserDashboard from '@/views/UserDashboard.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: Login },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: UserDashboard,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('invtr_token') || sessionStorage.getItem('invtr_token')
  if (to.meta.requiresAuth && !token) return '/login'
})

export default router