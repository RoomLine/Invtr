<template>
  <div class="view-section">
    <div class="section-header">
      <div>
        <h2 class="section-title">Inventory</h2>
        <p class="section-sub">Manage all equipment and assets</p>
      </div>
      <button class="add-btn" @click="$emit('openAddItem')">+ Add New Item</button>
    </div>

    <div class="filter-bar">
      <select class="filter-select" :value="filterCategory" @change="$emit('update:filterCategory', $event.target.value)">
        <option value="">All Categories</option>
        <option value="Electronics">Electronics</option>
        <option value="Utility">Utility</option>
        <option value="Furniture">Furniture</option>
      </select>
      <select class="filter-select" :value="filterStatus" @change="$emit('update:filterStatus', $event.target.value)">
        <option value="">All Statuses</option>
        <option value="Available">Available</option>
        <option value="Checked-Out">Checked Out</option>
        <option value="Under-Repair">Under Repair</option>
        <option value="Retired">Retired</option>
      </select>
    </div>

    <div class="panel">
      <div v-if="filteredItems.length === 0" class="empty-state">No items found.</div>
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>Item Name</th>
            <th>Category</th>
            <th>Serial Number</th>
            <th>Status</th>
            <th>Condition</th>
            <th>Location</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredItems" :key="item.id">
            <td>
              <div class="item-name-cell">
                <span class="item-emoji">{{ item.emoji }}</span>
                <span>{{ item.name }}</span>
              </div>
            </td>
            <td>{{ item.category }}</td>
            <td class="mono-cell">{{ item.serial || '—' }}</td>
            <td><span class="status-badge" :class="statusClass(item.status)">{{ item.status.replace(/-/g, ' ') }}</span></td>
            <td><span class="cond-badge" :class="'cond-' + item.condition">{{ item.condition }}</span></td>
            <td class="location-cell">{{ item.location || '—' }}</td>
            <td>
              <div class="action-btns">
                <button class="act-btn act-edit" @click="$emit('editItem', item)">Edit</button>
                <button class="act-btn act-delete" @click="$emit('deleteItem', item.id)">Delete</button>
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
  filteredItems: Array,
  filterCategory: String,
  filterStatus: String,
})

defineEmits(['openAddItem', 'editItem', 'deleteItem', 'update:filterCategory', 'update:filterStatus'])

function statusClass(status) {
  return 'status-' + (status || '').replace(/\s+/g, '-')
}
</script>
