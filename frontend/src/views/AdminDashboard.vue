<template>
  <div class="app-shell">

    <AdminSidebar
      v-model="sidebarCollapsed"
      :currentView="currentView"
      :navItems="navItems"
      @navigate="currentView = $event"
      @logout="logout"
    />
    <div class="main-content">

      <AdminTopbar
        v-model="searchQuery"
        :todayDate="todayDate"
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
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'

import AdminSidebar  from '@/components/admin/AdminSidebar.vue'
import AdminTopbar   from '@/components/admin/AdminTopbar.vue'
import DashboardView from '@/components/admin/DashboardView.vue'
import InventoryView from '@/components/admin/InventoryView.vue'
import RequestsView  from '@/components/admin/RequestsView.vue'
import UsersView     from '@/components/admin/UsersView.vue'
import SettingsView  from '@/components/admin/SettingsView.vue'
import AddItemModal  from '@/components/admin/AddItemModal.vue'
import AddUserModal  from '@/components/admin/AddUserModal.vue'

const router = useRouter()
const token = localStorage.getItem('invtr_token') || sessionStorage.getItem('invtr_token')
const authHeaders = () => ({ 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' })

function logout() {
  localStorage.removeItem('invtr_token')
  sessionStorage.removeItem('invtr_token')
  router.push('/login')
}

const currentView      = ref('dashboard')
const sidebarCollapsed = ref(false)
const searchQuery      = ref('')

const navItems = [
  { view: 'dashboard', icon: '📊', label: 'Dashboard' },
  { view: 'inventory', icon: '📦', label: 'Inventory' },
  { view: 'requests',  icon: '📋', label: 'Requests'  },
  { view: 'users',     icon: '👥', label: 'Users'     },
  { view: 'settings',  icon: '⚙️',  label: 'Settings'  },
]

const todayDate = new Date().toLocaleDateString('en-US', {
  weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
})

// ── Enum mapping helpers ──
const TYPE_MAP    = { ELECTRICAL: 'Electronics', FURNITURE: 'Furniture', UTILITY: 'Tools' }
const TYPE_ENUM   = { Electronics: 'ELECTRICAL', Furniture: 'FURNITURE', Tools: 'UTILITY', Utility: 'UTILITY' }
const STATUS_MAP  = { AVAILABLE: 'Available', CHECKED_OUT: 'Checked-Out', UNDER_REPAIR: 'Under-Repair', RETIRED: 'Retired' }
const STATUS_ENUM = { Available: 'AVAILABLE', 'Checked-Out': 'CHECKED_OUT', 'Under-Repair': 'UNDER_REPAIR', Retired: 'RETIRED' }
const COND_MAP    = { EXCELLENT: 'excellent', GOOD: 'good', DAMAGED: 'damaged', BROKEN: 'broken' }
const COND_ENUM   = { excellent: 'EXCELLENT', good: 'GOOD', damaged: 'DAMAGED', broken: 'BROKEN', fair: 'GOOD', poor: 'DAMAGED' }
const EMOJI_MAP   = { Electronics: '💻', Furniture: '🪑', Tools: '🔧' }

// ── Data ──
const items       = ref([])
const users       = ref([])
const rawRequests = ref([])

// Enrich raw requests with user names and equipment names reactively
const requests = computed(() =>
  rawRequests.value.map(r => {
    const user = users.value.find(u => u.id === r.userId)
    const itemNames = (r.equipmentIds || []).map(id => {
      const item = items.value.find(i => i.id === id)
      return item ? item.name : `Item #${id}`
    }).join(', ')
    return {
      id:        r.id,
      user:      user ? user.name : `User #${r.userId}`,
      item:      itemNames,
      requested: r.requestDate || '',
      returnBy:  r.endDateTime ? r.endDateTime.split('T')[0] : '',
      status:    (r.status || '').toLowerCase(),
    }
  })
)

// ── API calls ──
const loadItems = async () => {
  try {
    const res = await fetch('/equipment', { headers: authHeaders() })
    if (res.ok) {
      const data = await res.json()
      items.value = data.map(e => ({
        id:        e.id,
        name:      e.name,
        emoji:     EMOJI_MAP[TYPE_MAP[e.type]] || '📦',
        category:  TYPE_MAP[e.type] || e.type,
        serial:    e.serialNumber || '',
        status:    STATUS_MAP[e.status] || e.status,
        condition: COND_MAP[e.condition] || (e.condition || '').toLowerCase(),
        location:  e.location || '',
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
        id:      u.id,
        name:    `${u.firstName} ${u.familyName}`,
        email:   u.email,
        role:    u.roleName || 'USER',
        borrows: 0,
        active:  true,
      }))
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

// ── Computed ──
const availableCount    = computed(() => items.value.filter(i => i.status === 'Available').length)
const pendingCount      = computed(() => requests.value.filter(r => r.status === 'pending').length)
const repairCount       = computed(() => items.value.filter(i => i.status === 'Under-Repair').length)
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

const filterCategory = ref('')
const filterStatus   = ref('')
const filteredItems  = computed(() =>
  items.value.filter(i =>
    (!filterCategory.value || i.category === filterCategory.value) &&
    (!filterStatus.value   || i.status   === filterStatus.value)
  )
)

const reqFilter = ref('all')
const filteredRequests = computed(() =>
  reqFilter.value === 'all'
    ? requests.value
    : requests.value.filter(r => r.status === reqFilter.value)
)

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

const settings = reactive({
  notifications: [
    { key: 'email',   label: 'Email Notifications', sub: 'Send emails on new requests',     value: true  },
    { key: 'overdue', label: 'Overdue Alerts',       sub: 'Notify when items are overdue',   value: true  },
    { key: 'repair',  label: 'Repair Reminders',     sub: 'Remind about items under repair', value: false },
  ],
  system: [
    { key: 'dark',  label: 'Dark Mode',             sub: 'Switch interface theme',           value: false },
    { key: 'auto',  label: 'Auto-approve Requests', sub: 'Skip manual approval step',        value: false },
    { key: 'maint', label: 'Maintenance Mode',      sub: 'Disable user access temporarily',  value: false },
  ],
  profile: { name: 'Admin User', email: 'admin@equipro.com', password: '' },
  policy:  { maxDays: 14, maxItems: 3, lateFee: '$2.00' },
})

const showAddModal    = ref(false)
const showAddUserModal = ref(false)
const editingItemObj  = ref(null)

function openAddModal() {
  editingItemObj.value = null
  showAddModal.value   = true
}

function editItem(item) {
  editingItemObj.value = { ...item }
  showAddModal.value   = true
}

async function saveItem(form) {
  if (editingItemObj.value) {
    // PATCH — name cannot be changed (backend limitation)
    const body = {
      type:      TYPE_ENUM[form.category] || 'ELECTRICAL',
      condition: COND_ENUM[form.condition] || 'GOOD',
      location:  form.location || 'Unknown',
      status:    STATUS_ENUM[form.status] || 'AVAILABLE',
    }
    try {
      const res = await fetch(`/equipment/${editingItemObj.value.id}`, {
        method: 'PATCH', headers: authHeaders(), body: JSON.stringify(body),
      })
      if (res.ok) {
        const updated = await res.json()
        const idx = items.value.findIndex(i => i.id === editingItemObj.value.id)
        if (idx !== -1) {
          items.value[idx] = {
            ...items.value[idx],
            category:  TYPE_MAP[updated.type]      || items.value[idx].category,
            status:    STATUS_MAP[updated.status]   || items.value[idx].status,
            condition: COND_MAP[updated.condition]  || items.value[idx].condition,
            location:  updated.location             || items.value[idx].location,
          }
        }
      }
    } catch (_) {}
  } else {
    // POST — create new item
    const body = {
      type:         TYPE_ENUM[form.category] || 'ELECTRICAL',
      name:         form.name,
      serialNumber: form.serial,
      status:       STATUS_ENUM[form.status] || 'AVAILABLE',
      condition:    COND_ENUM[form.condition] || 'GOOD',
      location:     form.location || 'Unknown',
    }
    try {
      const res = await fetch('/equipment', {
        method: 'POST', headers: authHeaders(), body: JSON.stringify(body),
      })
      if (res.ok || res.status === 201) {
        const created = await res.json()
        items.value.push({
          id:        created.id,
          name:      created.name,
          emoji:     EMOJI_MAP[TYPE_MAP[created.type]] || '📦',
          category:  TYPE_MAP[created.type] || created.type,
          serial:    created.serialNumber || '',
          status:    STATUS_MAP[created.status] || created.status,
          condition: COND_MAP[created.condition] || (created.condition || '').toLowerCase(),
          location:  created.location || '',
        })
      }
    } catch (_) {}
  }
  editingItemObj.value = null
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
  const firstName  = parts[0] || form.name
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
</script>

<style>
@import '@/assets/dashboard.css';
</style>