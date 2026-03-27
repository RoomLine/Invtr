<template>
  <div class="view-section">
    <div class="section-header">
      <div>
        <h2 class="section-title">{{ $t('navigation.users') }}</h2>
        <p class="section-sub">{{ $t('users.subTitle') }}</p>
      </div>
<<<<<<< HEAD
      <div style="display:flex;gap:10px;align-items:center;">
        <div class="search-bar">
          <span class="search-icon">🔍</span>
          <input class="search-input" type="text" v-model="userSearch" :placeholder="$t('auth.firstName')" />
        </div>
        <button class="add-btn" @click="$emit('openAddUser')">+ {{ $t('users.addNew') }}</button>
      </div>
    </div>

    <div class="panel">
      <div v-if="filteredUsers.length === 0" class="empty-state">{{ $t('users.noUsers') }}</div>
=======
      <button class="add-btn" @click="$emit('openAddUser')">+ {{ $t('users.addNew') }}</button>
    </div>

    <div class="panel">
      <div v-if="users.length === 0" class="empty-state">{{ $t('users.noUsers') }}</div>
>>>>>>> origin/main
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>{{ $t('auth.firstName') }}</th>
            <th>{{ $t('auth.email') }}</th>
            <th>{{ $t('users.role') }}</th>
            <th>{{ $t('dashboard.activeBorrows') }}</th>
            <th>{{ $t('inventory.status') }}</th>
<<<<<<< HEAD
            <th>{{ $t('users.exportHistory') }}</th>
=======
>>>>>>> origin/main
            <th>{{ $t('common.active') }}</th>
          </tr>
        </thead>
        <tbody>
<<<<<<< HEAD
          <tr v-for="user in filteredUsers" :key="user.id">
=======
          <tr v-for="user in users" :key="user.id">
>>>>>>> origin/main
            <td>
              <div class="item-name-cell">
                <div class="user-initials" :class="{ 'user-initials-admin': user.role !== 'USER' }">
                  {{ user.name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase() }}
                </div>
                <span>{{ user.name }}</span>
              </div>
            </td>
            <td class="email-cell">{{ user.email }}</td>
            <td>
              <select
                class="role-select"
                :value="user.role"
                @change="$emit('changeRole', { userId: user.id, role: $event.target.value })"
              >
                <option value="USER">{{ $t('users.roles.user') }}</option>
                <option value="ADMIN">{{ $t('users.roles.admin') }}</option>
              </select>
            </td>
            <td class="center-cell">{{ user.borrows }}</td>
            <td>
              <span class="status-badge" :class="user.active ? 'status-Available' : 'status-Retired'">
                {{ user.active ? $t('common.activeStatus') : $t('common.inactive') }}
              </span>
            </td>
<<<<<<< HEAD
            <!-- Per-user history export -->
            <td>
              <div class="export-group">
                <button
                  class="export-btn"
                  :disabled="exportingId === user.id + '_CSV'"
                  @click="exportHistory(user, 'CSV')"
                  title="Export borrowing history as CSV"
                >
                  {{ exportingId === user.id + '_CSV' ? '...' : '⬇ CSV' }}
                </button>
                <button
                  class="export-btn"
                  :disabled="exportingId === user.id + '_XLSX'"
                  @click="exportHistory(user, 'XLSX')"
                  title="Export borrowing history as Excel"
                >
                  {{ exportingId === user.id + '_XLSX' ? '...' : '⬇ XLSX' }}
                </button>
              </div>
            </td>
=======
>>>>>>> origin/main
            <td>
              <div class="action-btns">
                <button class="act-btn act-delete" @click="$emit('deleteUser', user.id)">
                  {{ $t('common.remove') }}
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
<<<<<<< HEAD
import { ref, computed } from 'vue'

const props = defineProps({
  users: Array,
})

const userSearch = ref('')
const filteredUsers = computed(() => {
  const q = userSearch.value.toLowerCase()
  return q ? props.users.filter(u => u.name.toLowerCase().includes(q)) : props.users
})

defineEmits(['openAddUser', 'deleteUser', 'changeRole'])

const exportingId = ref(null)

async function exportHistory(user, format) {
  const key = user.id + '_' + format
  exportingId.value = key
  try {
    const token = localStorage.getItem('invtr_token') || sessionStorage.getItem('invtr_token')
    const res = await fetch(`/reports/history/${user.id}?format=${format}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (!res.ok) {
      exportingId.value = null
      return
    }
    const blob = await res.blob()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    // Filename: e.g. "Иван_Иванов_history.csv"
    const safeName = (user.name || 'user').replace(/\s+/g, '_')
    a.download = format === 'CSV' ? `${safeName}_history.csv` : `${safeName}_history.xlsx`
    a.click()
    URL.revokeObjectURL(url)
  } catch (_) {}
  exportingId.value = null
}
</script>
=======
defineProps({
  users: Array,
})

defineEmits(['openAddUser', 'deleteUser', 'changeRole'])
</script>
>>>>>>> origin/main
