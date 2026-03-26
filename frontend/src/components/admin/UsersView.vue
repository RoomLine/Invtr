<template>
  <div class="view-section">
    <div class="section-header">
      <div>
        <h2 class="section-title">{{ $t('navigation.users') }}</h2>
        <p class="section-sub">{{ $t('users.subTitle') }}</p>
      </div>
      <button class="add-btn" @click="$emit('openAddUser')">+ {{ $t('users.addNew') }}</button>
    </div>

    <div class="panel">
      <div v-if="users.length === 0" class="empty-state">{{ $t('users.noUsers') }}</div>
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>{{ $t('auth.firstName') }}</th>
            <th>{{ $t('auth.email') }}</th>
            <th>{{ $t('users.role') }}</th>
            <th>{{ $t('dashboard.activeBorrows') }}</th>
            <th>{{ $t('inventory.status') }}</th>
            <th>{{ $t('common.active') }}</th>
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
defineProps({
  users: Array,
})

defineEmits(['openAddUser', 'deleteUser', 'changeRole'])
</script>