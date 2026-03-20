import { createRouter, createWebHistory } from 'vue-router'

import Login          from '@/views/Login.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, role: 'admin' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const requiresAuth = to.meta.requiresAuth
  const token = localStorage.getItem('token')
  const role  = localStorage.getItem('role')

  if (requiresAuth && !token) {
    next('/login')
  } else if (requiresAuth && to.meta.role && to.meta.role !== role) {
    next('/login')
  } else {
    next()
  }
})

export default router