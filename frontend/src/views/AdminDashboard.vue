<template>
  <div class="app-shell">
    <AdminSidebar
      v-model="sidebarCollapsed"
      :currentView="currentView"
      :navItems="navItems"
      :adminName="adminName"
      :isDark="isDark"
      @navigate="currentView = $event"
      @logout="logout"
      @toggleTheme="toggleTheme"
    />

    <div class="main-content">
      <AdminTopbar
        v-model="searchQuery"
        :todayDate="formattedDate"
        :adminName="adminName"
      />

      <DashboardView
        v-if="currentView === 'dashboard'"
        :searchedItems="searchedItems"
        :requests="requests"
        :categoryBreakdown="categoryBreakdown"
        :totalItems="items.length"
        :availableCount="availableCount"
        :pendingCount="pendingCount"
        :repairCount="repairCount"
        @openAddItem="openAddModal"
        @editItem="editItem"
        @deleteItem="deleteItem"
        @navigate="currentView = $event"
      />

      <InventoryView
        v-if="currentView === 'inventory'"
        :filteredItems="filteredItems"
        :filterCategory="filterCategory"
        :filterStatus="filterStatus"
        @update:filterCategory="filterCategory = $event"
        @update:filterStatus="filterStatus = $event"
        @openAddItem="openAddModal"
        @editItem="editItem"
        @deleteItem="deleteItem"
      />

      <RequestsView
        v-if="currentView === 'requests'"
        :filteredRequests="filteredRequests"
        :reqFilter="reqFilter"
        @update:reqFilter="reqFilter = $event"
        @approveReq="updateReqStatus($event, 'approved')"
        @rejectReq="updateReqStatus($event, 'rejected')"
      />

      <UsersView
        v-if="currentView === 'users'"
        :users="users"
        @openAddUser="showAddUserModal = true"
        @deleteUser="deleteUser"
        @changeRole="updateUserRole"
      />

      <SettingsView
        v-if="currentView === 'settings'"
        :settings="settings"
      />
    </div>

    <AddItemModal
      v-model="showAddModal"
      :editingItem="editingItemObj"
      @save="saveItem"
    />

    <AddUserModal
      v-model="showAddUserModal"
      @save="saveUser"
    />
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

// Импорт на компоненти
import AdminSidebar  from '@/components/admin/AdminSidebar.vue'
import AdminTopbar   from '@/components/admin/AdminTopbar.vue'
import DashboardView from '@/components/admin/DashboardView.vue'
import InventoryView from '@/components/admin/InventoryView.vue'
import RequestsView  from '@/components/admin/RequestsView.vue'
import UsersView     from '@/components/admin/UsersView.vue'
import SettingsView  from '@/components/admin/SettingsView.vue'
import AddItemModal  from '@/components/admin/AddItemModal.vue'
import AddUserModal  from '@/components/admin/AddUserModal.vue'

// Инициализация на инструменти
const router = useRouter()
const { locale } = useI18n()
const token = localStorage.getItem('invtr_token') || sessionStorage.getItem('invtr_token')
const authHeaders = () => ({ 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' })

// ── Локализация на датата ──
const formattedDate = computed(() => {
  return new Date().toLocaleDateString(locale.value, {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  })
})

// ── Тема (Dark/Light) ──
const isDark = ref(localStorage.getItem('invtr_theme') === 'dark')

function applyTheme(dark) {
  if (dark) document.documentElement.classList.add('dark')
  else document.documentElement.classList.remove('dark')
  localStorage.setItem('invtr_theme', dark ? 'dark' : 'light')
}
applyTheme(isDark.value)

function toggleTheme() {
  document.documentElement.classList.add('theme-transitioning')
  isDark.value = !isDark.value
  applyTheme(isDark.value)
  // Синхронизация с обекта settings, ако съществува
  const darkSetting = settings.system.find(s => s.key === 'dark')
  if (darkSetting) darkSetting.value = isDark.value
  setTimeout(() => document.documentElement.classList.remove('theme-transitioning'), 400)
}

// ── Потребителска сесия ──
const adminName = ref('')
let adminEmail = ''
try {
  const payload = JSON.parse(atob(token.split('.')[1]))
  adminEmail = payload.sub || ''
} catch (_) {}

function logout() {
  localStorage.removeItem('invtr_token')
  sessionStorage.removeItem('invtr_token')
  router.push('/login')
}

// ── Навигация и Търсене ──
const currentView = ref('dashboard')
const sidebarCollapsed = ref(false)
const searchQuery = ref('')

const navItems = [
  { view: 'dashboard', icon: '📊' },
  { view: 'inventory', icon: '📦' },
  { view: 'requests',  icon: '📋' },
  { view: 'users',     icon: '👥' },
  { view: 'settings',  icon: '⚙️' },
]

// ── Мапинги за базата данни (Enum Mapping) ──
const TYPE_MAP = { ELECTRICAL: 'Electronics', FURNITURE: 'Furniture', UTILITY: 'Utility' }
const TYPE_ENUM = { Electronics: 'ELECTRICAL', Furniture: 'FURNITURE', Utility: 'UTILITY' }
const STATUS_MAP = { AVAILABLE: 'Available', CHECKED_OUT: 'Checked-Out', UNDER_REPAIR: 'Under-Repair', RETIRED: 'Retired' }
const STATUS_ENUM = { Available: 'AVAILABLE', 'Checked-Out': 'CHECKED_OUT', 'Under-Repair': 'UNDER_REPAIR', Retired: 'RETIRED' }
const COND_MAP = { EXCELLENT: 'excellent', GOOD: 'good', DAMAGED: 'damaged', BROKEN: 'broken' }
const COND_ENUM = { excellent: 'EXCELLENT', good: 'GOOD', damaged: 'DAMAGED', broken: 'BROKEN' }
const EMOJI_MAP = { Electronics: '💻', Furniture: '🪑', Utility: '🔧' }

// ── Данни (Data) ──
const items = ref([])
const users = ref([])
const rawRequests = ref([])

// Списък със заявки с обогатени данни (имена на потребители и предмети)
const requests = computed(() =>
  rawRequests.value.map(r => {
    const user = users.value.find(u => u.id === r.userId)
    const itemNames = (r.equipmentIds || []).map(id => {
      const item = items.value.find(i => i.id === id)
      return item ? item.name : `Item #${id}`
    }).join(', ')
    return {
      id: r.id,
      user: user ? user.name : `User #${r.userId}`,
      item: itemNames,
      requested: r.requestDate || '',
      returnBy: r.endDateTime ? r.endDateTime.split('T')[0] : '',
      status: (r.status || '').toLowerCase(),
    }
  })
)

// ── API заявки ──
const loadItems = async () => {
  try {
    const res = await fetch('/equipment', { headers: authHeaders() })
    if (res.ok) {
      const data = await res.json()
      items.value = data.map(e => ({
        id: e.id,
        name: e.name,
        emoji: EMOJI_MAP[TYPE_MAP[e.type]] || '📦',
        category: TYPE_MAP[e.type] || e.type,
        serial: e.serialNumber || '',
        status: STATUS_MAP[e.status] || e.status,
        condition: COND_MAP[e.condition] || (e.condition || '').toLowerCase(),
        location: e.location || '',
        photoUrl: e.photoUrl || '',
      }))
    }
  } catch (_) {}
}

const loadUsers = async () => {
  try {
    const res = await fetch('/auth/users', { headers: authHeaders() })
    if (res.ok) {
      const data = await res.json()
      users.value = data.map(u => ({
        id: u.id,
        name: `${u.firstName} ${u.familyName}`.trim(),
        email: u.email,
        role: u.roleName || 'USER',
        borrows: 0,
        active: true,
      }))
      const me = data.find(u => u.email === adminEmail)
      if (me) adminName.value = `${me.firstName} ${me.familyName}`.trim()
    }
  } catch (_) {}
}

const loadRequests = async () => {
  try {
    const res = await fetch('/requests/manager', { headers: authHeaders() })
    if (res.ok) {
      rawRequests.value = await res.json()
    }
  } catch (_) {}
}

onMounted(async () => {
  await Promise.all([loadItems(), loadUsers()])
  await loadRequests()
})

// ── Computed статистики за Dashboard ──
const availableCount = computed(() => items.value.filter(i => i.status === 'Available').length)
const pendingCount = computed(() => requests.value.filter(r => r.status === 'pending').length)
const repairCount = computed(() => items.value.filter(i => i.status === 'Under-Repair').length)
const categoryBreakdown = computed(() => items.value.reduce((acc, item) => {
  acc[item.category] = (acc[item.category] || 0) + 1
  return acc
}, {}))

const searchedItems = computed(() =>
  items.value.filter(i =>
    i.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    i.category.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    i.serial.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
)

// Филтри за Inventory
const filterCategory = ref('')
const filterStatus = ref('')
const filteredItems = computed(() =>
  items.value.filter(i =>
    (!filterCategory.value || i.category === filterCategory.value) &&
    (!filterStatus.value || i.status === filterStatus.value)
  )
)

const reqFilter = ref('all')
const filteredRequests = computed(() =>
  reqFilter.value === 'all' ? requests.value : requests.value.filter(r => r.status === reqFilter.value)
)

// ── Управление на състоянието (Логика) ──
async function updateReqStatus(id, status) {
  const endpoint = status === 'approved' ? `/requests/${id}/approve` : `/requests/${id}/reject`
  try {
    const res = await fetch(endpoint, { method: 'PUT', headers: authHeaders() })
    if (res.ok) {
      const updated = await res.json()
      const raw = rawRequests.value.find(r => r.id === id)
      if (raw) raw.status = updated.status
    }
  } catch (_) {}
}

// Settings реактивен обект
const settings = reactive({
  notifications: [
    { key: 'email', label: 'Email Notifications', sub: 'Send emails on new requests', value: true },
    { key: 'overdue', label: 'Overdue Alerts', sub: 'Notify when items are overdue', value: true },
    { key: 'repair', label: 'Repair Reminders', sub: 'Remind about items under repair', value: false },
  ],
  system: [
    { key: 'dark', label: 'Dark Mode', sub: 'Switch interface theme', value: isDark.value },
    { key: 'auto', label: 'Auto-approve Requests', sub: 'Skip manual approval step', value: false },
    { key: 'maint', label: 'Maintenance Mode', sub: 'Disable user access temporarily', value: false },
  ],
  profile: { name: 'Admin User', email: 'admin@equipro.com', password: '' },
  policy: { maxDays: 14, maxItems: 3, lateFee: '$2.00' },
})

const showAddModal = ref(false)
const showAddUserModal = ref(false)
const editingItemObj = ref(null)

function openAddModal() {
  editingItemObj.value = null
  showAddModal.value = true
}

function editItem(item) {
  editingItemObj.value = { ...item }
  showAddModal.value = true
}

async function saveItem(form) {
  if (editingItemObj.value) {
    const body = {
      type: TYPE_ENUM[form.category] || 'ELECTRICAL',
      condition: COND_ENUM[form.condition] || 'GOOD',
      location: form.location || 'Unknown',
      status: STATUS_ENUM[form.status] || 'AVAILABLE',
    }
    try {
      const res = await fetch(`/equipment/${editingItemObj.value.id}`, {
        method: 'PATCH', headers: authHeaders(), body: JSON.stringify(body),
      })
      if (res.ok) {
        await loadItems() // Презареждаме за най-сигурно
      }
    } catch (_) {}
  } else {
    const body = {
      type: TYPE_ENUM[form.category] || 'ELECTRICAL',
      name: form.name,
      serialNumber: form.serial,
      status: STATUS_ENUM[form.status] || 'AVAILABLE',
      condition: COND_ENUM[form.condition] || 'GOOD',
      location: form.location || 'Unknown',
      photoUrl: form.photoUrl || null,
    }
    try {
      const res = await fetch('/equipment', {
        method: 'POST', headers: authHeaders(), body: JSON.stringify(body),
      })
      if (res.ok || res.status === 201) {
        await loadItems()
      }
    } catch (_) {}
  }
}

async function deleteItem(id) {
  if (confirm('Delete this item?')) {
    try {
      const res = await fetch(`/equipment/${id}`, { method: 'DELETE', headers: authHeaders() })
      if (res.ok) {
        items.value = items.value.filter(i => i.id !== id)
      }
    } catch (_) {}
  }
}

async function saveUser(form) {
  const parts = form.name.trim().split(' ')
  const firstName = parts[0] || form.name
  const familyName = parts.slice(1).join(' ') || parts[0]
  try {
    const res = await fetch('/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: form.email, password: 'Invtr@1234', firstName, familyName }),
    })
    if (res.ok) {
      await loadUsers()
    }
  } catch (_) {}
}

function deleteUser(id) {
  if (confirm('Remove this user?')) {
    users.value = users.value.filter(u => u.id !== id)
  }
}

async function updateUserRole({ userId, role }) {
  try {
    const res = await fetch(`/auth/users/${userId}`, {
      method: 'PATCH',
      headers: authHeaders(),
      body: JSON.stringify({ role: role }),
    })
    if (res.ok) {
      const updated = await res.json()
      const u = users.value.find(u => u.id === userId)
      if (u) u.role = updated.roleName || role
    }
  } catch (_) {}
}

// Синхронизация на Dark Mode от настройките
watch(() => settings.system.find(s => s.key === 'dark')?.value, (val) => {
  if (val !== undefined && val !== isDark.value) {
    isDark.value = val
    applyTheme(val)
  }
})
</script>

<style>
@import '@/assets/dashboard.css';
</style>