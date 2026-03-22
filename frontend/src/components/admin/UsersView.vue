<template>
  <div class="view-section">
    <div class="section-header">
      <h2>👥 Users</h2>
      <button class="add-btn" @click="$emit('openAddUser')">+ Add User</button>
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
                <button class="act-btn act-delete" @click="$emit('deleteUser', user.id)" title="Remove">🗑️</button>
              </div>
            </td>
          </tr>
          <tr v-if="users.length === 0">
            <td colspan="6" class="empty-state">No users found</td>
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

defineEmits(['openAddUser', 'deleteUser'])
</script>