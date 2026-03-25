<template>
  <div class="app-shell">

    <!-- ── SIDEBAR ── -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <img src="@/assets/logo2.png" alt="INVTR" class="sidebar-logo" />
        <button class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          <span>{{ sidebarCollapsed ? '→' : '←' }}</span>
        </button>
      </div>

      <nav class="sidebar-nav">
        <button
          v-for="item in navItems"
          :key="item.id"
          class="nav-item"
          :class="{ active: activeView === item.id }"
          @click="activeView = item.id"
        >
          <span class="nav-icon">{{ item.icon }}</span>
          <span class="nav-label">{{ item.label }}</span>
        </button>
      </nav>

      <div class="sidebar-footer">
        <div class="user-chip">
          <div class="user-avatar">{{ userInitials }}</div>
          <div class="user-info">
            <span class="user-name">{{ userName }}</span>
            <span class="user-role">User</span>
          </div>
        </div>
        <button class="theme-toggle-btn" @click="toggleTheme" :title="isDark ? 'Light mode' : 'Dark mode'">
          {{ isDark ? '☀️' : '🌙' }}
        </button>
        <button class="logout-btn" @click="handleLogout" title="Log out">⏻</button>
      </div>
    </aside>

    <!-- ── MAIN CONTENT ── -->
    <main class="main-content">

      <header class="topbar">
        <div class="topbar-left">
          <h1 class="page-title">{{ currentPageTitle }}</h1>
        </div>
        <div class="topbar-right">
          <div class="search-bar" v-if="activeView === 'inventory'">
            <span class="search-icon">🔍</span>
            <input v-model="searchQuery" placeholder="Search equipment..." class="search-input" />
          </div>
          <div class="topbar-date">{{ todayDate }}</div>
        </div>
      </header>

      <!-- ── OVERVIEW ── -->
      <section v-if="activeView === 'overview'" class="view-section">
        <div class="stats-grid">
          <div class="stat-card stat-blue">
            <div class="stat-icon">📦</div>
            <div class="stat-body">
              <span class="stat-value">{{ stats.available }}</span>
              <span class="stat-label">Available Items</span>
            </div>
          </div>
          <div class="stat-card stat-orange">
            <div class="stat-icon">⏳</div>
            <div class="stat-body">
              <span class="stat-value">{{ stats.currentRequests }}</span>
              <span class="stat-label">Current Requests</span>
            </div>
          </div>
          <div class="stat-card stat-green">
            <div class="stat-icon">✅</div>
            <div class="stat-body">
              <span class="stat-value">{{ stats.active }}</span>
              <span class="stat-label">Active Borrows</span>
            </div>
          </div>
          <div class="stat-card stat-gray">
            <div class="stat-icon">📋</div>
            <div class="stat-body">
              <span class="stat-value">{{ stats.total }}</span>
              <span class="stat-label">Total Requests (All Time)</span>
            </div>
          </div>
        </div>

        <div class="overview-bottom">
          <div class="panel">
            <div class="panel-header">
              <h3>My Active Borrows</h3>
              <button class="link-btn" @click="activeView = 'history'">View all →</button>
            </div>
            <div v-if="activeBorrows.length === 0" class="empty-state">No active borrows</div>
            <div v-else class="borrow-list">
              <div v-for="b in activeBorrows" :key="b.id" class="borrow-row">
                <div class="borrow-info">
                  <span class="borrow-name">{{ b.itemName }}</span>
                  <span class="borrow-date">Borrowed: {{ b.borrowedDate }}</span>
                </div>
                <span :class="['status-badge', 'status-' + b.status]">{{ b.status }}</span>
              </div>
            </div>
          </div>

          <div class="panel">
            <div class="panel-header">
              <h3>Recent Requests</h3>
              <button class="link-btn" @click="activeView = 'requests'">View all →</button>
            </div>
            <div v-if="recentRequests.length === 0" class="empty-state">No recent requests</div>
            <div v-else class="borrow-list">
              <div v-for="r in recentRequests" :key="r.id" class="borrow-row">
                <div class="borrow-info">
                  <span class="borrow-name">{{ r.itemName }}</span>
                  <span class="borrow-date">{{ r.requestDate }}</span>
                </div>
                <span :class="['status-badge', 'status-' + r.status]">{{ r.status }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ── INVENTORY ── -->
      <section v-if="activeView === 'inventory'" class="view-section">
        <div class="filter-bar">
          <select v-model="filterType" class="filter-select">
            <option value="">All Types</option>
            <option v-for="t in equipmentTypes" :key="t" :value="t">{{ t }}</option>
          </select>
          <select v-model="filterStatus" class="filter-select">
            <option value="">All Statuses</option>
            <option value="Available">Available</option>
            <option value="Checked Out">Checked Out</option>
            <option value="Under Repair">Under Repair</option>
            <option value="Retired">Retired</option>
          </select>
        </div>

        <div v-if="equipmentLoading" class="empty-state-full">Loading equipment...</div>
        <div v-else-if="equipmentError" class="empty-state-full" style="color:#e74c3c">{{ equipmentError }}</div>
        <div v-else-if="filteredEquipment.length === 0" class="empty-state-full">No equipment found.</div>
        <div v-else class="equipment-grid">
          <div
            v-for="item in filteredEquipment"
            :key="item.id"
            class="equip-card"
            :class="{ 'equip-card-selected': selectedItemIds.has(item.id) }"
          >
            <!-- Photo if available -->
            <div v-if="item.photoUrl" class="equip-photo-container">
              <img :src="item.photoUrl" :alt="item.name" class="equip-photo" @error="$event.target.closest('.equip-photo-container').style.display='none'" />
            </div>
            <!-- No-photo placeholder -->
            <div v-else class="equip-no-photo">{{ item.icon }}</div>

            <div class="equip-card-top">
              <div>
                <h4 class="equip-name">{{ item.name }}</h4>
                <p class="equip-meta">{{ item.type }} · {{ item.location }}</p>
              </div>
              <span :class="['status-badge', 'status-' + item.status.replace(/ /g, '-')]">{{ item.status }}</span>
            </div>

            <div class="equip-condition">
              <span class="cond-label">Condition:</span>
              <span :class="['cond-badge', 'cond-' + item.condition.toLowerCase()]">{{ item.condition }}</span>
            </div>

            <p v-if="item.availableCount !== null" class="equip-stock">
              {{ item.availableCount }} unit{{ item.availableCount !== 1 ? 's' : '' }} available
            </p>

            <button
              v-if="item.status === 'Available'"
              class="request-btn"
              :class="{ 'request-btn-selected': selectedItemIds.has(item.id) }"
              @click="toggleItemSelection(item)"
            >
              {{ selectedItemIds.has(item.id) ? '✓ Selected' : '+ Add to Request' }}
            </button>
            <button v-else class="request-btn" disabled>Unavailable</button>
          </div>
        </div>

        <!-- Floating request bar -->
        <div v-if="selectedItemIds.size > 0" class="request-cart-bar">
          <span class="cart-summary">{{ selectedItemIds.size }} item{{ selectedItemIds.size !== 1 ? 's' : '' }} selected</span>
          <button class="cart-clear-btn" @click="selectedItemIds.clear()">Clear</button>
          <button class="cart-submit-btn" @click="openRequestModal()">Request All →</button>
        </div>
      </section>

      <!-- ── MY REQUESTS ── -->
      <section v-if="activeView === 'requests'" class="view-section">
        <div class="panel full-panel">
          <div class="panel-header">
            <h3>My Requests</h3>
            <span v-if="requestsUnavailable" style="font-size:12px;color:#aaa">⚠ Request service offline</span>
          </div>
          <div v-if="myRequests.length === 0" class="empty-state">You have no requests yet.</div>
          <table v-else class="data-table">
            <thead>
              <tr>
                <th>Item</th>
                <th>Requested</th>
                <th>From</th>
                <th>Until</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="r in myRequests" :key="r.id">
                <td>{{ r.itemName }}</td>
                <td>{{ r.requestDate }}</td>
                <td>{{ r.fromDate }}</td>
                <td>{{ r.toDate }}</td>
                <td><span :class="['status-badge', 'status-' + r.status]">{{ r.status }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- ── HISTORY ── -->
      <section v-if="activeView === 'history'" class="view-section">
        <div class="panel full-panel">
          <div class="panel-header">
            <h3>Borrowing History</h3>
            <span v-if="requestsUnavailable" style="font-size:12px;color:#aaa">⚠ Request service offline</span>
          </div>
          <div v-if="borrowHistory.length === 0" class="empty-state">No history yet.</div>
          <table v-else class="data-table">
            <thead>
              <tr>
                <th>Item</th>
                <th>Borrowed</th>
                <th>Returned</th>
                <th>Condition</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="h in borrowHistory" :key="h.id">
                <td>{{ h.itemName }}</td>
                <td>{{ h.borrowedDate }}</td>
                <td>{{ h.returnedDate || '—' }}</td>
                <td>
                  <span v-if="h.returnCondition" :class="['cond-badge', 'cond-' + h.returnCondition.toLowerCase()]">{{ h.returnCondition }}</span>
                  <span v-else>—</span>
                </td>
                <td><span :class="['status-badge', 'status-' + h.status]">{{ h.status }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

    </main>

    <!-- ── REQUEST MODAL ── -->
    <div v-if="showRequestModal" class="modal-overlay" @click.self="showRequestModal = false">
      <div class="modal-box request-modal">
        <h3 class="modal-title">Request Borrow</h3>

        <!-- Selected items list -->
        <div class="modal-items-list">
          <div v-for="item in selectedItemsList" :key="item.id" class="modal-item-preview">
            <span class="modal-item-icon">{{ item.icon }}</span>
            <div>
              <p class="modal-item-name">{{ item.name }}</p>
              <p class="modal-item-meta">{{ item.type }} · {{ item.location }}</p>
            </div>
            <button class="modal-item-remove" @click="toggleItemSelection(item)" title="Remove">✕</button>
          </div>
        </div>

        <div v-if="requestError" class="alert alert-error">{{ requestError }}</div>
        <div v-if="requestSuccess" class="alert alert-success">{{ requestSuccess }}</div>

        <div class="input-field">
          <label>From Date</label>
          <input type="date" v-model="reqFrom" :min="today" />
        </div>
        <div class="input-field">
          <label>Until Date</label>
          <input type="date" v-model="reqTo" :min="reqFrom || today" />
        </div>

        <div class="modal-actions">
          <button class="login-button" @click="submitRequest" :disabled="reqLoading">
            <span v-if="reqLoading" class="spinner"></span>
            <span v-else>Submit Request</span>
          </button>
          <button class="cancel-btn" @click="showRequestModal = false">Cancel</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const API_BASE = ''

// ── Auth / User info ──
const token = localStorage.getItem('invtr_token') || sessionStorage.getItem('invtr_token')

const userName = ref('User')
const userInitials = computed(() =>
  userName.value.split(' ').map(w => w[0]).filter(Boolean).join('').toUpperCase().slice(0, 2) || 'U'
)

// ── Theme ──
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
  setTimeout(() => document.documentElement.classList.remove('theme-transitioning'), 400)
}

let userEmail = ''
if (token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    userEmail = payload.sub || ''
    userName.value = userEmail || 'User'
  } catch (_) {}
}

// ── Enum → display maps ──
const STATUS_DISPLAY = {
  AVAILABLE:    'Available',
  CHECKED_OUT:  'Checked Out',
  UNDER_REPAIR: 'Under Repair',
  RETIRED:      'Retired',
}
const CONDITION_DISPLAY = {
  EXCELLENT: 'Excellent',
  GOOD:      'Good',
  DAMAGED:   'Damaged',
  BROKEN:    'Broken',
}
const TYPE_DISPLAY = {
  ELECTRICAL: 'Electrical',
  FURNITURE:  'Furniture',
  UTILITY:    'Utility',
}
const TYPE_ICON = {
  ELECTRICAL: '🔌',
  FURNITURE:  '🪑',
  UTILITY:    '🔧',
}

// ── UI State ──
const sidebarCollapsed = ref(false)
const activeView = ref('overview')
const searchQuery = ref('')
const filterType = ref('')
const filterStatus = ref('')

const navItems = [
  { id: 'overview',  icon: '🏠', label: 'Overview' },
  { id: 'inventory', icon: '📦', label: 'Inventory' },
  { id: 'requests',  icon: '📋', label: 'My Requests' },
  { id: 'history',   icon: '🕐', label: 'History' },
]

const currentPageTitle = computed(() => navItems.find(n => n.id === activeView.value)?.label || '')
const todayDate = new Date().toLocaleDateString('en-GB', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })
const today = new Date().toISOString().split('T')[0]

// ── Data refs ──
const equipment            = ref([])
const rawRequests          = ref([])
const equipmentLoading     = ref(false)
const equipmentError       = ref('')
const requestsUnavailable  = ref(false)

// ── Computed: enrich requests with equipment names reactively ──
const myRequests = computed(() =>
  rawRequests.value.map(r => ({
    ...r,
    itemName: (r.equipmentIds || []).map(id => {
      const eq = equipment.value.find(e => e.id === id)
      return eq ? eq.name : `Item #${id}`
    }).join(', '),
  }))
)

const borrowHistory = computed(() =>
  myRequests.value.map(r => ({
    ...r,
    borrowedDate:    r.fromDate,
    returnedDate:    r.status === 'returned' ? r.toDate : null,
    returnCondition: null,
  }))
)

const activeBorrows  = computed(() => myRequests.value.filter(r => r.status === 'approved'))
const recentRequests = computed(() => myRequests.value.slice(0, 3))
const stats = computed(() => ({
  available:       equipment.value.filter(e => e.status === 'Available').length,
  currentRequests: myRequests.value.filter(r => ['pending', 'approved'].includes(r.status)).length,
  active:          activeBorrows.value.length,
  total:           myRequests.value.length,
}))
const equipmentTypes = computed(() => [...new Set(equipment.value.map(e => e.type))])
const filteredEquipment = computed(() => equipment.value.filter(e => {
  const q = searchQuery.value.toLowerCase()
  const matchSearch  = !q || e.name.toLowerCase().includes(q) || e.type.toLowerCase().includes(q)
  const matchType    = !filterType.value   || e.type   === filterType.value
  const matchStatus  = !filterStatus.value || e.status === filterStatus.value
  return matchSearch && matchType && matchStatus
}))

// ── Fetch ──
const authHeaders = () => ({ 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' })

const loadEquipment = async () => {
  equipmentLoading.value = true
  equipmentError.value = ''
  try {
    const res = await fetch(`${API_BASE}/equipment`, { headers: authHeaders() })
    if (!res.ok) { equipmentError.value = `Could not load equipment (${res.status}).`; return }
    const data = await res.json()
    const availByName = {}
    data.forEach(e => {
      if (e.status === 'AVAILABLE') availByName[e.name] = (availByName[e.name] || 0) + 1
    })
    equipment.value = data.map(e => ({
      id:             e.id,
      name:           e.name,
      type:           TYPE_DISPLAY[e.type]           || e.type,
      condition:      CONDITION_DISPLAY[e.condition] || e.condition,
      status:         STATUS_DISPLAY[e.status]       || e.status,
      location:       e.location || '—',
      photoUrl:       e.photoUrl || null,
      icon:           TYPE_ICON[e.type] || '📦',
      availableCount: availByName[e.name] ?? null,
    }))
  } catch (_) {
    equipmentError.value = 'Could not reach the server. Is the backend running?'
  } finally {
    equipmentLoading.value = false
  }
}

// Fetches all user requests from GET /requests (backend extracts userId from JWT)
const loadRequests = async () => {
  try {
    const res = await fetch(`${API_BASE}/requests`, { headers: authHeaders() })
    if (res.ok) {
      const data = await res.json()
      rawRequests.value = data.map(r => ({
        id:           r.id,
        equipmentIds: r.equipmentIds || [],
        requestDate:  r.requestDate  || '',
        fromDate:     r.startDateTime ? r.startDateTime.split('T')[0] : '',
        toDate:       r.endDateTime   ? r.endDateTime.split('T')[0]   : '',
        status:       (r.status || '').toLowerCase(),
      }))
    } else {
      requestsUnavailable.value = true
    }
  } catch (_) {
    requestsUnavailable.value = true
  }
}

onMounted(() => {
  loadEquipment()
  loadRequests()
})

// ── Request Modal ──
const showRequestModal  = ref(false)
const selectedItemIds   = ref(new Set())
const reqFrom           = ref('')
const reqTo             = ref('')
const reqLoading        = ref(false)
const requestError      = ref('')
const requestSuccess    = ref('')

const selectedItemsList = computed(() =>
  equipment.value.filter(e => selectedItemIds.value.has(e.id))
)

const toggleItemSelection = (item) => {
  const ids = new Set(selectedItemIds.value)
  if (ids.has(item.id)) ids.delete(item.id)
  else ids.add(item.id)
  selectedItemIds.value = ids
  // Close modal if all items removed
  if (showRequestModal.value && ids.size === 0) showRequestModal.value = false
}

const openRequestModal = () => {
  reqFrom.value        = ''
  reqTo.value          = ''
  requestError.value   = ''
  requestSuccess.value = ''
  showRequestModal.value = true
}

const submitRequest = async () => {
  requestError.value = ''
  if (selectedItemIds.value.size === 0) { requestError.value = 'No items selected.'; return }
  if (!reqFrom.value || !reqTo.value)   { requestError.value = 'Please select both dates.'; return }
  if (reqTo.value < reqFrom.value)      { requestError.value = 'End date must be after start date.'; return }

  reqLoading.value = true
  try {
    const ids = [...selectedItemIds.value]
    const res = await fetch(`${API_BASE}/requests`, {
      method: 'POST',
      headers: authHeaders(),
      body: JSON.stringify({ equipmentIds: ids, fromDate: reqFrom.value, toDate: reqTo.value })
    })
    if (!res.ok) {
      let msg = 'Request failed. Please try again.'
      try { const d = await res.json(); msg = d.message || d.error || msg } catch (_) {}
      requestError.value = msg; return
    }
    const data = await res.json()
    requestSuccess.value = 'Request submitted successfully!'
    rawRequests.value.unshift({
      id:           data.id,
      equipmentIds: data.equipmentIds || ids,
      requestDate:  data.requestDate  || today,
      fromDate:     data.startDateTime ? data.startDateTime.split('T')[0] : reqFrom.value,
      toDate:       data.endDateTime   ? data.endDateTime.split('T')[0]   : reqTo.value,
      status:       (data.status || 'pending').toLowerCase(),
    })
    selectedItemIds.value = new Set()
    setTimeout(() => { showRequestModal.value = false }, 1500)
  } catch (_) {
    requestError.value = 'Could not reach the request service. Is it running?'
  } finally {
    reqLoading.value = false
  }
}

// ── Logout ──
const handleLogout = () => {
  localStorage.removeItem('invtr_token')
  sessionStorage.removeItem('invtr_token')
  router.push('/login')
}
</script>

<style>
@import '@/assets/dashboard.css';
</style>
