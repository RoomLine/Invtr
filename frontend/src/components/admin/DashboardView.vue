<template>
  <div class="view-section">
    <div class="stats-grid">
      <div class="stat-card stat-blue">
        <div class="stat-icon-wrap">💻</div>
        <div class="stat-body">
          <span class="stat-value">{{ totalItems }}</span>
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
          <button class="add-btn" @click="$emit('openAddItem')">+ Add New Item</button>
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
            <tr v-for="item in (searchedItems || []).slice(0, 6)" :key="item.id">
              <td>
                <div class="item-name-cell">
                  <span class="item-emoji">{{ item.emoji }}</span>
                  {{ item.name }}
                </div>
              </td>
              <td>{{ item.category }}</td>
              <td class="serial-cell">{{ item.serial }}</td>
              <td><span class="status-badge" :class="statusClass(item.status)">{{ (item.status || '').replace(/-/g, ' ') }}</span></td>
              <td>
                <div class="action-btns">
                  <button class="act-btn act-edit" @click="$emit('editItem', item)" title="Edit">✏️</button>
                  <button class="act-btn act-delete" @click="$emit('deleteItem', item.id)" title="Delete">🗑️</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="panel">
        <div class="panel-header">
          <h3>Recent Requests</h3>
          <button class="link-btn" @click="$emit('navigate', 'requests')">View all</button>
        </div>
        <div class="borrow-list">
          <div class="borrow-row" v-for="req in (requests || []).slice(0, 4)" :key="req.id">
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
              <div class="cat-bar-fill" :style="{ width: (count / totalItems * 100) + '%' }"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  searchedItems: Array,
  requests: Array,
  categoryBreakdown: Object,
  totalItems: Number,
  availableCount: Number,
  pendingCount: Number,
  repairCount: Number,
})

defineEmits(['openAddItem', 'editItem', 'deleteItem', 'navigate'])

function statusClass(status) {
  return 'status-' + (status || '').replace(/\s+/g, '-')
}
</script>