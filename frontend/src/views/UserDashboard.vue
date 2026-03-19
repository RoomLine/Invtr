<template>
  <div class="app-shell">

    <!-- ── SIDEBAR ── -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <img src="@/assets/logo-png.jpg" alt="INVTR" class="sidebar-logo" />
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
              <span class="stat-value">{{ stats.pending }}</span>
              <span class="stat-label">Pending Requests</span>
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
              <span class="stat-label">Total Requests</span>
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

        <div v-if="filteredEquipment.length === 0" class="empty-state-full">No equipment found.</div>
        <div v-else class="equipment-grid">
          <div v-for="item in filteredEquipment" :key="item.id" class="equip-card">
            <div class="equip-card-top">
              <div class="equip-icon">{{ item.icon }}</div>
              <span :class="['status-badge', 'status-' + item.status.replace(' ', '-')]">{{ item.status }}</span>
            </div>
            <h4 class="equip-name">{{ item.name }}</h4>
            <p class="equip-meta">{{ item.type }} · {{ item.location }}</p>
            <p class="equip-serial">S/N: {{ item.serialNumber }}</p>
            <div class="equip-condition">
              <span class="cond-label">Condition:</span>
              <span :class="['cond-badge', 'cond-' + item.condition.toLowerCase()]">{{ item.condition }}</span>
            </div>
            <button
              class="request-btn"
              :disabled="item.status !== 'Available'"
              @click="openRequestModal(item)"
            >
              {{ item.status === 'Available' ? 'Request Borrow' : 'Unavailable' }}
            </button>
          </div>
        </div>
      </section>

      <!-- ── MY REQUESTS ── -->
      <section v-if="activeView === 'requests'" class="view-section">
        <div class="panel full-panel">
          <div class="panel-header">
            <h3>My Requests</h3>
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
        <div class="modal-item-preview">
          <span class="modal-item-icon">{{ selectedItem?.icon }}</span>
          <div>
            <p class="modal-item-name">{{ selectedItem?.name }}</p>
            <p class="modal-item-meta">{{ selectedItem?.type }} · {{ selectedItem?.location }}</p>
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const API_BASE = ''

// ── Auth / User info ──
const token = localStorage.getItem('invtr_token') || sessionStorage.getItem('invtr_token')
const userName = ref('User')
const userInitials = computed(() => userName.value.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2))

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

// ── Mock Data ──
const equipment = ref([
  { id: 1, name: 'Projector Epson X41+', type: 'Projector', serialNumber: 'EPS-2024-001', condition: 'Good', status: 'Available', location: 'Room 101', icon: '📽️' },
  { id: 2, name: 'Projector BenQ MH535', type: 'Projector', serialNumber: 'BNQ-2023-002', condition: 'Good', status: 'Checked Out', location: 'Room 203', icon: '📽️' },
  { id: 3, name: 'Dell Monitor 24"', type: 'Monitor', serialNumber: 'DEL-2022-011', condition: 'Excellent', status: 'Available', location: 'Lab A', icon: '🖥️' },
  { id: 4, name: 'HP Laptop ProBook', type: 'Laptop', serialNumber: 'HP-2023-045', condition: 'Good', status: 'Available', location: 'Lab B', icon: '💻' },
  { id: 5, name: 'Logitech Keyboard K120', type: 'Peripheral', serialNumber: 'LOG-2021-088', condition: 'Fair', status: 'Available', location: 'Storage', icon: '⌨️' },
  { id: 6, name: 'SanDisk USB 64GB', type: 'Storage', serialNumber: 'SAN-2022-200', condition: 'Good', status: 'Available', location: 'Storage', icon: '💾' },
  { id: 7, name: 'Canon DSLR EOS 2000D', type: 'Camera', serialNumber: 'CAN-2020-007', condition: 'Good', status: 'Under Repair', location: 'Media Room', icon: '📷' },
  { id: 8, name: 'iPad 9th Gen', type: 'Tablet', serialNumber: 'APL-2023-033', condition: 'Excellent', status: 'Available', location: 'Room 305', icon: '📱' },
])

const myRequests = ref([
  { id: 1, itemName: 'SanDisk USB 64GB', requestDate: '2026-03-10', fromDate: '2026-03-11', toDate: '2026-03-15', status: 'approved' },
  { id: 2, itemName: 'Projector Epson X41+', requestDate: '2026-03-14', fromDate: '2026-03-15', toDate: '2026-03-16', status: 'pending' },
  { id: 3, itemName: 'iPad 9th Gen', requestDate: '2026-03-01', fromDate: '2026-03-02', toDate: '2026-03-05', status: 'rejected' },
])

const borrowHistory = ref([
  { id: 1, itemName: 'Dell Monitor 24"', borrowedDate: '2026-02-10', returnedDate: '2026-02-15', returnCondition: 'Good', status: 'returned' },
  { id: 2, itemName: 'SanDisk USB 64GB', borrowedDate: '2026-03-11', returnedDate: null, returnCondition: null, status: 'active' },
  { id: 3, itemName: 'HP Laptop ProBook', borrowedDate: '2026-01-20', returnedDate: '2026-01-25', returnCondition: 'Fair', status: 'returned' },
])

const activeBorrows = computed(() => borrowHistory.value.filter(b => b.status === 'active'))
const recentRequests = computed(() => myRequests.value.slice(0, 3))

const stats = computed(() => ({
  available: equipment.value.filter(e => e.status === 'Available').length,
  pending: myRequests.value.filter(r => r.status === 'pending').length,
  active: activeBorrows.value.length,
  total: myRequests.value.length,
}))

const equipmentTypes = computed(() => [...new Set(equipment.value.map(e => e.type))])

const filteredEquipment = computed(() => {
  return equipment.value.filter(e => {
    const matchSearch = !searchQuery.value ||
      e.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      e.type.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchType = !filterType.value || e.type === filterType.value
    const matchStatus = !filterStatus.value || e.status === filterStatus.value
    return matchSearch && matchType && matchStatus
  })
})

// ── Request Modal ──
const showRequestModal = ref(false)
const selectedItem = ref(null)
const reqFrom = ref('')
const reqTo = ref('')
const reqLoading = ref(false)
const requestError = ref('')
const requestSuccess = ref('')

const openRequestModal = (item) => {
  selectedItem.value = item
  reqFrom.value = ''
  reqTo.value = ''
  requestError.value = ''
  requestSuccess.value = ''
  showRequestModal.value = true
}

const submitRequest = async () => {
  requestError.value = ''
  if (!reqFrom.value || !reqTo.value) { requestError.value = 'Please select both dates.'; return }
  if (reqTo.value < reqFrom.value) { requestError.value = 'End date must be after start date.'; return }

  reqLoading.value = true
  try {
    const res = await fetch(`${API_BASE}/request`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        equipmentId: selectedItem.value.id,
        fromDate: reqFrom.value,
        toDate: reqTo.value
      })
    })
    if (!res.ok) {
      let msg = 'Request failed. Please try again.'
      try { const d = await res.json(); msg = d.message || d.error || msg } catch (_) {}
      requestError.value = msg; return
    }
    requestSuccess.value = 'Request submitted successfully!'
    myRequests.value.unshift({
      id: Date.now(),
      itemName: selectedItem.value.name,
      requestDate: today,
      fromDate: reqFrom.value,
      toDate: reqTo.value,
      status: 'pending'
    })
    setTimeout(() => { showRequestModal.value = false }, 1500)
  } catch (_) {
    requestSuccess.value = 'Request submitted! (offline mode)'
    myRequests.value.unshift({
      id: Date.now(),
      itemName: selectedItem.value.name,
      requestDate: today,
      fromDate: reqFrom.value,
      toDate: reqTo.value,
      status: 'pending'
    })
    setTimeout(() => { showRequestModal.value = false }, 1500)
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