<template>
  <div class="view-section">
    <div class="section-header">
      <h2>📦 Inventory Management</h2>
      <button class="add-btn" @click="$emit('openAddItem')">+ Add New Item</button>
    </div>

    <div class="filter-bar">
      <select class="filter-select" :value="filterCategory" @change="$emit('update:filterCategory', $event.target.value)">
        <option value="">All Categories</option>
        <option value="Electronics">Electronics</option>
        <option value="Books">Books</option>
        <option value="Tools">Tools</option>
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
                <button class="act-btn act-duplicate" @click="$emit('duplicateItem', item)" title="Duplicate">📑</button>
                <button class="act-btn act-edit" @click="$emit('editItem', item)" title="Edit">✏️</button>
                <button class="act-btn act-delete" @click="$emit('deleteItem', item.id)" title="Delete">🗑️</button>
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
</template>

<script setup>
defineProps({
  filteredItems: Array,
  filterCategory: String,
  filterStatus: String,
})

const emit = defineEmits([
  'openAddItem', 
  'editItem', 
  'deleteItem', 
  'update:filterCategory', 
  'update:filterStatus',
  'duplicateItem' 
])

function statusClass(status) {
  return 'status-' + (status || '').replace(/\s+/g, '-')
}
</script>