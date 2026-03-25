<template>
  <div class="view-section">
    <div class="stats-grid">
      <div class="stat-card stat-blue">
        <div class="stat-icon">💻</div>
        <div class="stat-body">
          <span class="stat-value">{{ totalItems }}</span>
          <span class="stat-label">{{ $t('dashboard.totalItems') }}</span>
        </div>
      </div>
      <div class="stat-card stat-green">
        <div class="stat-icon">✅</div>
        <div class="stat-body">
          <span class="stat-value">{{ availableCount }}</span>
          <span class="stat-label">{{ $t('dashboard.available') }}</span>
        </div>
      </div>
      <div class="stat-card stat-orange">
        <div class="stat-icon">⏳</div>
        <div class="stat-body">
          <span class="stat-value">{{ pendingCount }}</span>
          <span class="stat-label">{{ $t('dashboard.pendingRequests') }}</span>
        </div>
      </div>
      <div class="stat-card stat-red">
        <div class="stat-icon">🔧</div>
        <div class="stat-body">
          <span class="stat-value">{{ repairCount }}</span>
          <span class="stat-label">{{ $t('dashboard.underRepair') }}</span>
        </div>
      </div>
    </div>

    <div class="dashboard-grid">
      <div class="panel panel-wide">
        <div class="panel-header">
          <h3>{{ $t('dashboard.overview') }}</h3>
          <button class="add-btn" @click="$emit('openAddItem')">+ {{ $t('inventory.addNew') }}</button>
        </div>
        
        <div v-if="searchedItems.length === 0" class="empty-state">
          {{ $t('dashboard.noData') }}
        </div>
        
        <table v-else class="data-table">
          <thead>
            <tr>
              <th>{{ $t('inventory.itemName') }}</th>
              <th>{{ $t('inventory.category') }}</th>
              <th>{{ $t('inventory.serialNumber') }}</th>
              <th>{{ $t('inventory.status') }}</th>
              <th>{{ $t('common.active') }}</th> </tr>
          </thead>
          <tbody>
            <tr v-for="item in searchedItems.slice(0, 6)" :key="item.id">
              <td>
                <div class="item-name-cell">
                  <span class="item-emoji">{{ item.emoji }}</span>
                  <span>{{ item.name }}</span>
                </div>
              </td>
              <td>{{ translateCategory(item.category) }}</td>
              <td class="mono-cell">{{ item.serial || '—' }}</td>
              <td>
                <span class="status-badge" :class="statusClass(item.status)">
                  {{ translateStatus(item.status) }}
                </span>
              </td>
              <td>
                <div class="action-btns">
                  <button class="act-btn act-edit" @click="$emit('editItem', item)" :title="$t('inventory.editItem')">
                    ✎
                  </button>
                  <button class="act-btn act-delete" @click="$emit('deleteItem', item.id)" :title="$t('common.remove')">
                    🗑
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="panel-footer" v-if="searchedItems.length > 6">
          <button class="link-btn" @click="$emit('navigate', 'inventory')">
            {{ $t('common.viewAll') }} ({{ searchedItems.length }}) →
          </button>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <h3>{{ $t('dashboard.recentRequests') }}</h3>
          <button class="link-btn" @click="$emit('navigate', 'requests')">{{ $t('common.viewAll') }} →</button>
        </div>
        <div v-if="requests.length === 0" class="empty-state">{{ $t('dashboard.noRequests') }}</div>
        <div v-else class="borrow-list">
          <div class="borrow-row" v-for="req in requests.slice(0, 5)" :key="req.id">
            <div class="borrow-row-user">
              <div class="req-user-avatar">{{ req.user.charAt(0).toUpperCase() }}</div>
              <div class="borrow-info">
                <span class="borrow-name">{{ req.user }}</span>
                <span class="borrow-date">
                  {{ req.item }} · 
                  {{ req.status === 'pending' ? $t('dashboard.pendingRequests') : $t('navigation.history') }} 
                  {{ req.returnBy || '—' }}
                </span>
              </div>
            </div>
            <span class="status-badge" :class="statusClass(req.status)">
              {{ translateStatus(req.status) }}
            </span>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header"><h3>{{ $t('inventory.category') }}</h3></div>
        <div v-if="totalItems === 0" class="empty-state">{{ $t('dashboard.noData') }}</div>
        <div v-else class="category-list">
          <div v-for="(count, cat) in categoryBreakdown" :key="cat" class="cat-row">
            <div class="cat-header">
              <span class="cat-name">{{ translateCategory(cat) }}</span>
              <span class="cat-count">{{ count }}</span>
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
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

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

// Функция за динамични класове на статуса
function statusClass(status) {
  return 'status-' + (status || '').toLowerCase().replace(/[\s-]+/g, '-')
}

// Функция за превод на категорията от базата към JSON ключовете ни
function translateCategory(category) {
  const catMap = {
    'Electronics': 'inventory.categories.electronics',
    'Furniture': 'inventory.categories.furniture',
    'Utility': 'inventory.categories.utility'
  }
  return t(catMap[category] || category)
}

// Функция за превод на статуса от базата към JSON ключовете ни
function translateStatus(status) {
  const statMap = {
    'Available': 'inventory.statuses.available',
    'Checked-Out': 'inventory.statuses.checkedOut',
    'Under-Repair': 'inventory.statuses.underRepair',
    'Retired': 'inventory.statuses.retired',
    'pending': 'dashboard.pendingRequests'
  }
  return t(statMap[status] || status)
}
</script>