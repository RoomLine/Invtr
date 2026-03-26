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
            <span class="user-role">{{ $t('user.role') }}</span>
          </div>
        </div>
        <button class="theme-toggle-btn" @click="toggleTheme" :title="isDark ? $t('common.lightMode') : $t('common.darkMode')">
          {{ isDark ? '☀️' : '🌙' }}
        </button>
        <button class="logout-btn" @click="handleLogout" :title="$t('auth.logout')">⏻</button>
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
            <input v-model="searchQuery" :placeholder="$t('inventory.search')" class="search-input" />
          </div>
          <button class="lang-toggle-btn" @click="toggleLanguage">{{ currentLocale === 'bg' ? '🇬🇧 EN' : '🇧🇬 BG' }}</button>
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
              <span class="stat-label">{{ $t('user.overview.availableItems') }}</span>
            </div>
          </div>
          <div class="stat-card stat-orange">
            <div class="stat-icon">⏳</div>
            <div class="stat-body">
              <span class="stat-value">{{ stats.currentRequests }}</span>
              <span class="stat-label">{{ $t('user.overview.currentRequests') }}</span>
            </div>
          </div>
          <div class="stat-card stat-green">
            <div class="stat-icon">✅</div>
            <div class="stat-body">
              <span class="stat-value">{{ stats.active }}</span>
              <span class="stat-label">{{ $t('user.overview.activeBorrows') }}</span>
            </div>
          </div>
          <div class="stat-card stat-gray">
            <div class="stat-icon">📋</div>
            <div class="stat-body">
              <span class="stat-value">{{ stats.total }}</span>
              <span class="stat-label">{{ $t('user.overview.totalRequests') }}</span>
            </div>
          </div>
        </div>

        <div class="overview-bottom">
          <div class="panel">
            <div class="panel-header">
              <h3>{{ $t('user.overview.myActiveBorrows') }}</h3>
              <button class="link-btn" @click="activeView = 'history'">{{ $t('common.viewAll') }} →</button>
            </div>
            <div v-if="activeBorrows.length === 0" class="empty-state">{{ $t('user.overview.noActiveBorrows') }}</div>
            <div v-else class="borrow-list">
              <div v-for="b in activeBorrows" :key="b.id" class="borrow-row">
                <div class="borrow-info">
                  <span class="borrow-name">{{ b.itemName }}</span>
                  <span class="borrow-date">{{ $t('user.overview.borrowed') }} {{ b.borrowedDate }}</span>
                </div>
                <span :class="['status-badge', 'status-approved']">{{ translateReqStatus(b.status) }}</span>
              </div>
            </div>
          </div>

          <div class="panel">
            <div class="panel-header">
              <h3>{{ $t('user.overview.recentRequests') }}</h3>
              <button class="link-btn" @click="activeView = 'requests'">{{ $t('common.viewAll') }} →</button>
            </div>
            <div v-if="recentRequests.length === 0" class="empty-state">{{ $t('user.overview.noRecentRequests') }}</div>
            <div v-else class="borrow-list">
              <div v-for="r in recentRequests" :key="r.id" class="borrow-row">
                <div class="borrow-info">
                  <span class="borrow-name">{{ r.itemName }}</span>
                  <span class="borrow-date">{{ r.requestDate }}</span>
                </div>
                <span :class="['status-badge', 'status-' + r.status]">{{ translateReqStatus(r.status) }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ── INVENTORY ── -->
      <section v-if="activeView === 'inventory'" class="view-section">
        <div class="filter-bar">
          <!-- Filter by type: values are raw enums -->
          <select v-model="filterType" class="filter-select">
            <option value="">{{ $t('user.inventory.allTypes') }}</option>
            <option value="ELECTRICAL">{{ $t('inventory.categories.electronics') }}</option>
            <option value="FURNITURE">{{ $t('inventory.categories.furniture') }}</option>
            <option value="UTILITY">{{ $t('inventory.categories.utility') }}</option>
          </select>
          <!-- Filter by status: values are raw enums -->
          <select v-model="filterStatus" class="filter-select">
            <option value="">{{ $t('user.inventory.allStatuses') }}</option>
            <option value="AVAILABLE">{{ $t('inventory.statuses.available') }}</option>
            <option value="CHECKED_OUT">{{ $t('inventory.statuses.checkedOut') }}</option>
            <option value="UNDER_REPAIR">{{ $t('inventory.statuses.underRepair') }}</option>
            <option value="RETIRED">{{ $t('inventory.statuses.retired') }}</option>
          </select>
        </div>

        <div v-if="equipmentLoading" class="empty-state-full">{{ $t('user.inventory.loading') }}</div>
        <div v-else-if="equipmentError" class="empty-state-full" style="color:#e74c3c">{{ equipmentError }}</div>
        <div v-else-if="filteredEquipment.length === 0" class="empty-state-full">{{ $t('user.inventory.noEquipment') }}</div>
        <div v-else class="equipment-grid">
          <div
            v-for="item in filteredEquipment"
            :key="item.id"
            class="equip-card"
            :class="{ 'equip-card-selected': selectedItemIds.has(item.id) }"
          >
            <div v-if="item.photoUrl" class="equip-photo-container">
              <img :src="item.photoUrl" :alt="item.name" class="equip-photo"
                @error="$event.target.closest('.equip-photo-container').style.display='none'" />
            </div>
            <div v-else class="equip-no-photo">{{ item.icon }}</div>

            <div class="equip-card-top">
              <div>
                <h4 class="equip-name">{{ item.name }}</h4>
                <!-- Type and location — type translated reactively -->
                <p class="equip-meta">{{ translateType(item.rawType) }} · {{ item.location }}</p>
              </div>
              <!-- Status badge — class and text both from raw enum -->
              <span :class="['status-badge', equipStatusClass(item.rawStatus)]">
                {{ translateEquipStatus(item.rawStatus) }}
              </span>
            </div>

            <div class="equip-condition">
              <span class="cond-label">{{ $t('user.inventory.condition') }}</span>
              <!-- Condition badge — class and text both from raw enum -->
              <span :class="['cond-badge', 'cond-' + item.rawCondition.toLowerCase()]">
                {{ translateCondition(item.rawCondition) }}
              </span>
            </div>



            <button
              v-if="item.rawStatus === 'AVAILABLE'"
              class="request-btn"
              :class="{ 'request-btn-selected': selectedItemIds.has(item.id) }"
              @click="toggleItemSelection(item)"
            >
              {{ selectedItemIds.has(item.id) ? $t('user.inventory.selected') : $t('user.inventory.addToRequest') }}
            </button>
            <button v-else class="request-btn" disabled>{{ $t('user.inventory.unavailable') }}</button>
          </div>
        </div>

        <!-- Floating request bar -->
        <div v-if="selectedItemIds.size > 0" class="request-cart-bar">
          <span class="cart-summary">{{ selectedItemIds.size }} {{ $t('user.cart.selected') }}</span>
          <button class="cart-clear-btn" @click="selectedItemIds.clear()">{{ $t('user.cart.clear') }}</button>
          <button class="cart-submit-btn" @click="openRequestModal()">{{ $t('user.cart.requestAll') }}</button>
        </div>
      </section>

      <!-- ── MY REQUESTS ── -->
      <section v-if="activeView === 'requests'" class="view-section">
        <div class="panel full-panel">
          <div class="panel-header">
            <h3>{{ $t('user.requests.title') }}</h3>
            <span v-if="requestsUnavailable" style="font-size:12px;color:#aaa">{{ $t('user.requests.serviceOffline') }}</span>
          </div>
          <div v-if="myRequests.length === 0" class="empty-state">{{ $t('user.requests.empty') }}</div>
          <table v-else class="data-table">
            <thead>
              <tr>
                <th>{{ $t('user.requests.item') }}</th>
                <th>{{ $t('user.requests.requested') }}</th>
                <th>{{ $t('user.requests.from') }}</th>
                <th>{{ $t('user.requests.until') }}</th>
                <th>{{ $t('inventory.status') }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="r in myRequests" :key="r.id">
                <td>{{ r.itemName }}</td>
                <td>{{ r.requestDate }}</td>
                <td>{{ r.fromDate }}</td>
                <td>{{ r.toDate }}</td>
                <td><span :class="['status-badge', 'status-' + r.status]">{{ translateReqStatus(r.status) }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- ── HISTORY ── -->
      <section v-if="activeView === 'history'" class="view-section">
        <div class="panel full-panel">
          <div class="panel-header">
            <h3>{{ $t('user.history.title') }}</h3>
            <span v-if="requestsUnavailable" style="font-size:12px;color:#aaa">{{ $t('user.requests.serviceOffline') }}</span>
          </div>
          <div v-if="borrowHistory.length === 0" class="empty-state">{{ $t('user.history.empty') }}</div>
          <table v-else class="data-table">
            <thead>
              <tr>
                <th>{{ $t('user.history.item') }}</th>
                <th>{{ $t('user.history.borrowed') }}</th>
                <th>{{ $t('user.history.returned') }}</th>
                <th>{{ $t('user.history.condition') }}</th>
                <th>{{ $t('inventory.status') }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="h in borrowHistory" :key="h.id">
                <td>{{ h.itemName }}</td>
                <td>{{ h.borrowedDate }}</td>
                <td>{{ h.returnedDate || '—' }}</td>
                <td>
                  <span v-if="h.returnCondition" :class="['cond-badge', 'cond-' + h.returnCondition.toLowerCase()]">
                    {{ translateCondition(h.returnCondition) }}
                  </span>
                  <span v-else>—</span>
                </td>
                <td><span :class="['status-badge', 'status-' + h.status]">{{ translateReqStatus(h.status) }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

    </main>

    <!-- ── REQUEST MODAL ── -->
    <div v-if="showRequestModal" class="modal-overlay" @click.self="showRequestModal = false">
      <div class="modal-box request-modal">
        <h3 class="modal-title">{{ $t('user.modal.title') }}</h3>

        <div class="modal-items-list">
          <div v-for="item in selectedItemsList" :key="item.id" class="modal-item-preview">
            <span class="modal-item-icon">{{ item.icon }}</span>
            <div>
              <p class="modal-item-name">{{ item.name }}</p>
              <p class="modal-item-meta">{{ translateType(item.rawType) }} · {{ item.location }}</p>
            </div>
            <button class="modal-item-remove" @click="toggleItemSelection(item)" title="Remove">✕</button>
          </div>
        </div>

        <div v-if="requestError" class="alert alert-error">{{ requestError }}</div>
        <div v-if="requestSuccess" class="alert alert-success">{{ requestSuccess }}</div>

        <div class="input-field">
          <label>{{ $t('user.modal.fromDate') }}</label>
          <input type="date" v-model="reqFrom" :min="today" />
        </div>
        <div class="input-field">
          <label>{{ $t('user.modal.untilDate') }}</label>
          <input type="date" v-model="reqTo" :min="reqFrom || today" />
        </div>

        <div class="modal-actions">
          <button class="login-button" @click="submitRequest" :disabled="reqLoading">
            <span v-if="reqLoading" class="spinner"></span>
            <span v-else>{{ $t('user.modal.submit') }}</span>
          </button>
          <button class="cancel-btn" @click="showRequestModal = false">{{ $t('common.cancel') }}</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { locale, t } = useI18n()
const API_BASE = ''

// ── Language Toggle ──
const currentLocale = computed(() => locale.value)
function toggleLanguage() {
  const newLocale = locale.value === 'bg' ? 'en' : 'bg'
  locale.value = newLocale
  localStorage.setItem('user-locale', newLocale)
}

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

if (token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    userName.value = payload.sub || 'User'
  } catch (_) {}
}

// ── Reactive translation helpers (use raw enum keys stored from API) ──
function translateType(rawKey) {
  const map = {
    ELECTRICAL: 'inventory.categories.electronics',
    FURNITURE:  'inventory.categories.furniture',
    UTILITY:    'inventory.categories.utility',
  }
  return t(map[rawKey] || rawKey)
}

function translateCondition(rawKey) {
  if (!rawKey) return '—'
  const map = {
    EXCELLENT: 'inventory.conditions.excellent',
    GOOD:      'inventory.conditions.good',
    DAMAGED:   'inventory.conditions.damaged',
    BROKEN:    'inventory.conditions.broken',
  }
  return t(map[rawKey.toUpperCase()] || rawKey)
}

function translateEquipStatus(rawKey) {
  const map = {
    AVAILABLE:    'inventory.statuses.available',
    CHECKED_OUT:  'inventory.statuses.checkedOut',
    UNDER_REPAIR: 'inventory.statuses.underRepair',
    RETIRED:      'inventory.statuses.retired',
  }
  return t(map[rawKey] || rawKey)
}

function equipStatusClass(rawKey) {
  const map = {
    AVAILABLE:    'status-Available',
    CHECKED_OUT:  'status-Checked-Out',
    UNDER_REPAIR: 'status-Under-Repair',
    RETIRED:      'status-Retired',
  }
  return map[rawKey] || 'status-' + rawKey
}

// Request status translation (lowercase from API: pending, approved …)
function translateReqStatus(status) {
  const map = {
    pending:  'requests.tabs.pending',
    approved: 'requests.tabs.approved',
    rejected: 'requests.tabs.rejected',
    returned: 'requests.tabs.returned',
  }
  return t(map[(status || '').toLowerCase()] || status)
}

// Icon map
const TYPE_ICON = { ELECTRICAL: '🔌', FURNITURE: '🪑', UTILITY: '🔧' }

// ── UI State ──
const sidebarCollapsed = ref(false)
const activeView       = ref('overview')
const searchQuery      = ref('')
const filterType       = ref('')   // raw enum: ELECTRICAL | FURNITURE | UTILITY
const filterStatus     = ref('')   // raw enum: AVAILABLE | CHECKED_OUT | …

// Nav items reactive to locale
const navItems = computed(() => [
  { id: 'overview',  icon: '🏠', label: t('navigation.dashboard') },
  { id: 'inventory', icon: '📦', label: t('navigation.inventory') },
  { id: 'requests',  icon: '📋', label: t('navigation.myRequests') },
  { id: 'history',   icon: '🕐', label: t('navigation.history') },
])

const currentPageTitle = computed(() => navItems.value.find(n => n.id === activeView.value)?.label || '')
const todayDate = computed(() =>
  new Date().toLocaleDateString(locale.value, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })
)
const today = new Date().toISOString().split('T')[0]

// ── Data ──
const equipment           = ref([])
const rawRequests         = ref([])
const equipmentLoading    = ref(false)
const equipmentError      = ref('')
const requestsUnavailable = ref(false)

// ── Computed: enrich requests with item names ──
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
  // Use rawStatus (uppercase enum) for filtering
  available:       equipment.value.filter(e => e.rawStatus === 'AVAILABLE').length,
  currentRequests: myRequests.value.filter(r => ['pending', 'approved'].includes(r.status)).length,
  active:          activeBorrows.value.length,
  total:           myRequests.value.length,
}))

// equipmentTypes not needed since we use fixed enum dropdown
const filteredEquipment = computed(() => equipment.value.filter(e => {
  const q = searchQuery.value.toLowerCase()
  const matchSearch = !q || e.name.toLowerCase().includes(q) || e.rawType.toLowerCase().includes(q)
  const matchType   = !filterType.value   || e.rawType   === filterType.value
  const matchStatus = !filterStatus.value || e.rawStatus === filterStatus.value
  return matchSearch && matchType && matchStatus
}))

// ── Fetch ──
const authHeaders = () => ({ 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' })

const loadEquipment = async () => {
  equipmentLoading.value = true
  equipmentError.value   = ''
  try {
    const res = await fetch(`${API_BASE}/equipment`, { headers: authHeaders() })
    if (!res.ok) { equipmentError.value = `Could not load equipment (${res.status}).`; return }
    const data = await res.json()
    const availByName = {}
    data.forEach(e => {
      if (e.status === 'AVAILABLE') availByName[e.name] = (availByName[e.name] || 0) + 1
    })
    // Store raw enum keys — translation happens reactively in the template
    equipment.value = data.map(e => ({
      id:             e.id,
      name:           e.name,
      rawType:        e.type,       // ELECTRICAL | FURNITURE | UTILITY
      rawCondition:   e.condition,  // EXCELLENT | GOOD | DAMAGED | BROKEN
      rawStatus:      e.status,     // AVAILABLE | CHECKED_OUT | UNDER_REPAIR | RETIRED
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

let pollTimer = null
let fastPollTimer = null

function startFastPoll() {
  if (fastPollTimer) return
  fastPollTimer = setInterval(() => loadEquipment(), 8000)
}
function stopFastPoll() {
  if (fastPollTimer) { clearInterval(fastPollTimer); fastPollTimer = null }
}

onMounted(() => {
  loadEquipment()
  loadRequests()
  // Slow background poll for everything
  pollTimer = setInterval(() => { loadEquipment(); loadRequests() }, 30000)
})
onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
  stopFastPoll()
})

// Fast-poll equipment only while on the inventory tab
watch(activeView, (view) => {
  if (view === 'inventory') {
    loadEquipment()
    startFastPoll()
  } else {
    stopFastPoll()
  }
})

// ── Request Modal ──
const showRequestModal  = ref(false)
const selectedItemIds   = ref(new Set())
const reqFrom           = ref('')
const reqTo             = ref('')
const reqLoading        = ref(false)
const requestError      = ref('')
const requestSuccess    = ref('')

const selectedItemsList = computed(() => equipment.value.filter(e => selectedItemIds.value.has(e.id)))

const toggleItemSelection = (item) => {
  const ids = new Set(selectedItemIds.value)
  if (ids.has(item.id)) ids.delete(item.id)
  else ids.add(item.id)
  selectedItemIds.value = ids
  if (showRequestModal.value && ids.size === 0) showRequestModal.value = false
}

const openRequestModal = () => {
  reqFrom.value = reqTo.value = requestError.value = requestSuccess.value = ''
  showRequestModal.value = true
}

const submitRequest = async () => {
  requestError.value = ''
  if (selectedItemIds.value.size === 0) { requestError.value = t('user.modal.noItems'); return }
  if (!reqFrom.value || !reqTo.value)   { requestError.value = t('user.modal.selectDates'); return }
  if (reqTo.value < reqFrom.value)      { requestError.value = t('user.modal.endAfterStart'); return }

  reqLoading.value = true
  try {
    const ids = [...selectedItemIds.value]
    const res = await fetch(`${API_BASE}/requests`, {
      method: 'POST',
      headers: authHeaders(),
      body: JSON.stringify({ equipmentIds: ids, fromDate: reqFrom.value, toDate: reqTo.value }),
    })
    if (!res.ok) {
      let msg = t('user.modal.serverError')
      try { const d = await res.json(); msg = d.message || d.error || msg } catch (_) {}
      requestError.value = msg; return
    }
    const data = await res.json()
    requestSuccess.value = t('user.modal.success')
    rawRequests.value.unshift({
      id:           data.id,
      equipmentIds: data.equipmentIds || ids,
      requestDate:  data.requestDate  || today,
      fromDate:     data.startDateTime ? data.startDateTime.split('T')[0] : reqFrom.value,
      toDate:       data.endDateTime   ? data.endDateTime.split('T')[0]   : reqTo.value,
      status:       (data.status || 'pending').toLowerCase(),
    })
    selectedItemIds.value = new Set()
    setTimeout(() => {
      showRequestModal.value = false
      loadEquipment(); loadRequests()
    }, 1500)
  } catch (_) {
    requestError.value = t('user.modal.serverError')
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
