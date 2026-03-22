<template>
  <div class="app-shell">
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <span class="sidebar-logo-text">Equip<span>Pro</span></span>
        <button class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          {{ sidebarCollapsed ? '▶' : '◀' }}
        </button>
      </div>
      <nav class="sidebar-nav">
        <button
          v-for="nav in navItems"
          :key="nav.view"
          class="nav-item"
          :class="{ active: currentView === nav.view }"
          @click="currentView = nav.view"
        >
          <span class="nav-icon">{{ nav.icon }}</span>
          <span class="nav-label">{{ nav.label }}</span>
        </button>
      </nav>
      <div class="sidebar-footer">
        <div class="user-chip">
          <div class="user-avatar">AD</div>
          <div class="user-info">
            <span class="user-name">Admin User</span>
            <span class="user-role">Administrator</span>
          </div>
        </div>
        <button class="logout-btn" @click="logout" title="Logout">↩</button>
      </div>
    </aside>
    <div class="main-content">
      <div class="topbar">
        <div class="search-bar">
          <span class="search-icon">🔍</span>
          <input
            class="search-input"
            type="text"
            placeholder="Search equipment..."
            v-model="searchQuery"
          />
        </div>
        <div class="topbar-right">
          <span class="topbar-date">{{ todayDate }}</span>
          <div class="welcome-chip">
            <span class="welcome-text">Welcome, <strong>Admin</strong></span>
            <div class="admin-avatar">AD</div>
          </div>
        </div>
      </div>
      <div v-if="currentView === 'dashboard'" class="view-section">
        <div class="stats-grid">
          <div class="stat-card stat-blue">
            <div class="stat-icon-wrap">💻</div>
            <div class="stat-body">
              <span class="stat-value">{{ items.length }}</span>
              <span class="stat-label">Total Items</span>
            </div>
          </div>
          <div class="stat-card stat-green">
            <div class="stat-icon-wrap">✅</div>
            <div class="stat-body">
              <span class="stat-value">{{ availableCount }}</span>
              <span class="stat-label">Available</span>
            </div>
          </div>
          <div class="stat-card stat-orange">
            <div class="stat-icon-wrap">⏳</div>
            <div class="stat-body">
              <span class="stat-value">{{ pendingCount }}</span>
              <span class="stat-label">Pending Requests</span>
            </div>
          </div>
          <div class="stat-card stat-red">
            <div class="stat-icon-wrap">🔧</div>
            <div class="stat-body">
              <span class="stat-value">{{ repairCount }}</span>
              <span class="stat-label">Under Repair</span>
            </div>
          </div>
        </div>

        <div class="overview-bottom">
          <div class="panel full-panel">
            <div class="panel-header">
              <h3>Inventory Overview</h3>
              <button class="add-btn" @click="openAddModal">+ Add New Item</button>
            </div>
            <table class="data-table">
              <thead>
                <tr>
                  <th>Item Name</th>
                  <th>Category</th>
                  <th>Serial Number</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in items.slice(0, 6)" :key="item.id">
                  <td>
                    <div class="item-name-cell">
                      <span class="item-emoji">{{ item.emoji }}</span>
                      {{ item.name }}
                    </div>
                  </td>
                  <td>{{ item.category }}</td>
                  <td class="serial-cell">{{ item.serial }}</td>
                  <td><span class="status-badge" :class="statusClass(item.status)">{{ item.status.replace(/-/g, ' ') }}</span></td>
                  <td>
                    <div class="action-btns">
                      <button class="act-btn act-edit" @click="editItem(item)" title="Edit">✏️</button>
                      <button class="act-btn act-delete" @click="deleteItem(item.id)" title="Delete">🗑️</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="panel">
            <div class="panel-header">
              <h3>Recent Requests</h3>
              <button class="link-btn" @click="currentView = 'requests'">View all</button>
            </div>
            <div class="borrow-list">
              <div class="borrow-row" v-for="req in requests.slice(0, 4)" :key="req.id">
                <div class="borrow-info">
                  <span class="borrow-name">{{ req.user }} — {{ req.item }}</span>
                  <span class="borrow-date">Requested {{ req.requested }} · Return by {{ req.returnBy }}</span>
                </div>
                <span class="status-badge" :class="statusClass(req.status)">{{ req.status }}</span>
              </div>
            </div>
          </div>
          <div class="panel">
            <div class="panel-header"><h3>Category Breakdown</h3></div>
            <div class="category-list">
              <div v-for="(count, cat) in categoryBreakdown" :key="cat" class="cat-row">
                <div class="cat-header">
                  <span>{{ cat }}</span>
                  <span>{{ count }} items</span>
                </div>
                <div class="cat-bar-bg">
                  <div class="cat-bar-fill" :style="{ width: (count / items.length * 100) + '%' }"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-if="currentView === 'inventory'" class="view-section">
        <div class="section-header">
          <h2>📦 Inventory Management</h2>
          <button class="add-btn" @click="openAddModal">+ Add New Item</button>
        </div>
        <div class="filter-bar">
          <select class="filter-select" v-model="filterCategory">
            <option value="">All Categories</option>
            <option value="Electronics">Electronics</option>
            <option value="Books">Books</option>
            <option value="Tools">Tools</option>
            <option value="Furniture">Furniture</option>
          </select>
          <select class="filter-select" v-model="filterStatus">
            <option value="">All Statuses</option>
            <option value="Available">Available</option>
            <option value="Checked-Out">Checked Out</option>
            <option value="Under-Repair">Under Repair</option>
            <option value="Retired">Retired</option>
          </select>
        </div>
        <div class="panel">
          <table class="data-table">
            <thead>
              <tr>
                <th>Item Name</th>
                <th>Category</th>
                <th>Serial Number</th>
                <th>Status</th>
                <th>Condition</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in filteredItems" :key="item.id">
                <td>
                  <div class="item-name-cell">
                    <span class="item-emoji">{{ item.emoji }}</span>
                    {{ item.name }}
                  </div>
                </td>
                <td>{{ item.category }}</td>
                <td class="serial-cell">{{ item.serial }}</td>
                <td><span class="status-badge" :class="statusClass(item.status)">{{ item.status.replace(/-/g, ' ') }}</span></td>
                <td><span class="cond-badge" :class="'cond-' + item.condition">{{ item.condition }}</span></td>
                <td>
                  <div class="action-btns">
                    <button class="act-btn act-edit" @click="editItem(item)" title="Edit">✏️</button>
                    <button class="act-btn act-delete" @click="deleteItem(item.id)" title="Delete">🗑️</button>
                  </div>
                </td>
              </tr>
              <tr v-if="filteredItems.length === 0">
                <td colspan="6" class="empty-state">No items found</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div v-if="currentView === 'requests'" class="view-section">
        <div class="section-header">
          <h2>📋 Borrow Requests</h2>
        </div>
        <div class="req-tabs">
          <button
            v-for="tab in ['all','pending','approved','rejected']"
            :key="tab"
            class="tab-btn"
            :class="{ active: reqFilter === tab }"
            @click="reqFilter = tab"
          >{{ tab.charAt(0).toUpperCase() + tab.slice(1) }}</button>
        </div>
        <div class="panel">
          <table class="data-table">
            <thead>
              <tr>
                <th>User</th>
                <th>Item</th>
                <th>Requested</th>
                <th>Return By</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="req in filteredRequests" :key="req.id">
                <td style="font-weight:600">{{ req.user }}</td>
                <td>{{ req.item }}</td>
                <td class="date-cell">{{ req.requested }}</td>
                <td class="date-cell">{{ req.returnBy }}</td>
                <td><span class="status-badge" :class="statusClass(req.status)">{{ req.status }}</span></td>
                <td>
                  <div class="action-btns" v-if="req.status === 'pending'">
                    <button class="act-btn act-approve" @click="updateReqStatus(req.id, 'approved')" title="Approve">✅</button>
                    <button class="act-btn act-delete" @click="updateReqStatus(req.id, 'rejected')" title="Reject">❌</button>
                  </div>
                  <span v-else class="empty-state" style="padding:0">—</span>
                </td>
              </tr>
              <tr v-if="filteredRequests.length === 0">
                <td colspan="6" class="empty-state">No requests found</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div v-if="currentView === 'users'" class="view-section">
        <div class="section-header">
          <h2>👥 Users</h2>
          <button class="add-btn" @click="showAddUserModal = true">+ Add User</button>
        </div>
        <div class="panel">
          <table class="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Active Borrows</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in users" :key="user.id">
                <td>
                  <div class="item-name-cell">
                    <div class="user-initials" :class="{ inactive: !user.active }">
                      {{ user.name.split(' ').map(n => n[0]).join('') }}
                    </div>
                    {{ user.name }}
                  </div>
                </td>
                <td class="date-cell">{{ user.email }}</td>
                <td><span class="role-badge">{{ user.role }}</span></td>
                <td style="text-align:center">{{ user.borrows }}</td>
                <td>
                  <span class="status-badge" :class="user.active ? 'status-Available' : 'status-Retired'">
                    {{ user.active ? 'Active' : 'Inactive' }}
                  </span>
                </td>
                <td>
                  <div class="action-btns">
                    <button class="act-btn act-edit" title="Edit">✏️</button>
                    <button class="act-btn act-delete" @click="deleteUser(user.id)" title="Remove">🗑️</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div v-if="currentView === 'settings'" class="view-section">
        <div class="section-header" style="margin-bottom:24px">
          <h2>⚙️ Settings</h2>
        </div>
        <div class="settings-grid">
          <!-- Notifications -->
          <div class="settings-card">
            <h3>Notifications</h3>
            <div class="setting-row" v-for="s in settings.notifications" :key="s.key">
              <div>
                <div class="setting-label">{{ s.label }}</div>
                <div class="setting-sub">{{ s.sub }}</div>
              </div>
              <button class="toggle" :class="{ on: s.value }" @click="s.value = !s.value"></button>
            </div>
          </div>
          <!-- System -->
          <div class="settings-card">
            <h3>System</h3>
            <div class="setting-row" v-for="s in settings.system" :key="s.key">
              <div>
                <div class="setting-label">{{ s.label }}</div>
                <div class="setting-sub">{{ s.sub }}</div>
              </div>
              <button class="toggle" :class="{ on: s.value }" @click="s.value = !s.value"></button>
            </div>
          </div>
          <!-- Profile -->
          <div class="settings-card">
            <h3>Profile</h3>
            <div class="input-field">
              <label>Display Name</label>
              <input type="text" v-model="settings.profile.name" />
            </div>
            <div class="input-field">
              <label>Email</label>
              <input type="email" v-model="settings.profile.email" />
            </div>
            <div class="input-field">
              <label>New Password</label>
              <input type="password" placeholder="••••••••" />
            </div>
            <button class="primary-btn">Save Changes</button>
          </div>
          <!-- Borrow Policy -->
          <div class="settings-card">
            <h3>Borrow Policy</h3>
            <div class="input-field">
              <label>Max Borrow Days</label>
              <input type="number" v-model="settings.policy.maxDays" />
            </div>
            <div class="input-field">
              <label>Max Items Per User</label>
              <input type="number" v-model="settings.policy.maxItems" />
            </div>
            <div class="input-field">
              <label>Late Fee (per day)</label>
              <input type="text" v-model="settings.policy.lateFee" />
            </div>
            <button class="primary-btn">Update Policy</button>
          </div>
        </div>
      </div>

    </div>
    <Teleport to="body">
      <div class="modal-overlay" :class="{ open: showAddModal }" @click.self="showAddModal = false">
        <div class="modal-box">
          <div class="modal-title">➕ {{ editingItem ? 'Edit Item' : 'Add New Item' }}</div>
          <div class="input-field">
            <label>Item Name</label>
            <input type="text" v-model="itemForm.name" placeholder="e.g. Dell XPS Laptop" />
          </div>
          <div class="input-field">
            <label>Category</label>
            <select v-model="itemForm.category">
              <option>Electronics</option>
              <option>Books</option>
              <option>Tools</option>
              <option>Furniture</option>
            </select>
          </div>
          <div class="input-field">
            <label>Serial Number</label>
            <input type="text" v-model="itemForm.serial" placeholder="e.g. SN-XPS-001" />
          </div>
          <div class="input-field">
            <label>Status</label>
            <select v-model="itemForm.status">
              <option value="Available">Available</option>
              <option value="Checked-Out">Checked Out</option>
              <option value="Under-Repair">Under Repair</option>
              <option value="Retired">Retired</option>
            </select>
          </div>
          <div class="input-field">
            <label>Condition</label>
            <select v-model="itemForm.condition">
              <option value="excellent">Excellent</option>
              <option value="good">Good</option>
              <option value="fair">Fair</option>
              <option value="poor">Poor</option>
            </select>
          </div>
          <div class="modal-actions">
            <button class="primary-btn" @click="saveItem">
              {{ editingItem ? 'Save Changes' : 'Add Item' }}
            </button>
            <button class="cancel-btn" @click="closeItemModal">Cancel</button>
          </div>
        </div>
      </div>
    </Teleport>
    <Teleport to="body">
      <div class="modal-overlay" :class="{ open: showAddUserModal }" @click.self="showAddUserModal = false">
        <div class="modal-box">
          <div class="modal-title">👤 Add New User</div>
          <div class="input-field">
            <label>Full Name</label>
            <input type="text" v-model="userForm.name" placeholder="e.g. Jane Doe" />
          </div>
          <div class="input-field">
            <label>Email</label>
            <input type="email" v-model="userForm.email" placeholder="jane@example.com" />
          </div>
          <div class="input-field">
            <label>Role</label>
            <select v-model="userForm.role">
              <option>Staff</option>
              <option>Student</option>
              <option>Teacher</option>
              <option>Admin</option>
            </select>
          </div>
          <div class="modal-actions">
            <button class="primary-btn" @click="saveUser">Add User</button>
            <button class="cancel-btn" @click="showAddUserModal = false">Cancel</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>


<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
function logout() {
  localStorage.removeItem('invtr_token')
  sessionStorage.removeItem('invtr_token')
  router.push('/login')
}
import { ref, computed, reactive } from 'vue'
const currentView = ref('dashboard')
const sidebarCollapsed = ref(false)
const searchQuery = ref('')
const navItems = [
  { view: 'dashboard', icon: '📊', label: 'Dashboard' },
  { view: 'inventory', icon: '📦', label: 'Inventory' },
  { view: 'requests',  icon: '📋', label: 'Requests' },
  { view: 'users',     icon: '👥', label: 'Users' },
  { view: 'settings',  icon: '⚙️',  label: 'Settings' },
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
    { key: 'email',  label: 'Email Notifications', sub: 'Send emails on new requests',    value: true  },
    { key: 'overdue',label: 'Overdue Alerts',       sub: 'Notify when items are overdue',  value: true  },
    { key: 'repair', label: 'Repair Reminders',     sub: 'Remind about items under repair',value: false },
  ],
  system: [
    { key: 'dark',   label: 'Dark Mode',            sub: 'Switch interface theme',          value: false },
    { key: 'auto',   label: 'Auto-approve Requests',sub: 'Skip manual approval step',       value: false },
    { key: 'maint',  label: 'Maintenance Mode',     sub: 'Disable user access temporarily', value: false },
  ],
  profile: { name: 'Admin User', email: 'admin@equipro.com' },
  policy:  { maxDays: 14, maxItems: 3, lateFee: '$2.00' },
})
const availableCount  = computed(() => items.value.filter(i => i.status === 'Available').length)
const pendingCount    = computed(() => requests.value.filter(r => r.status === 'pending').length)
const repairCount     = computed(() => items.value.filter(i => i.status === 'Under-Repair').length)
const categoryBreakdown = computed(() => {
  return items.value.reduce((acc, item) => {
    acc[item.category] = (acc[item.category] || 0) + 1
    return acc
  }, {})
})
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
function statusClass(status) {
  return 'status-' + (status || '').replace(/\s+/g, '-')
}
let nextId = 9
const showAddModal = ref(false)
const editingItem  = ref(null)
const itemForm     = reactive({ name: '', category: 'Electronics', serial: '', status: 'Available', condition: 'excellent' })
function openAddModal() {
  editingItem.value = null
  Object.assign(itemForm, { name: '', category: 'Electronics', serial: '', status: 'Available', condition: 'excellent' })
  showAddModal.value = true
}
function editItem(item) {
  editingItem.value = item.id
  Object.assign(itemForm, { ...item })
  showAddModal.value = true
}
function saveItem() {
  if (!itemForm.name.trim() || !itemForm.serial.trim()) {
    alert('Please fill in name and serial number.')
    return
  }
  if (editingItem.value) {
    const idx = items.value.findIndex(i => i.id === editingItem.value)
    if (idx !== -1) items.value[idx] = { ...items.value[idx], ...itemForm }
  } else {
    items.value.push({
      id: nextId++,
      ...itemForm,
      emoji: EMOJI_MAP[itemForm.category] || '📦',
    })
  }
  closeItemModal()
}
function closeItemModal() {
  showAddModal.value = false
  editingItem.value  = null
}
function deleteItem(id) {
  if (confirm('Delete this item?')) {
    items.value = items.value.filter(i => i.id !== id)
  }
}
function updateReqStatus(id, status) {
  const req = requests.value.find(r => r.id === id)
  if (req) req.status = status
}
const showAddUserModal = ref(false)
const userForm = reactive({ name: '', email: '', role: 'Staff' })
function saveUser() {
  if (!userForm.name.trim() || !userForm.email.trim()) {
    alert('Please fill in name and email.')
    return
  }
  users.value.push({ id: nextId++, ...userForm, borrows: 0, active: true })
  Object.assign(userForm, { name: '', email: '', role: 'Staff' })
  showAddUserModal.value = false
}
function deleteUser(id) {
  if (confirm('Remove this user?')) {
    users.value = users.value.filter(u => u.id !== id)
  }
}
</script>


<style scoped>
@import url('https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&display=swap');
* { box-sizing: border-box; margin: 0; padding: 0; }
.app-shell {
  display: flex;
  min-height: 100vh;
  background: #f0f4f8;
  font-family: 'DM Sans', sans-serif;
}
/* ── SIDEBAR ── */
.sidebar {
  width: 240px;
  background: #103852;
  display: flex;
  flex-direction: column;
  transition: width 0.25s ease;
  overflow: hidden;
  flex-shrink: 0;
}
.sidebar.collapsed { width: 68px; }
.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 16px 16px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.sidebar-logo-text { color: #fff; font-size: 18px; font-weight: 700; white-space: nowrap; }
.sidebar-logo-text span { color: #52d265; }
.sidebar.collapsed .sidebar-logo-text { display: none; }
.collapse-btn {
  background: rgba(255,255,255,0.1);
  border: none; color: #fff;
  width: 28px; height: 28px;
  border-radius: 6px; cursor: pointer;
  font-size: 13px; display: flex;
  align-items: center; justify-content: center;
  flex-shrink: 0; transition: background 0.2s;
}
.collapse-btn:hover { background: rgba(255,255,255,0.2); }
.sidebar-nav { flex: 1; padding: 12px 8px; display: flex; flex-direction: column; gap: 4px; }
.nav-item {
  display: flex; align-items: center; gap: 12px;
  padding: 10px 12px; border: none;
  background: transparent; color: rgba(255,255,255,0.6);
  border-radius: 10px; cursor: pointer;
  font-family: 'DM Sans', sans-serif; font-size: 14px; font-weight: 500;
  text-align: left; white-space: nowrap;
  transition: background 0.2s, color 0.2s; width: 100%;
}
.nav-item:hover { background: rgba(255,255,255,0.08); color: #fff; }
.nav-item.active { background: rgba(82,210,101,0.18); color: #52d265; font-weight: 700; }
.nav-icon { font-size: 18px; flex-shrink: 0; }
.sidebar.collapsed .nav-label { display: none; }
.sidebar-footer {
  padding: 16px 10px;
  border-top: 1px solid rgba(255,255,255,0.08);
  display: flex; align-items: center; gap: 8px;
}
.user-chip { display: flex; align-items: center; gap: 10px; flex: 1; min-width: 0; }
.sidebar.collapsed .user-info { display: none; }
.user-avatar {
  width: 34px; height: 34px; background: #52d265; color: #103852;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 13px; font-weight: 700; flex-shrink: 0;
}
.user-info { display: flex; flex-direction: column; min-width: 0; }
.user-name { color: #fff; font-size: 13px; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.user-role { color: #52d265; font-size: 11px; }
.logout-btn {
  background: rgba(255,255,255,0.08); border: none;
  color: rgba(255,255,255,0.5); width: 32px; height: 32px;
  border-radius: 8px; cursor: pointer; font-size: 15px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; transition: background 0.2s, color 0.2s;
}
.logout-btn:hover { background: rgba(231,76,60,0.3); color: #e74c3c; }

/* ── MAIN ── */
.main-content { flex: 1; display: flex; flex-direction: column; min-width: 0; overflow-y: auto; }
.topbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 20px 28px; background: #fff;
  border-bottom: 1px solid #e8edf2; gap: 16px; flex-wrap: wrap;
}
.topbar-right { display: flex; align-items: center; gap: 16px; }
.topbar-date { font-size: 13px; color: #8a9ab0; white-space: nowrap; }
.search-bar {
  display: flex; align-items: center; background: #f0f4f8;
  border-radius: 10px; padding: 8px 14px; gap: 8px;
}
.search-icon { font-size: 14px; color: #8a9ab0; }
.search-input {
  border: none; background: transparent;
  font-family: 'DM Sans', sans-serif; font-size: 14px;
  color: #103852; width: 200px; outline: none;
}
.search-input::placeholder { color: #b0bbc8; }
.welcome-chip { display: flex; align-items: center; gap: 10px; }
.welcome-text { font-size: 14px; color: #8a9ab0; }
.welcome-text strong { color: #103852; }
.admin-avatar {
  width: 36px; height: 36px;
  background: linear-gradient(135deg, #52d265, #103852);
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 13px; font-weight: 700; color: #fff; border: 2px solid #52d265;
}
.view-section { padding: 24px 28px; }
.section-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;
}
.section-header h2 { font-size: 18px; font-weight: 700; color: #103852; }
.stats-grid { display: grid; grid-template-columns: repeat(4,1fr); gap: 16px; margin-bottom: 24px; }
.stat-card {
  background: #fff; border-radius: 14px; padding: 20px 22px;
  display: flex; align-items: center; gap: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  border-left: 4px solid transparent;
  transition: box-shadow 0.2s, transform 0.2s;
}
.stat-card:hover { box-shadow: 0 6px 20px rgba(16,56,82,0.1); transform: translateY(-2px); }
.stat-blue   { border-color: #3b82f6; }
.stat-orange { border-color: #f59e0b; }
.stat-green  { border-color: #52d265; }
.stat-red    { border-color: #ef4444; }
.stat-icon-wrap {
  width: 52px; height: 52px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; font-size: 24px; flex-shrink: 0;
}
.stat-blue  .stat-icon-wrap { background: #eff6ff; }
.stat-orange .stat-icon-wrap { background: #fffbeb; }
.stat-green .stat-icon-wrap { background: #f0fdf4; }
.stat-red   .stat-icon-wrap { background: #fef2f2; }
.stat-body { display: flex; flex-direction: column; }
.stat-value { font-size: 30px; font-weight: 700; color: #103852; line-height: 1; }
.stat-label { font-size: 13px; color: #8a9ab0; margin-top: 4px; font-weight: 500; }
.overview-bottom { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.panel { background: #fff; border-radius: 14px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.full-panel { grid-column: 1 / -1; }
.panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.panel-header h3 { font-size: 15px; font-weight: 700; color: #103852; }
.link-btn {
  background: none; border: none; color: #52d265;
  font-size: 13px; font-weight: 600; cursor: pointer; font-family: 'DM Sans', sans-serif;
}
.link-btn:hover { text-decoration: underline; }
.category-list { display: flex; flex-direction: column; gap: 12px; }
.cat-row {}
.cat-header { display: flex; justify-content: space-between; font-size: 13px; font-weight: 600; color: #103852; margin-bottom: 5px; }
.cat-bar-bg { background: #f0f4f8; border-radius: 6px; height: 8px; }
.cat-bar-fill { background: #52d265; height: 8px; border-radius: 6px; transition: width 0.5s ease; }

/* ── ADD BTN ── */
.add-btn {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 18px; background: #52d265; color: #103852;
  border: none; border-radius: 10px; font-family: 'DM Sans', sans-serif;
  font-size: 14px; font-weight: 700; cursor: pointer; transition: background 0.2s, transform 0.15s;
}
.add-btn:hover { background: #3dbf54; transform: translateY(-1px); }

/* ── TABLE ── */
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th {
  text-align: left; padding: 10px 14px; font-size: 11px;
  font-weight: 700; letter-spacing: 0.06em; text-transform: uppercase;
  color: #8a9ab0; border-bottom: 2px solid #e8edf2;
}
.data-table td { padding: 13px 14px; color: #1a2d3e; border-bottom: 1px solid #f0f4f8; }
.data-table tr:last-child td { border-bottom: none; }
.data-table tr:hover td { background: #f8fafc; }

.item-name-cell { display: flex; align-items: center; gap: 10px; font-weight: 600; }
.item-emoji { font-size: 18px; }
.serial-cell { font-family: monospace; font-size: 12px; color: #8a9ab0; }
.date-cell { color: #8a9ab0; font-size: 13px; }

.action-btns { display: flex; gap: 6px; }
.act-btn {
  width: 32px; height: 32px; border: none; border-radius: 8px;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  font-size: 14px; transition: background 0.2s;
}
.act-edit   { background: #f0f4f8; color: #103852; }
.act-edit:hover { background: #e0eaf3; }
.act-delete { background: #fff0f0; color: #e74c3c; }
.act-delete:hover { background: #fdd; }
.act-approve { background: #e6faf0; color: #22863a; }
.act-approve:hover { background: #d0f0dc; }

/* ── BORROW LIST ── */
.borrow-list { display: flex; flex-direction: column; gap: 10px; }
.borrow-row {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 14px; background: #f8fafc; border-radius: 10px; transition: background 0.2s;
}
.borrow-row:hover { background: #f0f4f8; }
.borrow-info { display: flex; flex-direction: column; }
.borrow-name { font-size: 14px; font-weight: 600; color: #103852; }
.borrow-date { font-size: 12px; color: #8a9ab0; margin-top: 2px; }

/* ── FILTER BAR ── */
.filter-bar { display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap; }
.filter-select {
  padding: 9px 14px; border: 1.5px solid #e3e8ef; border-radius: 9px;
  font-family: 'DM Sans', sans-serif; font-size: 14px; color: #103852;
  background: #fff; cursor: pointer; outline: none;
}
.filter-select:focus { border-color: #103852; }

/* ── REQUEST TABS ── */
.req-tabs { display: flex; gap: 8px; margin-bottom: 20px; }
.tab-btn {
  padding: 8px 18px; border: none; border-radius: 20px;
  font-family: 'DM Sans', sans-serif; font-size: 13px; font-weight: 600;
  cursor: pointer; background: #fff; color: #8a9ab0;
  transition: background 0.2s, color 0.2s; box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.tab-btn.active { background: #103852; color: #fff; }
.tab-btn:hover:not(.active) { background: #f0f4f8; color: #103852; }

/* ── USERS ── */
.user-initials {
  width: 32px; height: 32px; background: #103852; color: #52d265;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 700; flex-shrink: 0;
}
.user-initials.inactive { background: #e3e8ef; color: #8a9ab0; }
.role-badge {
  font-size: 12px; font-weight: 600; padding: 3px 10px;
  border-radius: 20px; background: #f0f4f8; color: #103852;
}
.status-badge {
  font-size: 11px; font-weight: 700; padding: 4px 10px;
  border-radius: 20px; white-space: nowrap; display: inline-block;
}
.status-Available  { background: #e6faf0; color: #22863a; }
.status-Checked-Out{ background: #e0f0ff; color: #1d6fa6; }
.status-Under-Repair{ background: #fff8e6; color: #b45309; }
.status-Retired    { background: #f0f0f0; color: #666; }
.status-pending    { background: #fff8e6; color: #b45309; }
.status-approved   { background: #e6faf0; color: #22863a; }
.status-rejected   { background: #fff0f0; color: #c0392b; }
.cond-badge { font-size: 11px; font-weight: 700; padding: 3px 9px; border-radius: 20px; text-transform: capitalize; }
.cond-excellent { background: #e6faf0; color: #22863a; }
.cond-good      { background: #e0f0ff; color: #1d6fa6; }
.cond-fair      { background: #fff8e6; color: #b45309; }
.cond-poor      { background: #fff0f0; color: #c0392b; }

/* ── SETTINGS ── */
.settings-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.settings-card { background: #fff; border-radius: 14px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.settings-card h3 { font-size: 15px; font-weight: 700; color: #103852; margin-bottom: 16px; }
.setting-row {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 0; border-bottom: 1px solid #f0f4f8;
}
.setting-row:last-child { border-bottom: none; }
.setting-label { font-size: 14px; color: #1a2d3e; font-weight: 500; }
.setting-sub   { font-size: 12px; color: #8a9ab0; margin-top: 2px; }

.toggle {
  width: 42px; height: 24px; background: #e3e8ef;
  border-radius: 12px; position: relative; cursor: pointer; border: none;
  transition: background 0.2s; flex-shrink: 0;
}
.toggle.on { background: #52d265; }
.toggle::after {
  content: ''; position: absolute;
  width: 18px; height: 18px; background: #fff;
  border-radius: 50%; top: 3px; left: 3px;
  transition: left 0.2s; box-shadow: 0 1px 4px rgba(0,0,0,0.2);
}
.toggle.on::after { left: 21px; }
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.5);
  display: flex; justify-content: center; align-items: center;
  z-index: 1000; backdrop-filter: blur(4px);
  opacity: 0; pointer-events: none; transition: opacity 0.2s;
}
.modal-overlay.open { opacity: 1; pointer-events: all; }
.modal-box {
  background: #fff; padding: 32px; border-radius: 18px;
  width: 90%; max-width: 440px; box-shadow: 0 20px 60px rgba(0,0,0,0.25);
  transform: translateY(20px); transition: transform 0.25s ease;
}
.modal-overlay.open .modal-box { transform: translateY(0); }
.modal-title { font-size: 18px; font-weight: 700; color: #103852; margin-bottom: 20px; }

.input-field { margin-bottom: 14px; }
.input-field label {
  display: block; font-size: 11px; font-weight: 700;
  letter-spacing: 0.08em; text-transform: uppercase; color: #7a8a9a; margin-bottom: 6px;
}
.input-field input,
.input-field select {
  width: 100%; padding: 11px 13px; border: 1.5px solid #e3e8ef;
  border-radius: 9px; font-family: 'DM Sans', sans-serif;
  font-size: 14px; color: #1a2d3e; outline: none; background: #fff; transition: border-color 0.2s;
}
.input-field input:focus,
.input-field select:focus { border-color: #103852; box-shadow: 0 0 0 3px rgba(16,56,82,0.08); }

.modal-actions { display: flex; gap: 10px; margin-top: 20px; }
.primary-btn {
  flex: 1; padding: 13px; background: #103852; color: #fff; border: none;
  border-radius: 10px; font-family: 'DM Sans', sans-serif;
  font-size: 15px; font-weight: 700; cursor: pointer; transition: background 0.2s;
}
.primary-btn:hover { background: #0a2538; }
.cancel-btn {
  flex: 1; padding: 12px; background: #f0f4f8; color: #8a9ab0; border: none;
  border-radius: 10px; font-family: 'DM Sans', sans-serif;
  font-size: 14px; font-weight: 600; cursor: pointer; transition: background 0.2s;
}
.cancel-btn:hover { background: #e3e8ef; }

.empty-state { text-align: center; color: #b0bbc8; font-size: 14px; padding: 20px 0; }

@media (max-width: 900px) {
  .stats-grid { grid-template-columns: repeat(2,1fr); }
  .overview-bottom { grid-template-columns: 1fr; }
  .settings-grid { grid-template-columns: 1fr; }
}
@media (max-width: 600px) {
  .stats-grid { grid-template-columns: 1fr 1fr; }
  .view-section { padding: 16px; }
  .topbar { padding: 14px 16px; }
}
</style>
