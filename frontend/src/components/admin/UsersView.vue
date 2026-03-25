<template>
  <div class="view-section">
    <div class="section-header">
      <div>
        <h2 class="section-title">Users</h2>
        <p class="section-sub">Manage registered users and their access</p>
      </div>
      <button class="add-btn" @click="$emit('openAddUser')">+ Add User</button>
    </div>

    <div class="panel">
      <div v-if="users.length === 0" class="empty-state">No users found.</div>
      <table v-else class="data-table">
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
                <option value="USER">USER</option>
                <option value="ADMIN">ADMIN</option>
              </select>
            </td>
            <td class="center-cell">{{ user.borrows }}</td>
            <td>
              <span class="status-badge" :class="user.active ? 'status-Available' : 'status-Retired'">
                {{ user.active ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td>
              <div class="action-btns">
                <button class="act-btn act-delete" @click="$emit('deleteUser', user.id)">Remove</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
defineProps({
  users: Array,
})

defineEmits(['openAddUser', 'deleteUser', 'changeRole'])
</script>
