import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import UserDashboard from '@/views/UserDashboard.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'

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
    meta: { requiresAuth: true, role: 'ADMIN'}
  }
]

/**
 * Resolves the user's role from a decoded JWT payload.
 * Handles all common Spring Boot patterns:
 *   1. payload.role    = 'ADMIN'         (string)        ← your backend uses this
 *   2. payload.roleId  = 2               (camelCase int)
 *   3. payload.role_id = 2               (snake_case int)
 *   4. payload.roles   = ['ROLE_ADMIN']  (Spring Security list)
 */
function getRoleFromPayload(payload) {
  if (payload.role) return String(payload.role).toUpperCase()
  const numericId = payload.roleId !== undefined ? payload.roleId
                  : payload.role_id !== undefined ? payload.role_id
                  : null
  if (numericId !== null) return Number(numericId) === 2 ? 'ADMIN' : 'USER'
  if (Array.isArray(payload.roles)) {
    return payload.roles.some(r => String(r).toUpperCase().includes('ADMIN')) ? 'ADMIN' : 'USER'
  }
  return 'USER'
}

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('invtr_token') || sessionStorage.getItem('invtr_token')
  if (to.meta.requiresAuth && !token) return '/login'

  if (to.meta.requiresAuth && token) {
    const payload = JSON.parse(atob(token.split('.')[1]))
    const role = getRoleFromPayload(payload)
    if (to.meta.role === 'ADMIN' && role !== 'ADMIN') return '/dashboard'
    if (to.meta.role === 'USER' && role === 'ADMIN') return '/admin-dashboard'
  }
})



export default router

