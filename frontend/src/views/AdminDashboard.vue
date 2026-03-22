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
import { ref, computed, reactive } from 'vue'
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

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
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

const EMOJI_MAP = { Electronics: '💻', Books: '📖', Tools: '🔧', Furniture: '🪑' }

const items = ref([
  { id: 1, name: 'Dell XPS Laptop',    emoji: '💻', category: 'Electronics', serial: 'SN-DELL-001', status: 'Available',    condition: 'excellent' },
  { id: 2, name: 'Epson Projector',     emoji: '📽️',  category: 'Electronics', serial: 'SN-EPS-123',  status: 'Checked-Out',  condition: 'good'      },
  { id: 3, name: 'Biology Textbook',    emoji: '📖', category: 'Books',       serial: 'ISBN-978-3',  status: 'Available',    condition: 'fair'      },
  { id: 4, name: 'Canon DSLR Camera',   emoji: '📷', category: 'Electronics', serial: 'SN-CAM-007',  status: 'Under-Repair', condition: 'fair'      },
  { id: 5, name: 'Standing Desk',       emoji: '🪑', category: 'Furniture',   serial: 'FN-DESK-011', status: 'Available',    condition: 'good'      },
  { id: 6, name: 'Cordless Drill',      emoji: '🔧', category: 'Tools',       serial: 'TL-DRL-033',  status: 'Available',    condition: 'excellent' },
  { id: 7, name: 'HP LaserJet Printer', emoji: '🖨️',  category: 'Electronics', serial: 'SN-HP-099',   status: 'Checked-Out',  condition: 'good'      },
  { id: 8, name: 'Chemistry Textbook',  emoji: '📗', category: 'Books',       serial: 'ISBN-341-A',  status: 'Available',    condition: 'excellent' },
])

const requests = ref([
  { id: 1, user: 'Alice Johnson', item: 'Dell XPS Laptop',  requested: '2026-03-15', returnBy: '2026-03-29', status: 'pending'  },
  { id: 2, user: 'Bob Smith',     item: 'Epson Projector',   requested: '2026-03-14', returnBy: '2026-03-21', status: 'approved' },
  { id: 3, user: 'Carol White',   item: 'Canon DSLR Camera', requested: '2026-03-13', returnBy: '2026-03-20', status: 'rejected' },
  { id: 4, user: 'David Lee',     item: 'HP LaserJet',       requested: '2026-03-16', returnBy: '2026-03-30', status: 'pending'  },
  { id: 5, user: 'Eva Green',     item: 'Standing Desk',     requested: '2026-03-17', returnBy: '2026-04-03', status: 'pending'  },
])

const users = ref([
  { id: 1, name: 'Alice Johnson', email: 'alice@school.edu', role: 'Student', borrows: 1, active: true  },
  { id: 2, name: 'Bob Smith',     email: 'bob@school.edu',   role: 'Teacher', borrows: 2, active: true  },
  { id: 3, name: 'Carol White',   email: 'carol@school.edu', role: 'Staff',   borrows: 0, active: false },
  { id: 4, name: 'David Lee',     email: 'david@school.edu', role: 'Student', borrows: 1, active: true  },
  { id: 5, name: 'Eva Green',     email: 'eva@school.edu',   role: 'Teacher', borrows: 0, active: true  },
])

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

function updateReqStatus(id, status) {
  const req = requests.value.find(r => r.id === id)
  if (req) req.status = status
}

let nextId = 9
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

function saveItem(form) {
  if (editingItemObj.value) {
    const idx = items.value.findIndex(i => i.id === editingItemObj.value.id)
    if (idx !== -1) items.value[idx] = { ...items.value[idx], ...form }
  } else {
    items.value.push({
      id: nextId++,
      ...form,
      emoji: EMOJI_MAP[form.category] || '📦',
    })
  }
  editingItemObj.value = null
}

function deleteItem(id) {
  if (confirm('Delete this item?')) {
    items.value = items.value.filter(i => i.id !== id)
  }
}

function saveUser(form) {
  users.value.push({ id: nextId++, ...form, borrows: 0, active: true })
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