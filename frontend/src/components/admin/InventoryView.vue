<template>
  <div class="view-section">
    <div class="section-header">
      <div>
        <h2 class="section-title">{{ $t('navigation.inventory') }}</h2>
        <p class="section-sub">{{ $t('dashboard.overview') }}</p>
      </div>
      <button class="add-btn" @click="$emit('openAddItem')">+ {{ $t('inventory.addNew') }}</button>
    </div>

    <div class="filter-bar">
      <select class="filter-select" :value="filterCategory" @change="$emit('update:filterCategory', $event.target.value)">
        <option value="">{{ $t('inventory.category') }}: {{ $t('common.viewAll') }}</option>
        <option value="Electronics">{{ $t('inventory.categories.electronics') }}</option>
        <option value="Utility">{{ $t('inventory.categories.utility') }}</option>
        <option value="Furniture">{{ $t('inventory.categories.furniture') }}</option>
      </select>

      <select class="filter-select" :value="filterStatus" @change="$emit('update:filterStatus', $event.target.value)">
        <option value="">{{ $t('inventory.status') }}: {{ $t('common.viewAll') }}</option>
        <option value="Available">{{ $t('inventory.statuses.available') }}</option>
        <option value="Checked-Out">{{ $t('inventory.statuses.checkedOut') }}</option>
        <option value="Under-Repair">{{ $t('inventory.statuses.underRepair') }}</option>
        <option value="Retired">{{ $t('inventory.statuses.retired') }}</option>
      </select>
    </div>

    <div class="panel">
      <div v-if="filteredItems.length === 0" class="empty-state">{{ $t('dashboard.noData') }}</div>
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>{{ $t('inventory.itemName') }}</th>
            <th>{{ $t('inventory.category') }}</th>
            <th>{{ $t('inventory.serialNumber') }}</th>
            <th>{{ $t('inventory.status') }}</th>
            <th>{{ $t('inventory.condition') }}</th>
            <th>{{ $t('inventory.location') }}</th>
            <th>{{ $t('common.active') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredItems" :key="item.id">
            <td>
              <div class="item-name-cell">
                <div class="item-img-wrapper">
                  <img v-if="item.photoUrl" :src="item.photoUrl" class="item-actual-img" alt="item" />
                  <span v-else class="item-emoji">{{ item.emoji }}</span>
                </div>
                <span class="item-text-name">{{ item.name }}</span>
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
              <span class="cond-badge" :class="'cond-' + item.condition">
                {{ translateCondition(item.condition) }}
              </span>
            </td>
            <td class="location-cell">{{ item.location || '—' }}</td>
            <td>
              <div class="action-btns">
                <button class="act-btn act-edit" @click="$emit('editItem', item)">{{ $t('common.save') }}</button>
                <button class="act-btn act-delete" @click="$emit('deleteItem', item.id)">{{ $t('common.remove') }}</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

defineProps({
  filteredItems: Array,
  filterCategory: String,
  filterStatus: String,
})

defineEmits(['openAddItem', 'editItem', 'deleteItem', 'update:filterCategory', 'update:filterStatus'])

// Мапинг функции за превод
function translateCategory(category) {
  const catMap = {
    'Electronics': 'inventory.categories.electronics',
    'Furniture': 'inventory.categories.furniture',
    'Utility': 'inventory.categories.utility'
  }
  return t(catMap[category] || category)
}

function translateStatus(status) {
  const statMap = {
    'Available': 'inventory.statuses.available',
    'Checked-Out': 'inventory.statuses.checkedOut',
    'Under-Repair': 'inventory.statuses.underRepair',
    'Retired': 'inventory.statuses.retired'
  }
  return t(statMap[status] || status)
}

function translateCondition(condition) {
  if (!condition) return '—'
  return t(`inventory.conditions.${condition.toLowerCase()}`)
}

function statusClass(status) {
  return 'status-' + (status || '').toLowerCase().replace(/[\s-]+/g, '-')
}
</script>